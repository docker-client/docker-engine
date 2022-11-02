package de.gesellix.docker.context;

import com.squareup.moshi.Moshi;
import de.gesellix.docker.engine.DockerEnv;
import okio.Okio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

public class MetadataStore {
  private final static Logger log = LoggerFactory.getLogger(MetadataStore.class);

  public final static String metadataDir = "meta";
  final String metaFile = "meta.json";

  private final Moshi moshi = new Moshi.Builder().build();

  File root;
//  Config config;

  public MetadataStore(File root) {
    this.root = root;
  }

  public Metadata getMetadata(String contextName) {
    return getByID(getContextDir(contextName));
  }

  public Metadata getByID(String contextDirectory) {
    Map payload = getMetadataPayload(contextDirectory);
    Metadata metadata = new Metadata((String) payload.get("Name"));
    // TODO `metadata` should be read type safe
    // see https://github.com/docker/cli/blob/09c94c1c21cb2ed02d347934de85b6163dc62ddf/cli/context/store/metadatastore.go#L83
    metadata.setMetadata(payload.get("Metadata"));
    // TODO each `endpoint` should be read type safe
    // see https://github.com/docker/cli/blob/09c94c1c21cb2ed02d347934de85b6163dc62ddf/cli/context/store/metadatastore.go#L87
    metadata.getEndpoints().putAll((Map<String, Object>) payload.get("Endpoints"));
    if (metadata.getEndpoints().containsKey(DockerEnv.dockerEndpointDefaultName)) {
      Map<String, Object> endpointMeta = (Map<String, Object>) metadata.getEndpoints().get(DockerEnv.dockerEndpointDefaultName);
      metadata.getEndpoints().put(
          DockerEnv.dockerEndpointDefaultName,
          new EndpointMetaBase((String) endpointMeta.get("Host"), false));
    }
    return metadata;
  }

  // Code taken from https://stackoverflow.com/a/62401723/372019
  // SHA256 ist the current implementation in the docker/cli,
  // see https://github.com/docker/cli/blob/cd7c493ea2cfb8c6db0beb65cf9830c8df23a9f9/cli/context/store/store.go#L8
  public String getContextDir(String contextName) {
    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    byte[] hashBytes = messageDigest.digest(contextName.getBytes(StandardCharsets.UTF_8));
    BigInteger noHash = new BigInteger(1, hashBytes);
    String hashStr = noHash.toString(16);
    return hashStr;
  }

  private Map getMetadataPayload(String contextDirectory) {
    File contextMetadata = new File(new File(root, contextDirectory), metaFile);
    if (!contextMetadata.exists()) {
      throw new IllegalStateException("context does not exist", new FileNotFoundException(contextMetadata.getAbsolutePath()));
    }
    try {
      return moshi.adapter(Map.class).fromJson(Okio.buffer(Okio.source(contextMetadata)));
    } catch (Exception e) {
      log.debug(MessageFormat.format("failed to read metadata from {}", contextMetadata), e);
      return Collections.emptyMap();
    }
  }
}
