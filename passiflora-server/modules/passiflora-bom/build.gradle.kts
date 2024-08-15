plugins {
    id("java-platform")
}

dependencies {
    constraints {
        api("commons-httpclient:commons-httpclient:3.1")
        api("org.liquibase:liquibase-core:4.25.1")
        api("org.liquibase:liquibase-groovy-dsl:3.0.3")
        api("info.picocli:picocli:4.7.5")
        api("org.postgresql:postgresql:42.2.27")
        api("org.projectlombok:lombok:1.18.30")
        api("cn.hutool:hutool-core:5.8.27")
        api("cn.hutool:hutool-db:5.8.27")
        api("cn.hutool:hutool-extra:5.8.27")
        api("cn.hutool:hutool-crypto:5.8.27")
        api("cn.hutool:hutool-http:5.8.27")
        api("org.mapstruct:mapstruct:1.5.5.Final")
        api("org.mapstruct:mapstruct-processor:1.5.5.Final")
        api("org.projectlombok:lombok-mapstruct-binding:0.2.0")
        api("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.4.0")
        api("com.github.xiaoymin:knife4j-gateway-spring-boot-starter:4.4.0")
        api("io.swagger.core.v3:swagger-annotations-jakarta:2.2.20")
        api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.5")
        api("org.freemarker:freemarker:2.3.32")
        api("org.mybatis:mybatis:3.5.16")
        api("org.mybatis:mybatis-spring:3.0.3")
        api("com.baomidou:mybatis-plus-annotation:3.5.6")
        api("com.baomidou:mybatis-plus-extension:3.5.6")
        api("com.baomidou:mybatis-plus-boot-starter:3.5.6")
        api("org.mybatis:mybatis-typehandlers-jsr310:1.0.2")
        api("org.redisson:redisson-spring-boot-starter:3.27.2")
        api("org.redisson:redisson:3.27.2")
        api("io.minio:minio:8.5.10")
        api("org.apache.commons:commons-lang3:3.16.0")
        api("org.apache.commons:commons-collections4:4.4")
        api("commons-codec:commons-codec:1.17.1")
        api("com.google.guava:guava:33.2.1-jre")
    }
}

