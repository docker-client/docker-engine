import java.text.SimpleDateFormat
import java.util.*

rootProject.extra.set("artifactVersion", SimpleDateFormat("yyyy-MM-dd\'T\'HH-mm-ss").format(Date()))

buildscript {
    repositories {
        mavenLocal()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.github.ben-manes.versions") version "0.28.0"
    id("com.jfrog.bintray") version "1.8.4" apply false
    id("net.ossindex.audit") version "0.4.11"
    id("io.freefair.github.package-registry-maven-publish") version "4.1.6" // apply false
}

val dependencyVersions = listOf(
        "com.squareup.okio:okio:2.4.3",
        "junit:junit:4.13",
        "org.codehaus.groovy:groovy:2.5.9",
        "org.codehaus.groovy:groovy-json:2.5.9",
        "org.codehaus.groovy:groovy-macro:2.5.9",
        "org.codehaus.groovy:groovy-nio:2.5.9",
        "org.codehaus.groovy:groovy-sql:2.5.9",
        "org.codehaus.groovy:groovy-templates:2.5.9",
        "org.codehaus.groovy:groovy-test:2.5.9",
        "org.codehaus.groovy:groovy-xml:2.5.9",
        "org.slf4j:slf4j-api:1.7.30"
)

subprojects {
    configurations.all {
        resolutionStrategy {
            failOnVersionConflict()
            force(dependencyVersions)
        }
    }
}

fun findProperty(s: String) = project.findProperty(s) as String?

rootProject.github {
    slug.set("${project.property("github.package-registry.owner")}/${project.property("github.package-registry.repository")}")
    username.set(System.getenv("GITHUB_ACTOR") ?: findProperty("github.package-registry.username"))
    token.set(System.getenv("GITHUB_TOKEN") ?: findProperty("github.package-registry.password"))
}

tasks {
    wrapper {
        gradleVersion = "6.2.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}
