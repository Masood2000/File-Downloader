plugins {
    kotlin("jvm") version "2.1.10"
}

group = "com.masood"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")




}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}



