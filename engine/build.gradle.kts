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
        strictly("[1.7,1.8)")
        prefer("1.7.30")
      }
    }
    listOf(
      "com.squareup.okhttp3:mockwebserver",
      "com.squareup.okhttp3:okhttp"
    ).onEach {
      implementation(it) {
        version {
          strictly("[4,5)")
          prefer("4.9.1")
        }
      }
    }
    implementation("com.squareup.okio:okio") {
      version {
        strictly("[2.5,3)")
        prefer("2.10.0")
      }
    }
    listOf(
      "org.jetbrains.kotlin:kotlin-reflect",
      "org.jetbrains.kotlin:kotlin-stdlib",
      "org.jetbrains.kotlin:kotlin-stdlib-jdk8",
      "org.jetbrains.kotlin:kotlin-stdlib-common",
      "org.jetbrains.kotlin:kotlin-test"
    ).onEach {
      implementation(it) {
        version {
          strictly("[1.3,1.5)")
          prefer("1.3.72")
        }
      }
    }
    listOf(
      "org.codehaus.groovy:groovy",
      "org.codehaus.groovy:groovy-json"
    ).onEach {
      implementation(it) {
        version {
          strictly("[2.5,)")
          prefer("2.5.14")
        }
      }
    }
  }
  implementation("org.codehaus.groovy:groovy")

  implementation("com.squareup.moshi:moshi:1.12.0")

  implementation("org.slf4j:slf4j-api")
  testImplementation("ch.qos.logback:logback-classic:1.2.5")

  implementation("com.squareup.okio:okio")
  implementation("com.squareup.okhttp3:okhttp")
  testImplementation("com.squareup.okhttp3:mockwebserver")

  implementation("org.apache.commons:commons-compress:1.21")
  testImplementation("org.apache.commons:commons-lang3:3.12.0")

  implementation("de.gesellix:docker-filesocket:2021-06-06T17-29-35")
  testImplementation("de.gesellix:testutil:2021-04-21T23-29-14")

  implementation("org.bouncycastle:bcpkix-jdk15on:1.69")

  testImplementation("org.spockframework:spock-core:2.0-groovy-2.5")
  testImplementation("cglib:cglib-nodep:3.3.0")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
  withType(Test::class.java) {
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
