import org.apache.tools.ant.filters.ReplaceTokens
import java.util.*

plugins {
    java
    jacoco
    id("org.liquibase.gradle") version Version.liquibasePluginVersion
    id("org.springframework.boot") version Version.springBootVersion
}

val env: String = System.getProperty("env", Constants.DEL_ENV)
val projectVersion = project.version.toString()
val configMap = configMap("${project.rootDir}/config.yml", env, projectVersion)
val os = System.getProperty("os.name").lowercase(Locale.getDefault())
val arch = System.getProperty("os.arch").lowercase(Locale.getDefault())

dependencies {
    implementation(project(":modules:passiflora-feign"))
    implementation(project(":modules:passiflora-starter-mybatis-flex"))
    annotationProcessor(platform(project(":modules:passiflora-bom")))
    testAnnotationProcessor(platform(project(":modules:passiflora-bom")))
    liquibaseRuntime(platform(project(":modules:passiflora-bom")))

    if (os.contains("mac")) {
        if (arch.contains("aarch64")) {
            compileOnly(group = "io.netty", name = "netty-resolver-dns-native-macos", classifier = "osx-aarch_64")
        } else {
            compileOnly(group = "io.netty", name = "netty-resolver-dns-native-macos")
        }
    }

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    annotationProcessor("com.mybatis-flex:mybatis-flex-processor")
    testAnnotationProcessor("com.mybatis-flex:mybatis-flex-processor")

    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // 非开发环境，排除 Swagger(UI
    if (configMap["env"] == "dev" || configMap["env"] == "local") {
        implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter")
    } else {
        implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter") {
            exclude(group = "com.github.xiaoymin", module = "knife4j-openapi3-ui")
            exclude(group = "org.webjars", module = "swagger-ui")
        }
    }
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    implementation("com.mybatis-flex:mybatis-flex-spring-boot3-starter")
    implementation("org.redisson:redisson-spring-boot-starter")
    implementation("org.postgresql:postgresql")
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:apache-client")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.zaxxer:HikariCP")

    // liquibase
    implementation("org.liquibase:liquibase-core")
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("info.picocli:picocli")
    liquibaseRuntime("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:minio")
    testImplementation("com.redis:testcontainers-redis")

}

tasks {
    processResources {
        exclude("db/**")
        filter<ReplaceTokens>("tokens" to configMap)
    }
    processTestResources {
        from(sourceSets["test"].resources)
        from(sourceSets["main"].resources.srcDirs) {
            include("db/changelogs/**")
        }
        filter<ReplaceTokens>("tokens" to configMap)
    }
    compileJava {
        options.compilerArgs.add("-Xlint:deprecation")
        doFirst {
            print("> Task :${project.name}: 使用 $env 环境编译")
        }
        doLast {
            copy {
                from("src/main/java") {
                    include("**/*Mapper.xml")
                }
                into("build/classes/java/main")
            }
        }
    }
    test {
        useJUnitPlatform()
    }
    jar {
        enabled = false
    }
    liquibase {
        activities.register("main") {
            this.arguments = mapOf(
                "changelogFile" to "src/main/resources/db/main.yml",
                "url" to configMap["storage_app.database.url"],
                "username" to configMap["storage_app.database.username"],
                "password" to configMap["storage_app.database.password"],
                "logLevel" to "info"
            )
        }
    }
}