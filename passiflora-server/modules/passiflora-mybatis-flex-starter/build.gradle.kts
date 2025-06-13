plugins {
    id("java-library")
    jacoco
}

dependencies {
    api(platform(project(":modules:passiflora-bom")))
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

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    compileOnlyApi("org.projectlombok:lombok")

    api(project(":modules:passiflora-common"))
    api("com.mybatis-flex:mybatis-flex-spring-boot3-starter")
}

tasks {
    test {
        useJUnitPlatform()
    }
}