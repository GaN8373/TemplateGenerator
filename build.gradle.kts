repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

plugins {
    id("org.jetbrains.intellij.platform") version "2.3.0"
    kotlin("jvm")
}

group = "generator"
version = "1.0-SNAPSHOT"


dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
    intellijPlatform {
        rider("2024.3.6")
        bundledPlugins("com.intellij.database")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("243.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.freemarker:freemarker:2.3.32")
}
kotlin {
    jvmToolchain(17)
}