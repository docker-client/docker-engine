import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.internal.plugins.DslObject

buildscript {
    repositories {
        mavenLocal()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
    }
}

project.extra.set("bintrayDryRun", false)

plugins {
    groovy
    maven
    `maven-publish`
    id("com.github.ben-manes.versions")
    id("net.ossindex.audit")
    id("com.jfrog.bintray")
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
    compile("org.codehaus.groovy:groovy:2.5.4")
    compile("org.codehaus.groovy:groovy-json:2.5.4")

    compile("org.slf4j:slf4j-api:1.7.25")
    testCompile("ch.qos.logback:logback-classic:1.2.3")

    compile("com.squareup.okio:okio:2.2.2")
    compile("com.squareup.okhttp3:okhttp:3.13.1")
    testCompile("com.squareup.okhttp3:mockwebserver:3.13.1")

    compile("org.apache.commons:commons-compress:1.18")

    compile("de.gesellix:docker-filesocket:2019-02-16T17-47-40")
    testCompile("de.gesellix:testutil:2019-02-16T17-54-28")

    compile("org.bouncycastle:bcpkix-jdk15on:1.61")

    testCompile("org.spockframework:spock-core:1.2-groovy-2.5")
    testCompile("cglib:cglib-nodep:3.2.10")
}

tasks {
    withType(Test::class.java) {
        useJUnit()

        // minimal way of providing a special environment variable for the EnvFileParserTest
        environment("A_KNOWN_VARIABLE", "my value")
    }

    bintrayUpload {
        dependsOn("build")
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn("classes")
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

artifacts {
    add("archives", sourcesJar.get())
}

tasks.install {
    DslObject(repositories)
            .convention
            .getPlugin<MavenRepositoryHandlerConvention>()
            .mavenInstaller {
                pom {
                    groupId = "de.gesellix"
                    artifactId = "docker-engine"
                    version = "local"
                }
            }
}

val publicationName = "engineClient"
publishing {
    publications {
        register(publicationName, MavenPublication::class) {
            groupId = "de.gesellix"
            artifactId = "docker-engine"
            version = rootProject.extra["artifactVersion"] as String
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}

fun findProperty(s: String) = project.findProperty(s) as String?

bintray {
    user = System.getenv()["BINTRAY_USER"] ?: findProperty("bintray.user")
    key = System.getenv()["BINTRAY_API_KEY"] ?: findProperty("bintray.key")
    setPublications(publicationName)
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "docker-utils"
        name = "engine-client"
        desc = "Bare HTTP client for the Docker engine api"
        setLicenses("MIT")
        setLabels("docker", "engine api", "remote api", "http", "client", "java", "kotlin")
        version.name = rootProject.extra["artifactVersion"] as String
        vcsUrl = "https://github.com/docker-client/docker-engine.git"
    })
    dryRun = project.extra["bintrayDryRun"] as Boolean
}
