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
                        BuildTime    : { it == "06/26/2020 17:19:32" },
                        GitCommit    : { it == "0da829ac52" },
                        GoVersion    : { it == "go1.13.11" },
                        KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                        MinAPIVersion: { it == "1.24" },
                        Os           : { it == "windows" },
                        Version      : { it == "19.03.11" }]
            }
            else {
                versionDetails = [
                        ApiVersion   : { it == "1.40" },
                        Arch         : { it == "amd64" },
                        BuildTime    : { it == "2018-03-12T00:00:00.000000000+00:00" },
                        GitCommit    : { it == "9dc6525e6118a25fab2be322d1914740ea842495" },
                        GoVersion    : { it == "go1.13.11" },
                        KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                        MinAPIVersion: { it == "1.12" },
                        Os           : { it == "linux" },
                        Version      : { it == "19.03.12+azure" }]
            }
        }
        else if (LocalDocker.isNativeWindows()) {
            versionDetails = [
                    ApiVersion   : { it == "1.40" },
                    Arch         : { it == "amd64" },
                    BuildTime    : { it == "2020-06-22T15:57:30.000000000+00:00" },
                    GitCommit    : { it == "48a66213fe" },
                    GoVersion    : { it == "go1.13.10" },
                    KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                    MinAPIVersion: { it == "1.24" },
                    Os           : { it == "windows" },
                    Version      : { it == "19.03.12" }]
        }
        else {
            versionDetails = [
                    ApiVersion   : { it == "1.40" },
                    Arch         : { it == "amd64" },
                    BuildTime    : { it == "2020-06-22T15:49:27.000000000+00:00" },
                    GitCommit    : { it == "48a66213fe" },
                    GoVersion    : { it == "go1.13.10" },
                    KernelVersion: { it =~ "\\d.\\d{1,2}.\\d{1,2}(-\\w+)?" },
                    MinAPIVersion: { it == "1.12" },
                    Os           : { it == "linux" },
                    Version      : { it == "19.03.12" }]
        }
    }
}
