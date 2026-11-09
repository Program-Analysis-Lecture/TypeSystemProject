plugins {
    kotlin("jvm") version "2.4.21"
}

group = "dk.sdu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val sootUpVersion = "3.0.1"

dependencies {
    implementation("org.soot-oss:sootup.core:$sootUpVersion")
    implementation("org.soot-oss:sootup.java.core:$sootUpVersion")
    implementation("org.soot-oss:sootup.java.bytecode.frontend:$sootUpVersion")
    testImplementation(kotlin("test"))
}

// Usage: ./gradlew runSoot [--args="<classpath-dir> <class-name> [method-name]"]
tasks.register<JavaExec>("runSoot") {
    group = "application"
    description = "Lists the locals and casts of methods using SootUp's Jimple."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("soot.MethodInspectorKt")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}