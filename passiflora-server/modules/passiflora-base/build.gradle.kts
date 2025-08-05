plugins {
    java
    `java-library`
}

dependencies {
    api(enforcedPlatform(project(":modules:passiflora-bom")))
    annotationProcessor(enforcedPlatform(project(":modules:passiflora-bom")))
    testAnnotationProcessor(enforcedPlatform(project(":modules:passiflora-bom")))

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    annotationProcessor("com.mybatis-flex:mybatis-flex-processor")
    testAnnotationProcessor("com.mybatis-flex:mybatis-flex-processor")

    compileOnlyApi("org.projectlombok:lombok")
    compileOnlyApi("io.swagger.core.v3:swagger-annotations-jakarta")
    compileOnlyApi("jakarta.validation:jakarta.validation-api")
    compileOnlyApi("org.hibernate.validator:hibernate-validator")
    compileOnly("com.mybatis-flex:mybatis-flex-core")
    compileOnly("org.mapstruct:mapstruct")
}

tasks.test {
    useJUnitPlatform()
}