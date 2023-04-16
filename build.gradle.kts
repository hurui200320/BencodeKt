plugins {
    kotlin("jvm") version "1.8.20"
    `maven-publish`
}

group = "info.skyblond"
version = "1.0.0"

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
        create<MavenPublication>("Github") {
            groupId = "info.skyblond"
            artifactId = "bencodekt"
            version = rootProject.version.toString()

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/hurui200320/BencodeKt")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
