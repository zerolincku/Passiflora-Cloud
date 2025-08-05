plugins {
    java
    idea
    pmd
    `maven-publish`
    id("io.spring.dependency-management") version Version.springDependencyManagementPluginVersion
    id("com.diffplug.spotless") version Version.spotlessPluginVersion
}

allprojects {
    group = "com.zerolinck"
    version = Version.passifloraVersion
    apply(plugin = "idea")
    apply(plugin = "pmd")
    apply(plugin = "io.spring.dependency-management")

    tasks.withType<JavaCompile> { options.encoding = "UTF-8" }

    configurations.all {
        exclude(group = "commons-logging", module = "commons-logging")
    }

    repositories {
        huaweiRepository()
        aliyunCentral()
        tencentRepository()
        springRepository()
        mavenCentral()
        google()
    }

    dependencyManagement {
        applyMavenExclusions(false)
    }

    pmd {
        isConsoleOutput = true
        toolVersion = Version.pmdToolVersion
        rulesMinimumPriority = 5
        ruleSets(rootDir.absolutePath + "/pmd-rule.xml")
    }

    plugins.withId("java-library") {
        apply(plugin = "maven-publish")

        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])
                }
            }
        }
    }

    plugins.withId("java-platform") {
        apply(plugin = "maven-publish")

        publishing {
            publications {
                create<MavenPublication>("mavenJavaPlatform") {
                    from(components["javaPlatform"])
                }
            }
        }
    }
}

// code format
spotless {
    java {
        target("**/*.java")
        removeUnusedImports()
        palantirJavaFormat(Version.palantirJavaVersion).formatJavadoc(true)
        formatAnnotations()
        importOrder("java|javax|jakarta", "com|cn|org|io", "lombok")
        licenseHeader(header)
    }
    yaml {
        target("**/*.yml")
        jackson()
    }
}

tasks.compileJava { dependsOn("spotlessApply") }

var header =
    "/* \n" +
        " * Copyright (C) 2025 Linck. <zerolinck@foxmail.com>\n" +
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
        " */"
