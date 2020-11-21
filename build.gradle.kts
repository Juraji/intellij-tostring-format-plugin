plugins {
    id("org.jetbrains.intellij") version "0.6.4"
    kotlin("jvm") version "1.4.10"
}

group = "nl.juraji.intellij"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.2.3"
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
        * Added basic support for JSON formatting of Java's #toString() output. 
    """.trimIndent())
}
