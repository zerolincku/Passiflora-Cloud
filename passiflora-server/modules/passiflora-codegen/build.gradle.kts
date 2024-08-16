plugins {
    java
}

dependencies {
    implementation(project(":modules:passiflora-common"))
    annotationProcessor(platform(project(":modules:passiflora-bom")))
    testAnnotationProcessor(platform(project(":modules:passiflora-bom")))

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding")

    runtimeOnly("org.postgresql:postgresql")
    implementation("cn.hutool:hutool-db:5.8.27")
    implementation("cn.hutool:hutool-extra:5.8.27")
    implementation("org.freemarker:freemarker")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-core")
}