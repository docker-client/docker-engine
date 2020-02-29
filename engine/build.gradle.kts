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
    id("io.freefair.github.package-registry-maven-publish")
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
    implementation("org.codehaus.groovy:groovy:2.5.9")
    implementation("org.codehaus.groovy:groovy-json:2.5.9")
    implementation("com.squareup.moshi:moshi:1.9.2")

    implementation("org.slf4j:slf4j-api:1.7.30")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("com.squareup.okio:okio:2.4.3")
    implementation("com.squareup.okhttp3:okhttp:4.4.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.4.0")

    implementation("org.apache.commons:commons-compress:1.20")

    implementation("de.gesellix:docker-filesocket:2020-02-29T17-48-11")
    testImplementation("de.gesellix:testutil:2020-02-29T17-37-38")

    implementation("org.bouncycastle:bcpkix-jdk15on:1.64")

    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
    testImplementation("cglib:cglib-nodep:3.3.0")
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
    archiveClassifier.set("sources")
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
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
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
