package de.gesellix.docker.engine

class TestConstants {

  final String imageRepo
  final String imageTag
  final String imageName

  final Map<String, Closure<Boolean>> versionDetails = [:]

  public static TestConstants CONSTANTS = new TestConstants()

  TestConstants() {
    imageRepo = "gesellix/echo-server"
    imageTag = "2024-12-22T16-35-00"
    imageName = "$imageRepo:$imageTag"

    versionDetails = [
        ApiVersion   : { it in ["1.43", "1.44", "1.45", "1.46", "1.47"] },
        Arch         : { it in ["amd64", "arm64"] },
        BuildTime    : { it =~ "\\d{4}-\\d{2}-\\d{2}T\\w+" },
        GitCommit    : { it =~ "\\w{6,}" },
        GoVersion    : { it =~ "go\\d+.\\d+.\\d+" },
        KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
        MinAPIVersion: { it in ["1.12", "1.24"] },
        Os           : { it == "linux" },
        Version      : { it == "master" || it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
    if (LocalDocker.isNativeWindows()) {
      versionDetails.Os = { it == "windows" }
    }
  }
}
