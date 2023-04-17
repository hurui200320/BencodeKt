plugins {
    kotlin("jvm") version "1.8.20"
    `maven-publish`
}

group = "info.skyblond"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.dampcake:bencode:1.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "info.skyblond"
            artifactId = "bencodekt"
            version = rootProject.version.toString()

            from(components["java"])
        }
    }
}
