package de.gesellix.docker.engine

class TestConstants {

  final String imageRepo
  final String imageTag
  final String imageName
  final String imageDigest

  final Map<String, Closure<Boolean>> versionDetails = [:]

  public static TestConstants CONSTANTS = new TestConstants()

  TestConstants() {
    if (LocalDocker.isNativeWindows()) {
      imageDigest = "sha256:9f33e2a85c7238da1205513c33b4f813176c34dbd3069752500e2f9f12bdde98"
    }
    else {
      imageDigest = "sha256:9161d20871559b45e5afa19047ed0bfc1a0e2c6dfdd6a9488a2fd388fe28642d"
    }
    imageRepo = "gesellix/echo-server"
    imageTag = "2022-04-08T22-27-00";
    imageName = "$imageRepo:$imageTag"

    if (System.getenv("GITHUB_ACTOR")) {
      // TODO consider checking the Docker api version instead of "GITHUB_ACTOR"
      if (LocalDocker.isNativeWindows()) {
        versionDetails = [
            ApiVersion   : { it == "1.41" },
            Arch         : { it == "amd64" },
            BuildTime    : { it =~ "08/19/2021 \\w+" },
            GitCommit    : { it == "e1bf5b9c13" },
            GoVersion    : { it == "go1.13.15" },
            KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
            MinAPIVersion: { it == "1.24" },
            Os           : { it == "windows" },
            Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
      }
      else {
        versionDetails = [
            ApiVersion   : { it == "1.41" },
            Arch         : { it == "amd64" },
            BuildTime    : { it =~ "2022-03-24T\\w+" },
            GitCommit    : { it == "87a90dc786bda134c9eb02adbae2c6a7342fb7f6" },
            GoVersion    : { it == "go1.16.15" },
            KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
            MinAPIVersion: { it == "1.12" },
            Os           : { it == "linux" },
            Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
      }
    }
    else if (LocalDocker.isNativeWindows()) {
      versionDetails = [
          ApiVersion   : { it == "1.41" },
          Arch         : { it == "amd64" },
          BuildTime    : { it =~ "2021-12-13T\\w+" },
          GitCommit    : { it == "459d0df" },
          GoVersion    : { it == "go1.16.12" },
          KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
          MinAPIVersion: { it == "1.24" },
          Os           : { it == "windows" },
          Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
    }
    else {
      versionDetails = [
          ApiVersion   : { it == "1.41" },
          Arch         : { it == "amd64" },
          BuildTime    : { it =~ "2021-12-13T\\w+" },
          GitCommit    : { it == "459d0df" },
          GoVersion    : { it == "go1.16.12" },
          KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
          MinAPIVersion: { it == "1.12" },
          Os           : { it == "linux" },
          Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
    }
  }
}
