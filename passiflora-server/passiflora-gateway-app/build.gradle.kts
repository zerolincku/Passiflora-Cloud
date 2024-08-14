plugins {
    id("java")
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

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding")

    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    // 非开发环境，排除 Swagger UI
    if (configMap.get("env") == "dev") {
        implementation("com.github.xiaoymin:knife4j-gateway-spring-boot-starter:4.4.0")
    }
}

tasks {
    jar {
        enabled = false
    }
    processResources {
        include("**/*.yml")
        expand(configMap)
    }
}