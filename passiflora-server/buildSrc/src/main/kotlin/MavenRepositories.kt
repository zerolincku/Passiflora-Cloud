import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

fun RepositoryHandler.aliyunCentral() {
    maven { url = URI("https://maven.aliyun.com/repository/central") }
}

fun RepositoryHandler.tencentRepository() {
    maven { url = URI( "https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
}

fun RepositoryHandler.huaweiRepository() {
    maven { url = URI( "https://mirrors.huaweicloud.com/repository/maven/") }
}

fun RepositoryHandler.springRepository() {
    maven { url = URI( "https://repo.spring.io/milestone") }
}