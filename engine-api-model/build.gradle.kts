import java.text.SimpleDateFormat
import java.util.*

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm")
  id("org.jetbrains.kotlin.kapt")
  id("maven-publish")
  id("signing")
  id("com.github.ben-manes.versions")
  id("net.ossindex.audit")
  id("io.freefair.maven-central.validate-poms")
  id("org.openapi.generator")
  id("org.jlleitschuh.gradle.ktlint")
}

openApiGenerate {
  inputSpec.set(file("./docker-engine-api-v1.41.yaml").absolutePath)
  configFile.set(file("./openapi-generator-config.yaml").absolutePath)
  outputDir.set(file(".").absolutePath)
}
val openApiGenerateCleanup by tasks.register("openApiGenerateCleanup") {
  dependsOn(tasks.openApiGenerate)
  doLast {
    listOf(
      "build.gradle",
      "gradlew",
      "gradlew.bat",
      "settings.gradle"
    ).onEach {
      file(it).delete()
    }
    listOf(
      "gradle",
      "src/main/kotlin/de/gesellix/docker/engine/api",
      "src/main/kotlin/de/gesellix/docker/engine/client"
    ).onEach {
      file(it).deleteRecursively()
    }
  }
}
tasks.runKtlintFormatOverKotlinScripts.get().dependsOn(tasks.openApiGenerate, openApiGenerateCleanup)
tasks.ktlintKotlinScriptFormat.get().dependsOn(tasks.openApiGenerate, openApiGenerateCleanup)
tasks.ktlintMainSourceSetFormat.get().dependsOn(tasks.openApiGenerate, openApiGenerateCleanup)
tasks.ktlintFormat.get().dependsOn(tasks.openApiGenerate, openApiGenerateCleanup)
val updateApiModelSources by tasks.register("updateApiModelSources") {
  group = "openapi tools"
  dependsOn(tasks.ktlintFormat)
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.20")

  implementation("com.squareup.moshi:moshi:1.12.0")
  kapt("com.squareup.moshi:moshi-kotlin-codegen:1.12.0")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
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

val remoteApiVersion = "1.41"

fun findProperty(s: String) = project.findProperty(s) as String?

val isSnapshot = project.version == "unspecified"
val artifactVersion = if (!isSnapshot) project.version as String else SimpleDateFormat("yyyy-MM-dd\'T\'HH-mm-ss").format(Date())!!
val publicationName = "dockerApiModel"
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
        name.set("docker-api-model")
        description.set("API model for the Docker engine api")
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
      artifactId = "docker-api-model"
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
