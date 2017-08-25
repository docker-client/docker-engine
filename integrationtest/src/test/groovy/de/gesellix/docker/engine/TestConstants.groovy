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
        } else {
            imageRepo = "gesellix/testimage"
            imageTag = "os-linux"
            imageDigest = "sha256:0ce18ad10d281bef97fe2333a9bdcc2dbf84b5302f66d796fed73aac675320db"
        }
        imageName = "$imageRepo:$imageTag"

        // TODO consider checking the Docker api version instead of "TRAVIS"
        if (System.env.TRAVIS) {
            versionDetails = [
                    ApiVersion   : { it == "1.27" },
                    Arch         : { it == "amd64" },
                    BuildTime    : { it == "2017-03-27T17:10:36.401799458+00:00" },
                    GitCommit    : { it == "c6d412e" },
                    GoVersion    : { it == "go1.7.5" },
                    KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                    MinAPIVersion: { it == "1.12" },
                    Os           : { it == "linux" },
                    Version      : { it == "17.03.1-ce" }]
        } else if (LocalDocker.isNativeWindows()) {
            versionDetails = [
                    ApiVersion   : { it == "1.29" },
                    Arch         : { it == "amd64" },
                    BuildTime    : { it == "2017-05-04T21:43:09.220628248+00:00" },
                    GitCommit    : { it == "89658be" },
                    GoVersion    : { it == "go1.7.5" },
                    KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                    MinAPIVersion: { it == "1.24" },
                    Os           : { it == "windows" },
                    Version      : { it == "17.05.0-ce" }]
        } else {
            versionDetails = [
                    ApiVersion   : { it == "1.31" },
                    Arch         : { it == "amd64" },
                    BuildTime    : { it == "2017-08-22T22:30:35.498879141+00:00" },
                    GitCommit    : { it == "fe143e3" },
                    GoVersion    : { it == "go1.8.3" },
                    KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                    MinAPIVersion: { it == "1.12" },
                    Os           : { it == "linux" },
                    Version      : { it == "17.07.0-ce-rc4" }]
        }
    }
}
