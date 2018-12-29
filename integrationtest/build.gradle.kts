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
    compile(project(":engine"))

    testCompile("net.jodah:failsafe:1.1.1")

    testRuntime("ch.qos.logback:logback-classic:1.2.3")

    testCompile("de.gesellix:testutil:2018-12-29T16-12-32")
    testCompile("org.spockframework:spock-core:1.2-groovy-2.5")
    testCompile("cglib:cglib-nodep:3.2.10")
    testCompile("joda-time:joda-time:2.10.1")
    testCompile("ch.qos.logback:logback-classic:1.2.3")
}
tasks.check.get().shouldRunAfter(project(":engine").tasks.check)

tasks {
    withType(Test::class.java) {
        useJUnit()
    }
}
