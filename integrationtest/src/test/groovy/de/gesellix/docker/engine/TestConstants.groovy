package de.gesellix.docker.engine

class TestConstants {

  final String imageRepo
  final String imageTag
  final String imageName

  final Map<String, Closure<Boolean>> versionDetails = [:]

  public static TestConstants CONSTANTS = new TestConstants()

  TestConstants() {
    imageRepo = "gesellix/echo-server"
    imageTag = "2023-07-02T12-00-00"
    imageName = "$imageRepo:$imageTag"

    if (System.getenv("GITHUB_ACTOR")) {
      // TODO consider checking the Docker api version instead of "GITHUB_ACTOR"
      if (LocalDocker.isNativeWindows()) {
        versionDetails = [
            ApiVersion   : { it == "1.43" },
            Arch         : { it in ["amd64", "arm64"] },
            BuildTime    : { it =~ "2023-\\d{2}-\\d{2}T\\w+" },
            GitCommit    : { it =~ "\\w{6,}" },
            GoVersion    : { it =~ "go\\d+.\\d+.\\d+" },
            KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
            MinAPIVersion: { it == "1.24" },
            Os           : { it == "windows" },
            Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
      }
      else {
        versionDetails = [
            ApiVersion   : { it == "1.43" },
            Arch         : { it in ["amd64", "arm64"] },
            BuildTime    : { it =~ "2023-\\d{2}-\\d{2}T\\w+" },
            GitCommit    : { it =~ "\\w{6,}" },
            GoVersion    : { it =~ "go\\d+.\\d+.\\d+" },
            KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
            MinAPIVersion: { it == "1.12" },
            Os           : { it == "linux" },
            Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
      }
    }
    else if (LocalDocker.isNativeWindows()) {
      versionDetails = [
          ApiVersion   : { it == "1.43" },
          Arch         : { it in ["amd64", "arm64"] },
          BuildTime    : { it =~ "2023-05-25T\\w+" },
          GitCommit    : { it == "659604f9" },
          GoVersion    : { it =~ "go\\d+.\\d+.\\d+" },
          KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
          MinAPIVersion: { it == "1.24" },
          Os           : { it == "windows" },
          Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
    }
    else {
      versionDetails = [
          ApiVersion   : { it == "1.44" },
          Arch         : { it in ["amd64", "arm64"] },
          BuildTime    : { it =~ "2023-08-31T\\w+" },
          GitCommit    : { it == "8dfaf0c780630c4f3a6ee51b324bdf50b36e2bb3" },
          GoVersion    : { it =~ "go\\d+.\\d+.\\d+" },
          KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}\\w*" },
          MinAPIVersion: { it == "1.12" },
          Os           : { it == "linux" },
          Version      : { it =~ "\\d{1,2}.\\d{1,2}.\\d{1,2}\\w*" }]
    }
  }
}
