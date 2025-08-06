import com.jetbrains.plugin.structure.base.problems.MAX_CHANGE_NOTES_LENGTH

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
version = "3.1.2-release"


dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.0")

    intellijPlatform {
        // build version
        rider("2023.2")
        // debug version
//        rider("2024.3.6")
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
        sinceBuild.set("223")
        untilBuild.set("252.*")
        val changelog =file("CHANGELOG.md").readText()
        changeNotes.set(changelog.substring(0, minOf(MAX_CHANGE_NOTES_LENGTH, changelog.length)))
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