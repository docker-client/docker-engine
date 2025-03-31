import java.text.SimpleDateFormat
import java.util.*

plugins {
  id("groovy")
  id("maven-publish")
  id("signing")
  id("com.github.ben-manes.versions")
  id("net.ossindex.audit")
  id("io.freefair.maven-central.validate-poms")
}

repositories {
  mavenCentral()
}

dependencies {
  constraints {
    implementation("org.slf4j:slf4j-api") {
      version {
        strictly(libs.versions.slf4jVersionrange.get())
        prefer(libs.versions.slf4j.get())
      }
    }
    implementation("com.squareup.moshi:moshi") {
      version {
        strictly(libs.versions.moshiVersionrange.get())
        prefer(libs.versions.moshi.get())
      }
    }
    listOf(
      "com.squareup.okhttp3:okhttp"
    ).forEach {
      implementation(it) {
        version {
          strictly(libs.versions.okhttpVersionrange.get())
          prefer(libs.versions.okhttp.get())
        }
      }
    }
    listOf(
      libs.bundles.okio
    ).forEach {
      implementation(it) {
        version {
          strictly(libs.versions.okioVersionrange.get())
          prefer(libs.versions.okio.get())
        }
      }
    }
    listOf(
      libs.bundles.kotlin
    ).forEach {
      implementation(it) {
        version {
          strictly(libs.versions.kotlinVersionrange.get())
          prefer(libs.versions.kotlin.get())
        }
      }
    }
  }

  implementation(libs.moshi)

  implementation(libs.slf4j)
  testImplementation("ch.qos.logback:logback-classic:${libs.versions.logbackVersionrange.get()}!!${libs.versions.logback.get()}")

  implementation(libs.okio)
  implementation(libs.okhttp)
  testImplementation(libs.okhttpMockwebserver)

  implementation("org.apache.commons:commons-compress:1.27.1")
  testImplementation("org.apache.commons:commons-lang3:3.17.0")

  implementation("de.gesellix:docker-filesocket:2025-01-18T12-53-00")
  testImplementation("de.gesellix:testutil:2025-01-18T12-52-00")

  implementation("org.bouncycastle:bcpkix-jdk18on:1.80")

  testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
  testRuntimeOnly("net.bytebuddy:byte-buddy:1.17.5")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}

tasks {
  withType<JavaCompile> {
    options.encoding = "UTF-8"
  }
  withType<Test> {
    useJUnitPlatform()

    // minimal way of providing a special environment variable for the EnvFileParserTest
    environment("A_KNOWN_VARIABLE", "my value")
  }
}

val javadocJar by tasks.registering(Jar::class) {
  dependsOn("classes")
  archiveClassifier.set("javadoc")
  from(tasks.javadoc)
}

val sourcesJar by tasks.registering(Jar::class) {
  dependsOn("classes")
  archiveClassifier.set("sources")
  from(sourceSets.main.get().allSource)
}

artifacts {
  add("archives", sourcesJar.get())
  add("archives", javadocJar.get())
}

fun findProperty(s: String) = project.findProperty(s) as String?

val isSnapshot = project.version == "unspecified"
val artifactVersion = if (!isSnapshot) project.version as String else SimpleDateFormat("yyyy-MM-dd\'T\'HH-mm-ss").format(Date())!!
val publicationName = "dockerEngine"
publishing {
  repositories {
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/${property("github.package-registry.owner")}/${property("github.package-registry.repository")}")
      credentials {
        username = System.getenv("GITHUB_ACTOR") ?: findProperty("github.package-registry.username")
        password = System.getenv("GITHUB_TOKEN") ?: findProperty("github.package-registry.password")
      }
    }
  }
  publications {
    register(publicationName, MavenPublication::class) {
      pom {
        name.set("docker-engine")
        description.set("Bare HTTP client for the Docker engine api")
        url.set("https://github.com/docker-client/docker-engine")
        licenses {
          license {
            name.set("MIT")
            url.set("https://opensource.org/licenses/MIT")
          }
        }
        developers {
          developer {
            id.set("gesellix")
            name.set("Tobias Gesellchen")
            email.set("tobias@gesellix.de")
          }
        }
        scm {
          connection.set("scm:git:github.com/docker-client/docker-engine.git")
          developerConnection.set("scm:git:ssh://github.com/docker-client/docker-engine.git")
          url.set("https://github.com/docker-client/docker-engine")
        }
      }
      artifactId = "docker-engine"
      version = artifactVersion
      from(components["java"])
      artifact(sourcesJar.get())
      artifact(javadocJar.get())
    }
  }
}

signing {
  val signingKey: String? by project
  val signingPassword: String? by project
  useInMemoryPgpKeys(signingKey, signingPassword)
  sign(publishing.publications[publicationName])
}
