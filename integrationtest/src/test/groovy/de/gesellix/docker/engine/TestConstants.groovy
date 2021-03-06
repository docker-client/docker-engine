package de.gesellix.docker.engine

class TestConstants {

  final String imageRepo
  final String imageTag
  final String imageName
  final String imageDigest

  final Map<String, Closure<Boolean>> versionDetails = [:]

  static TestConstants CONSTANTS = new TestConstants()

  TestConstants() {
    if (LocalDocker.isNativeWindows()) {
      imageRepo = "gesellix/testimage"
      imageTag = "os-windows"
      imageDigest = "sha256:fd9e2bfa5acf34d40971f7749fcb560f3ef4423a814218055e5d124579ce7bd0"
      //imageDigest = "sha256:ad668e7a31ddd5df9fa481b983df0ea300045da865179cfe058503c6ef16237d"
    }
    else {
      imageRepo = "gesellix/testimage"
      imageTag = "os-linux"
      imageDigest = "sha256:0ce18ad10d281bef97fe2333a9bdcc2dbf84b5302f66d796fed73aac675320db"
    }
    imageName = "$imageRepo:$imageTag"

    if (System.getenv("GITHUB_ACTOR")) {
      // TODO consider checking the Docker api version instead of "GITHUB_ACTOR"
      if (LocalDocker.isNativeWindows()) {
        versionDetails = [
            ApiVersion   : { it == "1.41" },
            Arch         : { it == "amd64" },
            BuildTime    : { it =~ "04/12/2021 \\w+" },
            GitCommit    : { it == "b24528647a" },
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
            BuildTime    : { it =~ "2021-04-09T\\w+" },
            GitCommit    : { it == "8728dd246c3ab53105434eef8ffe997b6fd14dc6" },
            GoVersion    : { it == "go1.13.15" },
            KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
            MinAPIVersion: { it == "1.12" },
            Os           : { it == "linux" },
            Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
      }
    }
    else if (LocalDocker.isNativeWindows()) {
      versionDetails = [
          ApiVersion   : { it == "1.40" },
          Arch         : { it == "amd64" },
          BuildTime    : { it =~ "2021-09-16T\\w+" },
          GitCommit    : { it == "4484c46d9d" },
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
          BuildTime    : { it =~ "2021-06-02T\\w+" },
          GitCommit    : { it == "b0f5bc3" },
          GoVersion    : { it == "go1.13.15" },
          KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
          MinAPIVersion: { it == "1.12" },
          Os           : { it == "linux" },
          Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
    }
  }
}
