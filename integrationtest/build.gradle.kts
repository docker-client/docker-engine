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

    testRuntime("ch.qos.logback:logback-classic:1.2.3")

    testCompile("de.gesellix:testutil:2019-02-16T17-54-28")
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
