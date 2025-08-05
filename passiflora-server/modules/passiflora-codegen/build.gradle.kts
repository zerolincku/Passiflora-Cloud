import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    `java-library`
}

val env: String = System.getProperty("env", Constants.DEL_ENV)
val projectVersion = project.version.toString()
val configMap = configMap("${project.rootDir}/config.yml", env, projectVersion)

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
    implementation("org.yaml:snakeyaml")
    implementation("commons-dbutils:commons-dbutils")
    implementation("com.zaxxer:HikariCP")
    implementation("org.freemarker:freemarker")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("org.slf4j:slf4j-jdk14")
    implementation("org.yaml:snakeyaml")
}

tasks {
    processResources {
        filter<ReplaceTokens>("tokens" to configMap)
    }
}