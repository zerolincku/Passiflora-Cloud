plugins {
    java
    id("java-library")
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

    compileOnlyApi("org.projectlombok:lombok")
    compileOnly("io.swagger.core.v3:swagger-annotations-jakarta")
    compileOnly("jakarta.validation:jakarta.validation-api")
    compileOnly("org.hibernate.validator:hibernate-validator")
    compileOnly("com.baomidou:mybatis-plus-annotation")
    api("org.mybatis:mybatis")
    api("org.mapstruct:mapstruct")
}

tasks.test {
    useJUnitPlatform()
}