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
          prefer("4.9.0")
        }
      }
    }
    implementation("com.squareup.okio:okio") {
      version {
        strictly("[2.5,3)")
        prefer("2.8.0")
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
          strictly("[2.5,3)")
          prefer("2.5.13")
        }
      }
    }
  }
  implementation(project(":engine"))
  testImplementation("com.squareup.okhttp3:okhttp")

  testImplementation("org.slf4j:slf4j-api")
  testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")

  testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
  testImplementation("cglib:cglib-nodep:3.3.0")
}
tasks.check.get().shouldRunAfter(project(":engine").tasks.check)

tasks {
  withType(Test::class.java) {
    useJUnit()
  }
}
