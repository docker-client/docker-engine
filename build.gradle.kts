import java.text.SimpleDateFormat
import java.util.*

rootProject.extra.set("artifactVersion", SimpleDateFormat("yyyy-MM-dd\'T\'HH-mm-ss").format(Date()))

plugins {
  id("maven-publish")
  id("com.github.ben-manes.versions") version "0.39.0"
  id("net.ossindex.audit") version "0.4.11"
  id("io.freefair.maven-central.validate-poms") version "5.3.3.3"
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
  id("org.jetbrains.kotlin.jvm") version "1.4.31" apply false
  id("org.jetbrains.kotlin.kapt") version "1.4.31" apply false
}

val dependencyVersions = listOf(
  "com.squareup.okio:okio:2.10.0",
  "org.jetbrains:annotations:21.0.1",
  "org.jetbrains.kotlin:kotlin-reflect:1.4.31",
  "org.jetbrains.kotlin:kotlin-stdlib:1.4.31",
  "org.jetbrains.kotlin:kotlin-stdlib-common:1.4.31",
  "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.5.10",
  "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.31"
)

val dependencyGroupVersions = mapOf(
  "org.junit.jupiter" to "5.7.2",
  "org.junit.platform" to "1.7.2"
)

subprojects {
  configurations.all {
    resolutionStrategy {
      failOnVersionConflict()
      force(dependencyVersions)
      eachDependency {
        val forcedVersion = dependencyGroupVersions[requested.group]
        if (forcedVersion != null) {
          useVersion(forcedVersion)
        }
      }
    }
  }
}

fun findProperty(s: String) = project.findProperty(s) as String?

val isSnapshot = project.version == "unspecified"
nexusPublishing {
  repositories {
    if (!isSnapshot) {
      sonatype {
        // 'sonatype' is pre-configured for Sonatype Nexus (OSSRH) which is used for The Central Repository
        stagingProfileId.set(System.getenv("SONATYPE_STAGING_PROFILE_ID") ?: findProperty("sonatype.staging.profile.id")) //can reduce execution time by even 10 seconds
        username.set(System.getenv("SONATYPE_USERNAME") ?: findProperty("sonatype.username"))
        password.set(System.getenv("SONATYPE_PASSWORD") ?: findProperty("sonatype.password"))
      }
    }
  }
}
