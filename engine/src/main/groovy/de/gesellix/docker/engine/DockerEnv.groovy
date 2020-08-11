package de.gesellix.docker.engine

/**
 * Configuration via environment variables should work like
 * described in the official <a href="https://docs.docker.com/engine/reference/commandline/cli/#environment-variables">cli docs</a>.
 */
class DockerEnv {

    String dockerHost

    int defaultTlsPort = 2376

    String tlsVerify = System.getProperty("docker.tls.verify", System.getenv("DOCKER_TLS_VERIFY") ?: "")

    String certPath = System.getProperty("docker.cert.path", System.getenv("DOCKER_CERT_PATH") ?: "")

    String defaultCertPath = new File(System.getProperty("user.home"), ".docker").absolutePath

    // the v1 registry still seems to be valid for authentication.
    String indexUrl_v1 = "https://index.docker.io/v1/"
    String indexUrl_v2 = "https://registry-1.docker.io"

    File configFile = new File("${System.getProperty('user.home')}/.docker", "config.json")

    File legacyConfigFile = new File("${System.getProperty('user.home')}", ".dockercfg")

    File dockerConfigFile = null

    String apiVersion = System.getProperty("docker.api.version", System.getenv("DOCKER_API_VERSION") ?: "")

    String tmpdir = System.getProperty("docker.tmpdir", System.getenv("DOCKER_TMPDIR") ?: "")

    String dockerContentTrust = System.getProperty("docker.content.trust", System.getenv("DOCKER_CONTENT_TRUST") ?: "")

    String contentTrustServer = System.getProperty("docker.content.trust.server", System.getenv("DOCKER_CONTENT_TRUST_SERVER") ?: "")

    String officialNotaryServer = "https://notary.docker.io"

    DockerEnv() {
        this.dockerHost = getDockerHostOrDefault()
    }

    static String getDockerHostOrDefault() {
        String configuredDockerHost = System.getProperty("docker.host", System.getenv("DOCKER_HOST") ?: "")
        if (configuredDockerHost) {
            return configuredDockerHost
        }
        else {
            if (System.properties["os.name"].toLowerCase().contains("windows")) {
                // default to non-tls http
                //return "tcp://localhost:2375"

                // or use a named pipe:
                return "npipe:////./pipe/docker_engine"
            }
            else {
                return "unix:///var/run/docker.sock"
            }
        }
    }

    void setDockerConfigFile(File dockerConfigFile) {
        this.dockerConfigFile = dockerConfigFile
    }

    File getDockerConfigFile() {
        if (dockerConfigFile == null) {
            String dockerConfig = System.getProperty("docker.config", System.getenv("DOCKER_CONFIG") ?: "")
            if (dockerConfig) {
                this.dockerConfigFile = new File(dockerConfig, 'config.json')
            }
            else if (configFile.exists()) {
                this.dockerConfigFile = configFile
            }
            else if (legacyConfigFile.exists()) {
                this.dockerConfigFile = legacyConfigFile
            }
        }
        return dockerConfigFile
    }
}
