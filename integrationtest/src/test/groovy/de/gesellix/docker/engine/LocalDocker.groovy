package de.gesellix.docker.engine

import groovy.util.logging.Slf4j

@Slf4j
class LocalDocker {

    static void main(String[] args) {
        log.debug available() ? "connection success" : "failed to connect"
    }

    static available() {
        try {
            return new OkDockerClient().get([path: "/_ping", timeout: 2000]).status.code == 200
        }
        catch (Exception e) {
            log.info("Docker not available", e)
            return false
        }
    }

    static hasSwarmMode() {
        try {
            def version = getDockerVersion()
            return version.major >= 1 && version.minor >= 12
        }
        catch (Exception e) {
            log.info("Docker not available", e)
            return false
        }
    }

    static supportsStack() {
        try {
            def version = getDockerVersion()
            return (version.major >= 1 && version.minor >= 13) || version.major >= 17
        }
        catch (Exception e) {
            log.info("Docker not available", e)
            return false
        }
    }

    static DockerVersion getDockerVersion() {
        try {
            def version = new OkDockerClient().get([path: "/version"]).content.Version as String
            return DockerVersion.parseDockerVersion(version)
        }
        catch (Exception e) {
            log.info("Docker not available", e)
            throw new RuntimeException(e)
        }
    }

    static boolean isNativeWindows() {
        try {
            def version = new OkDockerClient().get([path: "/version"])
            def arch = version.content.Arch as String
            def os = version.content.Os as String
            return "$os/$arch".toString() == "windows/amd64"
        }
        catch (Exception e) {
            log.info("Docker not available", e)
            throw new RuntimeException(e)
        }
    }

    static isNamedPipe() {
        def dockerHost = new DockerEnv().dockerHost
        return dockerHost.startsWith("npipe://")
    }

    static isUnixSocket() {
        def dockerHost = new DockerEnv().dockerHost
        return dockerHost.startsWith("unix://")
    }

    static isTcpSocket() {
        def dockerHost = new DockerEnv().dockerHost
        return dockerHost.startsWith("tcp://") || dockerHost.startsWith("http://") || dockerHost.startsWith("https://")
    }
}
