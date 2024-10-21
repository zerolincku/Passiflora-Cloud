import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    jacoco
    id("org.liquibase.gradle") version Version.liquibasePluginVersion
    id("org.springframework.boot") version Version.springBootVersion
}

configurations.all {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
}

val env: String = System.getProperty("env", "local")
println("> 使用 $env 环境编译")
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
    implementation("org.springframework.boot:spring-boot-starter-aop")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // 非开发环境，排除 Swagger UI
    if (configMap["env"] == "dev" || configMap["env"] == "local") {
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
    implementation("org.postgresql:postgresql")

    // liquibase
    implementation("org.liquibase:liquibase-core")
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl")
    liquibaseRuntime("info.picocli:picocli")
    liquibaseRuntime("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
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
                "url" to configMap["iam_app.database.url"],
                "username" to configMap["iam_app.database.username"],
                "password" to configMap["iam_app.database.password"],
                "logLevel" to "info"
            )
        }
    }
}
