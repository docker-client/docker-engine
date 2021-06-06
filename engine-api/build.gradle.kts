plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm")
  id("org.jetbrains.kotlin.kapt")
  id("com.github.ben-manes.versions")
}

version = "1.41.1"

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
//  implementation(project(":engine"))
  implementation(project(":engine-api-model"))
  implementation("de.gesellix:docker-filesocket:2021-06-06T17-29-35")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.31")
  implementation("com.squareup.moshi:moshi:1.12.0")
//  implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
  kapt("com.squareup.moshi:moshi-kotlin-codegen:1.12.0")
  implementation("com.squareup.okhttp3:okhttp:4.9.1")
  testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
}

tasks.withType(Test::class.java) {
  useJUnitPlatform()
}

//tasks.javadoc {
//  options.tags = ["http.response.details:a:Http Response Details"]
//}
