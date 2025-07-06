plugins {
  groovy
  id("com.github.ben-manes.versions")
  id("org.sonatype.gradle.plugins.scan")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}

repositories {
  maven {
    name = "githubPackages"
    url = uri("https://maven.pkg.github.com/gesellix/okhttp")
    // username and password (a personal Github access token) should be specified as
    // `githubPackagesUsername` and `githubPackagesPassword` Gradle properties or alternatively
    // as `ORG_GRADLE_PROJECT_githubPackagesUsername` and `ORG_GRADLE_PROJECT_githubPackagesPassword`
    // environment variables
    credentials(PasswordCredentials::class)
//    credentials {
//      username = System.getenv("GITHUB_ACTOR")
//      password = System.getenv("GITHUB_TOKEN")
//    }
  }
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
    listOf(
      libs.bundles.okhttp
    ).forEach {
      implementation(it) {
        version {
          strictly(libs.versions.okhttpVersionrange.get())
          prefer(libs.versions.okhttp.get())
        }
      }
    }
    implementation("com.squareup.okio:okio") {
      version {
        strictly(libs.versions.okioVersionrange.get())
        prefer(libs.versions.okio.get())
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
  implementation(project(":engine"))
  testImplementation(libs.okhttp)
  testImplementation(libs.slf4j)
  testRuntimeOnly(libs.logback)
  testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
  testRuntimeOnly("net.bytebuddy:byte-buddy:1.17.8")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.13.4")
}
tasks.check.get().shouldRunAfter(project(":engine").tasks.check)

tasks {
  withType<JavaCompile> {
    options.encoding = "UTF-8"
  }
  withType<Test> {
    useJUnitPlatform()
  }
}
