plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin.kapt)
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://reposilite.runicrealms.com/releases")
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.test)

    // Configuration and Injection
    implementation(libs.guice)
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.dataformat.yaml)

    // Velocity
    kapt(libs.velocity.api)
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)

    // Velagones
    compileOnly(libs.velagones.velocity)
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.build { dependsOn("shadowJar") }

tasks.shadowJar {
    archiveBaseName.set("nexus-velocity")
    relocate("com.fasterxml.jackson", "shadow.com.fasterxml.jackson")
}

val archiveName = "nexus-velocity"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.parent!!.group.toString()
            artifactId = archiveName
            version = project.parent!!.version.toString()
        }
    }
    repositories {
        maven {
            name = "reposilite"
            url = uri("https://reposilite.runicrealms.com/releases/")
            credentials {
                username = System.getenv("REPOSILITE_USERNAME")
                password = System.getenv("REPOSILITE_PASSWORD")
            }
        }
    }
}
