package de.gesellix.docker.context;

import de.gesellix.docker.engine.DockerConfigReader;
import de.gesellix.docker.engine.DockerEnv;

import java.util.Map;

public class DockerContextResolver {

  // see the original implementation at https://github.com/docker/cli/blob/de6020a240ff95c97150f07d7a0dd59981143868/cli/command/cli.go#L448
  public String resolveDockerContextName(DockerConfigReader dockerConfigReader) {
    String dockerHost = DockerEnv.getDockerHostFromSystemPropertyOrEnvironment();
    String dockerContext = DockerEnv.getDockerContextFromSystemPropertyOrEnvironment();
//    if (dockerContext != null && dockerHost != null) {
//      throw new IllegalStateException("Conflicting options: either specify --host or --context, not both");
//    }
    if (dockerContext != null) {
      return dockerContext;
    }
    if (dockerHost != null) {
      return DockerEnv.dockerDefaultContextName;
    }
    Map<String, Object> configFile = dockerConfigReader.readDockerConfigFile();
    if (configFile != null && configFile.containsKey("currentContext")) {
      // TODO ensure `currentContext` to be valid
      // _, err := contextstore.GetMetadata(config.CurrentContext)
      // if errdefs.IsNotFound(err) {
      //   return "", errors.Errorf("current context %q is not found on the file system, please check your config file at %s", config.CurrentContext, config.Filename)
      // }
      return (String) configFile.get("currentContext");
    }
    return DockerEnv.dockerDefaultContextName;
  }

  // see the original implementation at https://github.com/docker/cli/blob/de6020a240ff95c97150f07d7a0dd59981143868/cli/command/cli.go#L278
  public EndpointMetaBase resolveDockerEndpoint(ContextStore store, String contextName) {
    Metadata metadata = store.getMetadata(contextName);
    if (metadata == null || metadata.getEndpoints() == null || !metadata.getEndpoints().containsKey(DockerEnv.dockerEndpointDefaultName)) {
      throw new IllegalStateException("cannot find docker endpoint in context " + contextName);
    }
    if (!(metadata.getEndpoints().get(DockerEnv.dockerEndpointDefaultName) instanceof EndpointMetaBase)) {
      throw new IllegalStateException("endpoint " + DockerEnv.dockerEndpointDefaultName + " is not of type EndpointMetaBase");
//      throw new IllegalStateException("endpoint " + DockerEnv.dockerEndpointDefaultName + " is not of type EndpointMeta");
    }
    // TODO TLSData
    return (EndpointMetaBase) metadata.getEndpoints().get(DockerEnv.dockerEndpointDefaultName);
  }
}
