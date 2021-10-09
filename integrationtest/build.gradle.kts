plugins {
  groovy
  id("com.github.ben-manes.versions")
  id("net.ossindex.audit")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  mavenCentral()
}

dependencies {
  constraints {
    implementation("org.slf4j:slf4j-api") {
      version {
        strictly("[1.7,1.8)")
        prefer("1.7.32")
      }
    }
    listOf(
      "com.squareup.okhttp3:mockwebserver",
      "com.squareup.okhttp3:okhttp"
    ).onEach {
      implementation(it) {
        version {
          strictly("[4,5)")
          prefer("4.9.2")
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
          strictly("[1.3,1.6)")
          prefer("1.5.31")
        }
      }
    }
  }
  implementation(project(":engine"))
  testImplementation("com.squareup.okhttp3:okhttp")

  testImplementation("org.slf4j:slf4j-api")
  testRuntimeOnly("ch.qos.logback:logback-classic:1.2.6")

  testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
  testImplementation("cglib:cglib-nodep:3.3.0")
}
tasks.check.get().shouldRunAfter(project(":engine").tasks.check)

tasks {
  withType(Test::class.java) {
    useJUnitPlatform()
  }
}
