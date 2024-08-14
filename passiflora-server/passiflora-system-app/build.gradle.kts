import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    jacoco
    id("org.liquibase.gradle") version "2.2.1"
    id("org.springframework.boot") version "3.3.0"
}

configurations.all {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
}

val env = System.getProperty("env", "dev")
println("> 使用 ${env} 环境编译")
val projectVersion = project.version.toString()
val configMap = configMap("${project.rootDir}/config.yml", env, projectVersion)

dependencies {
    implementation(project(":modules:passiflora-feign"))
    annotationProcessor(platform(project(":modules:passiflora-bom")))
    testAnnotationProcessor(platform(project(":modules:passiflora-bom")))
    liquibaseRuntime(platform(project(":modules:passiflora-bom")))

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // 非开发环境，排除 Swagger UI
    if (configMap["env"] == "dev") {
        implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter")
    } else {
        implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter") {
            exclude(group = "com.github.xiaoymin", module = "knife4j-openapi3-ui")
            exclude(group = "org.webjars", module = "swagger-ui")
        }
    }
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    implementation("org.redisson:redisson-spring-boot-starter")
    implementation("com.baomidou:mybatis-plus-boot-starter")
    implementation("cn.hutool:hutool-crypto")
    implementation("org.postgresql:postgresql")

    // liquibase
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl")
    liquibaseRuntime("info.picocli:picocli")
    liquibaseRuntime("org.postgresql:postgresql")
}

tasks {
    processResources {
        include("**/*.yml")
        expand(configMap)
    }
    compileJava {
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
                "changeLogFile" to "src/main/resources/db/main.xml",
                "url" to configMap["storage_app_database_url"],
                "username" to configMap["storage_app_database_username"],
                "password" to configMap["storage_app_database_username"],
                "logLevel" to "info"
            )
        }
    }
}
