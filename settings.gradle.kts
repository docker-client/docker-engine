rootProject.name = "docker-engine"
include("engine", "integrationtest")

// https://docs.gradle.org/current/userguide/toolchains.html#sub:download_repositories
plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
