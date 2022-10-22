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
            ApiVersion   : { it == "1.42" },
            Arch         : { it == "amd64" },
            BuildTime    : { it =~ "2022-03-27T\\w+" },
            GitCommit    : { it == "8941dcfcc5" },
            GoVersion    : { it == "go1.18" },
            KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
            MinAPIVersion: { it == "1.24" },
            Os           : { it == "windows" },
            Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
      }
      else {
        versionDetails = [
            ApiVersion   : { it == "1.41" },
            Arch         : { it == "amd64" },
            BuildTime    : { it =~ "2022-09-08T\\w+" },
            GitCommit    : { it == "e42327a6d3c55ceda3bd5475be7aae6036d02db3" },
            GoVersion    : { it == "go1.18.7" },
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
          Arch         : { it == "amd64" },
          BuildTime    : { it =~ "2022-06-06T\\w+" },
          GitCommit    : { it == "a89b842" },
          GoVersion    : { it == "go1.17.11" },
          KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
          MinAPIVersion: { it == "1.12" },
          Os           : { it == "linux" },
          Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
    }
  }
}
