plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.yaml:snakeyaml:2.3")
}

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/central") }
    maven { url = uri( "https://maven.aliyun.com/repository/public") }
    mavenCentral()
    google()
}
