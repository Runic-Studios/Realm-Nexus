import com.ncorti.ktfmt.gradle.tasks.KtfmtCheckTask
import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.ktfmt)
}

group = "com.runicrealms.nexus"

version = "0.1.0"

repositories { mavenCentral() }

subprojects {
    repositories { mavenCentral() }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")

    kotlin { compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") } }

    java { toolchain.languageVersion.set(JavaLanguageVersion.of(21)) }
}

ktfmt {
    kotlinLangStyle()
    srcSetPathExclusionPattern = Regex(".*generated.*")
}

tasks.withType<KtfmtFormatTask>().configureEach {
    source = project.fileTree(rootDir)
    include("**/*.kt")
    exclude("**/generated/**")
}

tasks.withType<KtfmtCheckTask>().configureEach {
    source = project.fileTree(rootDir)
    include("**/*.kt")
    exclude("**/generated/**")
}
