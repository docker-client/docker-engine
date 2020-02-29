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

        if (System.env.TRAVIS) {
            // TODO consider checking the Docker api version instead of "TRAVIS"
            versionDetails = [
                    ApiVersion   : { it == "1.32" },
                    Arch         : { it == "amd64" },
                    BuildTime    : { it == "2017-09-26T22:41:20.000000000+00:00" },
                    GitCommit    : { it == "afdb6d4" },
                    GoVersion    : { it == "go1.8.3" },
                    KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                    MinAPIVersion: { it == "1.12" },
                    Os           : { it == "linux" },
                    Version      : { it == "17.09.0-ce" }]
        }
        else if (System.env.GITHUB_ACTOR) {
            // TODO consider checking the Docker api version instead of "GITHUB_ACTOR"
            if (LocalDocker.isNativeWindows()) {
                versionDetails = [
                        ApiVersion   : { it == "1.40" },
                        Arch         : { it == "amd64" },
                        BuildTime    : { it == "11/13/2019 07:58:51" },
                        GitCommit    : { it == "2ee0c57608" },
                        GoVersion    : { it == "go1.12.12" },
                        KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                        MinAPIVersion: { it == "1.24" },
                        Os           : { it == "windows" },
                        Version      : { it == "19.03.5" }]
            }
            else {
                versionDetails = [
                        ApiVersion   : { it == "1.40" },
                        Arch         : { it == "amd64" },
                        BuildTime    : { it == "2020-01-24T20:08:11.000000000+00:00" },
                        GitCommit    : { it == "ea84732a77" },
                        GoVersion    : { it == "go1.12.14" },
                        KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                        MinAPIVersion: { it == "1.12" },
                        Os           : { it == "linux" },
                        Version      : { it == "3.0.10+azure" }]
            }
        }
        else if (LocalDocker.isNativeWindows()) {
            versionDetails = [
                    ApiVersion   : { it == "1.40" },
                    Arch         : { it == "amd64" },
                    BuildTime    : { it == "2019-11-13T07:36:50.000000000+00:00" },
                    GitCommit    : { it == "633a0ea" },
                    GoVersion    : { it == "go1.12.12" },
                    KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                    MinAPIVersion: { it == "1.24" },
                    Os           : { it == "windows" },
                    Version      : { it == "19.03.5" }]
        }
        else {
            versionDetails = [
                    ApiVersion   : { it == "1.40" },
                    Arch         : { it == "amd64" },
                    BuildTime    : { it == "2019-11-13T07:29:19.000000000+00:00" },
                    GitCommit    : { it == "633a0ea" },
                    GoVersion    : { it == "go1.12.12" },
                    KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                    MinAPIVersion: { it == "1.12" },
                    Os           : { it == "linux" },
                    Version      : { it == "19.03.5" }]
        }
    }
}
