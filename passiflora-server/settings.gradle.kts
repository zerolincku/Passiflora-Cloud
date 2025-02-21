pluginManagement {
    repositories {
        maven { url = uri("https://mirrors.huaweicloud.com/repository/maven/")}
        maven { url = uri("https://maven.aliyun.com/repository/central")}
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")}
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

rootProject.name = "passiflora"

include("modules:passiflora-base")
include("modules:passiflora-common")
include("modules:passiflora-bom")
include("modules:passiflora-model")
include("modules:passiflora-feign")
include("modules:passiflora-codegen")
include("passiflora-gateway-app")
include("passiflora-iam-app")
include("passiflora-storage-app")
include("modules:passiflora-mybatis-flex-starter")
