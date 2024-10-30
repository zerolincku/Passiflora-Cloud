import org.apache.tools.ant.filters.ReplaceTokens
import java.util.*

plugins {
    java
    id("org.springframework.boot") version Version.springBootVersion
    id("org.graalvm.buildtools.native") version Version.graalvmBuildtoolsVersion
}

configurations.all {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
    exclude("commons-logging", "commons-logging")
}

val env: String = System.getProperty("env", Constans.DEL_ENV)
val projectVersion = project.version.toString()
val configMap = configMap("${project.rootDir}/config.yml", env, projectVersion)
val os = System.getProperty("os.name").lowercase(Locale.getDefault())
val arch = System.getProperty("os.arch").lowercase(Locale.getDefault())

dependencies {
    implementation(project(":modules:passiflora-feign"))
    annotationProcessor(platform(project(":modules:passiflora-bom")))
    testAnnotationProcessor(platform(project(":modules:passiflora-bom")))

    if (os.contains("mac")) {
        if (arch.contains("aarch64")) {
            compileOnly(group = "io.netty", name = "netty-resolver-dns-native-macos", classifier = "osx-aarch_64")
        } else {
            compileOnly(group = "io.netty", name = "netty-resolver-dns-native-macos")
        }
    }

    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding")

    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("com.github.ben-manes.caffeine:caffeine")

    // 非开发环境，排除 Swagger UI
    if (configMap["env"] == "dev" || configMap["env"] == "local") {
        implementation("com.github.xiaoymin:knife4j-gateway-spring-boot-starter")
    }
}

tasks {
    processResources {
        filter<ReplaceTokens>("tokens" to configMap)
    }
    compileJava {
        options.compilerArgs.add("-Xlint:deprecation")
        doFirst {
            print("> Task :${project.name}: 使用 $env 环境编译")
        }
    }
    jar {
        enabled = false
    }

}