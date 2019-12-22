buildscript {
    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
    }
}

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
    mavenLocal()
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(project(":engine"))

    testImplementation("org.slf4j:slf4j-api:1.7.29")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
    testImplementation("cglib:cglib-nodep:3.3.0")
    testImplementation("joda-time:joda-time:2.10.5")
}
tasks.check.get().shouldRunAfter(project(":engine").tasks.check)

tasks {
    withType(Test::class.java) {
        useJUnit()
    }
}
