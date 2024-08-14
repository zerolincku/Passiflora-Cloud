plugins{
    java
    idea
    id("io.spring.dependency-management") version "1.1.4"
    id("com.diffplug.spotless") version "6.25.0"
}

allprojects {
    group = "com.zerolinck"
    version = "0.1"
    apply(plugin = "idea")
    apply(plugin = "io.spring.dependency-management")

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    repositories {
        aliyunCentral()
        aliyunPublic()
        mavenCentral()
        google()
    }

    dependencyManagement{
        imports{
            mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:2023.0.1.0")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.1")
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.0")
        }
    }
}

spotless {
    java {
        target("**/*.java")
        importOrder()
        removeUnusedImports()
        cleanthat()
        palantirJavaFormat().formatJavadoc(true)
        formatAnnotations()
        licenseHeader("/* \n" +
                " * Copyright (C) 2024 Linck. <zerolinck@foxmail.com>\n" +
                " * \n" +
                " * This program is free software: you can redistribute it and/or modify\n" +
                " * it under the terms of the GNU Affero General Public License as published\n" +
                " * by the Free Software Foundation, either version 3 of the License, or\n" +
                " * (at your option) any later version.\n" +
                " * \n" +
                " * This program is distributed in the hope that it will be useful,\n" +
                " * but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                " * GNU Affero General Public License for more details.\n" +
                " * \n" +
                " * You should have received a copy of the GNU Affero General Public License\n" +
                " * along with this program.  If not, see <https://www.gnu.org/licenses/>.\n" +
                " */")
    }
}

tasks.compileJava {
    dependsOn("spotlessApply")
}