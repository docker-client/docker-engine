rootProject.name = "docker-engine"
include("engine")

// https://docs.gradle.org/current/userguide/toolchains.html#sub:download_repositories
plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
