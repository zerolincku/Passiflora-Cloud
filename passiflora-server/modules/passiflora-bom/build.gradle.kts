plugins {
    id("java-platform")
}

dependencies {
    constraints {
        api("org.slf4j:slf4j-jdk14:2.0.16")
        api("io.netty:netty-resolver-dns-native-macos:4.1.114.Final")
        api("info.picocli:picocli:4.7.6")
        api("org.mapstruct:mapstruct:1.6.2")
        api("org.mapstruct:mapstruct-processor:1.6.2")
        api("org.projectlombok:lombok-mapstruct-binding:0.2.0")
        api("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.4.0")
        api("com.github.xiaoymin:knife4j-gateway-spring-boot-starter:4.4.0")
        api("io.swagger.core.v3:swagger-annotations-jakarta:2.2.20")
        api("org.mybatis:mybatis:3.5.16")
        api("org.mybatis:mybatis-spring:3.0.3")
        api("com.baomidou:mybatis-plus-annotation:3.5.8")
        api("com.baomidou:mybatis-plus-extension:3.5.8")
        api("com.baomidou:mybatis-plus-boot-starter:3.5.8")
        api("org.mybatis:mybatis-typehandlers-jsr310:1.0.2")
        api("org.redisson:redisson-spring-boot-starter:3.37.0")
        api("org.redisson:redisson:3.37.0")
        api("org.apache.commons:commons-lang3:3.17.0")
        api("org.apache.commons:commons-collections4:4.4")
        api("commons-codec:commons-codec:1.17.1")
        api("commons-io:commons-io:2.17.0")
        api("org.apache.httpcomponents.client5:httpclient5:5.4")
        api("commons-dbutils:commons-dbutils:1.8.1")
        api("org.apache.velocity:velocity-engine-core:2.4.1")
        api("com.google.guava:guava:33.3.1-jre")
        api("software.amazon.awssdk:s3:2.28.23")
        api("software.amazon.awssdk:apache-client:2.28.24")
        api("com.redis:testcontainers-redis:2.2.2")
        api("org.jetbrains:annotations:26.0.1")
    }
}

