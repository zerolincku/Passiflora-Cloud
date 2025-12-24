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

dependencyResolutionManagement{
    // 策略设置：
    // FAIL_ON_PROJECT_REPOS (推荐): 强制所有项目只使用这里定义的仓库。如果在某个子项目里单独写了 repositories {}，构建会报错。这能保证仓库来源的统一和安全。
    // PREFER_SETTINGS: 这里定义的优先，但子项目也可以自己定义。
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

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
