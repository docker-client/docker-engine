plugins {
  groovy
  id("com.github.ben-manes.versions")
  id("net.ossindex.audit")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  constraints {
    implementation("org.slf4j:slf4j-api") {
      version {
        strictly("[1.7,3)")
        prefer("2.0.13")
      }
    }
    listOf(
      "com.squareup.okhttp3:mockwebserver",
      "com.squareup.okhttp3:okhttp"
    ).forEach {
      implementation(it) {
        version {
          strictly("[4,5)")
          prefer("4.12.0")
        }
      }
    }
    implementation("com.squareup.okio:okio") {
      version {
        strictly("[3,4)")
        prefer("3.9.0")
      }
    }
    listOf(
      "org.jetbrains.kotlin:kotlin-reflect",
      "org.jetbrains.kotlin:kotlin-stdlib",
      "org.jetbrains.kotlin:kotlin-stdlib-jdk7",
      "org.jetbrains.kotlin:kotlin-stdlib-jdk8",
      "org.jetbrains.kotlin:kotlin-stdlib-common",
      "org.jetbrains.kotlin:kotlin-test"
    ).forEach {
      implementation(it) {
        version {
          strictly("[1.6,1.10)")
          prefer("1.9.23")
        }
      }
    }
  }
  implementation(project(":engine"))
  testImplementation("com.squareup.okhttp3:okhttp:4.12.0")

  testImplementation("org.slf4j:slf4j-api:2.0.13")
  testRuntimeOnly("ch.qos.logback:logback-classic:[1.2,2)!!1.3.14")

  testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
  testRuntimeOnly("net.bytebuddy:byte-buddy:1.14.18")
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
