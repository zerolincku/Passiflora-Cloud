plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:${Version.springBootVersion}"))
    api(platform("org.springframework.cloud:spring-cloud-dependencies:${Version.springCloudVersion}"))
    api(platform("com.alibaba.cloud:spring-cloud-alibaba-dependencies:${Version.aliCloudVersion}"))

    constraints {
        api("org.slf4j:slf4j-jdk14:2.0.16")
        api("io.netty:netty-resolver-dns-native-macos:4.1.114.Final")
        api("info.picocli:picocli:4.7.6")
        api("org.mapstruct:mapstruct:1.6.3")
        api("org.mapstruct:mapstruct-processor:1.6.3")
        api("org.projectlombok:lombok-mapstruct-binding:0.2.0")
        api("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.5.0")
        api("com.github.xiaoymin:knife4j-gateway-spring-boot-starter:4.5.0")
        api("org.springdoc:springdoc-openapi-starter-common:2.8.8")
        api("org.springdoc:springdoc-openapi-starter-webmvc-api:2.8.8")
        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")
        api("io.swagger.core.v3:swagger-annotations-jakarta:2.2.20")
        api("com.mybatis-flex:mybatis-flex-core:1.11.1")
        api("com.mybatis-flex:mybatis-flex-spring-boot3-starter:1.11.1")
        api("com.mybatis-flex:mybatis-flex-processor:1.11.1")
        api("org.redisson:redisson-spring-boot-starter:3.50.0")
        api("org.redisson:redisson:3.50.0")
        api("org.apache.commons:commons-lang3:3.18.0")
        api("org.apache.commons:commons-collections4:4.4")
        api("commons-codec:commons-codec:1.17.1")
        api("commons-io:commons-io:2.17.0")
        api("org.apache.httpcomponents.client5:httpclient5:5.4")
        api("commons-dbutils:commons-dbutils:1.8.1")
        api("org.apache.velocity:velocity-engine-core:2.4.1")
        api("com.google.guava:guava:33.3.1-jre")
        api("software.amazon.awssdk:s3:2.28.23")
        api("software.amazon.awssdk:apache-client:2.28.24")
        api("com.redis:testcontainers-redis:2.2.4")
        api("org.jetbrains:annotations:26.0.1")
    }
}

