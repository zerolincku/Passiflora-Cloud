plugins {
    id("java-library")
    jacoco
}

dependencies {
    api(project(":modules:passiflora-model"))
    annotationProcessor(platform(project(":modules:passiflora-bom")))
    testAnnotationProcessor(platform(project(":modules:passiflora-bom")))

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:minio")
    api("com.redis:testcontainers-redis")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    compileOnlyApi("org.springframework:spring-context")
    compileOnlyApi("com.fasterxml.jackson.core:jackson-databind")
    compileOnlyApi("com.fasterxml.jackson.core:jackson-core")
    compileOnlyApi("org.springframework.boot:spring-boot-autoconfigure")
    compileOnlyApi("org.springframework:spring-web")
    compileOnlyApi("org.apache.tomcat.embed:tomcat-embed-core")
    compileOnlyApi("org.postgresql:postgresql")
    compileOnly("org.springframework:spring-test")
    compileOnly("io.swagger.core.v3:swagger-annotations-jakarta")
    compileOnly("org.testcontainers:postgresql")
    compileOnly("org.testcontainers:minio")
    compileOnly("com.redis:testcontainers-redis")

    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    api("org.apache.tomcat.embed:tomcat-embed-core")
    api("org.springframework.boot:spring-boot-starter-log4j2")
    api("org.springframework:spring-webmvc")
    api("org.springframework.data:spring-data-redis")
    api("com.baomidou:mybatis-plus-extension")
    api("org.redisson:redisson")
    api("org.apache.commons:commons-lang3")
    api("org.apache.commons:commons-collections4")
    api("commons-codec:commons-codec")
    api("commons-io:commons-io")
    api("com.google.guava:guava")
}

tasks {
    test {
        useJUnitPlatform()
    }
}