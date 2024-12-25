plugins {
    id("java-library")
}

configurations.all {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
}

dependencies {
    api(project(":modules:passiflora-model"))
    api(project(":modules:passiflora-common"))
    annotationProcessor(platform(project(":modules:passiflora-bom")))
    testAnnotationProcessor(platform(project(":modules:passiflora-bom")))

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding")

    api("org.springframework.cloud:spring-cloud-openfeign-core")
    api("io.swagger.core.v3:swagger-annotations-jakarta")
    api("io.github.openfeign:feign-core")
    implementation("org.apache.tomcat.embed:tomcat-embed-core")
}