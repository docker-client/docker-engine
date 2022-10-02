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
        strictly("[1.7,3)")
        prefer("1.7.36")
      }
    }
    listOf(
      "com.squareup.okhttp3:mockwebserver",
      "com.squareup.okhttp3:okhttp"
    ).onEach {
      implementation(it) {
        version {
          strictly("[4,5)")
          prefer("4.10.0")
        }
      }
    }
    implementation("com.squareup.okio:okio") {
      version {
        strictly("[3,4)")
        prefer("3.2.0")
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
          strictly("[1.5,1.8)")
          prefer("1.7.20")
        }
      }
    }
  }
  implementation(project(":engine"))
  testImplementation("com.squareup.okhttp3:okhttp:4.10.0")

  testImplementation("org.slf4j:slf4j-api:1.7.36")
  testRuntimeOnly("ch.qos.logback:logback-classic:[1.2,2)!!1.2.11")

  testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
  testImplementation("cglib:cglib-nodep:3.3.0")
}
tasks.check.get().shouldRunAfter(project(":engine").tasks.check)

tasks {
  withType(Test::class.java) {
    useJUnitPlatform()
  }
}
