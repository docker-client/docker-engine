package de.gesellix.docker.engine

class TestConstants {

  final String imageRepo
  final String imageTag
  final String imageName

  final Map<String, Closure<Boolean>> versionDetails = [:]

  public static TestConstants CONSTANTS = new TestConstants()

  TestConstants() {
    imageRepo = "gesellix/echo-server"
    imageTag = "2022-07-31T15-12-00"
    imageName = "$imageRepo:$imageTag"

    if (System.getenv("GITHUB_ACTOR")) {
      // TODO consider checking the Docker api version instead of "GITHUB_ACTOR"
      if (LocalDocker.isNativeWindows()) {
        versionDetails = [
            ApiVersion   : { it == "1.41" },
            Arch         : { it in ["amd64", "arm64"] },
            BuildTime    : { it =~ "2022-\\d{2}-\\d{2}T\\w+" },
            GitCommit    : { it =~ "\\w{6,}" },
            GoVersion    : { it == "go1.18.7" },
            KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
            MinAPIVersion: { it == "1.24" },
            Os           : { it == "windows" },
            Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
      }
      else {
        versionDetails = [
            ApiVersion   : { it == "1.41" },
            Arch         : { it in ["amd64", "arm64"] },
            BuildTime    : { it =~ "2022-\\d{2}-\\d{2}T\\w+" },
            GitCommit    : { it =~ "\\w{6,}" },
            GoVersion    : { it == "go1.18.9" },
            KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
            MinAPIVersion: { it == "1.12" },
            Os           : { it == "linux" },
            Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
      }
    }
    else if (LocalDocker.isNativeWindows()) {
      versionDetails = [
          ApiVersion   : { it == "1.41" },
          Arch         : { it in ["amd64", "arm64"] },
          BuildTime    : { it =~ "2022-06-06T\\w+" },
          GitCommit    : { it == "a89b842" },
          GoVersion    : { it == "go1.17.11" },
          KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
          MinAPIVersion: { it == "1.24" },
          Os           : { it == "windows" },
          Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
    }
    else {
      versionDetails = [
          ApiVersion   : { it == "1.41" },
          Arch         : { it in ["amd64", "arm64"] },
          BuildTime    : { it =~ "2022-10-25T\\w+" },
          GitCommit    : { it == "3056208" },
          GoVersion    : { it == "go1.18.7" },
          KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
          MinAPIVersion: { it == "1.12" },
          Os           : { it == "linux" },
          Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
    }
  }
}
