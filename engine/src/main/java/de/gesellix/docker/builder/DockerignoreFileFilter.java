package de.gesellix.docker.builder;

import de.gesellix.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DockerignoreFileFilter {

  private static final Logger log = LoggerFactory.getLogger(DockerignoreFileFilter.class);

  private final GlobsMatcher globsMatcher;

  public DockerignoreFileFilter(File base) {
    this(base, Collections.emptyList());
  }

  public DockerignoreFileFilter(File base, List<String> additionalExcludes) {
    List<String> dockerignore = getDockerignorePatterns(base);
    dockerignore.add(".dockerignore");
    dockerignore.addAll(additionalExcludes);
    try {
      dockerignore = relativize(dockerignore, base);
    }
    catch (IllegalArgumentException e) {
      log.error(String.format("base: %1$s, dockerignore: %2$s", base.getAbsolutePath(), dockerignore), e);
      throw e;
    }

    log.debug("base: {}", base.getAbsolutePath());
    log.debug("dockerignore: {}", dockerignore);
    globsMatcher = new GlobsMatcher(base, dockerignore);
  }

  public List<String> getDockerignorePatterns(final File base) {
    List<String> result = new ArrayList<>();
    File[] files = base.listFiles();
    if (files == null || files.length == 0) {
      return result;
    }
    Optional<File> dockerignoreFile = Arrays.stream(files).filter((file -> {
      String relativeFileName = relativize(base, file);
      return ".dockerignore".equals(relativeFileName);
    })).findFirst();
    if (!dockerignoreFile.isPresent()) {
      return result;
    }
    try {
      Collections.addAll(result, IOUtils.toString(new FileInputStream(dockerignoreFile.get())).split("[\r\n]+"));
      return result;
    }
    catch (IOException e) {
      log.error("Couldn't read {}", dockerignoreFile.get());
      throw new RuntimeException(e);
    }
  }

  public List<String> relativize(Collection<String> dockerignores, final File base) {
    return dockerignores.stream()
        .map((String dockerignore) -> new File(dockerignore).isAbsolute() ? relativize(base, new File(dockerignore)) : dockerignore)
        .collect(Collectors.toList());
  }

  public String relativize(File base, File absolute) {
    Path basePath = base.getAbsoluteFile().toPath();
    Path otherPath = absolute.getAbsoluteFile().toPath();
    if (!basePath.getRoot().equals(otherPath.getRoot())) {
      // Can occur on Windows, when
      // - java temp directory is under C:/
      // - project directory is under D:/
      return otherPath.toString();
    }

    return basePath.relativize(otherPath).toString();
  }

  public List<File> collectFiles(File base) throws IOException {
    final List<File> files = new ArrayList<>();
    Files.walk(base.toPath())
        .filter(p -> Files.isRegularFile(p) && !getGlobsMatcher().matches(p.toFile()))
        .forEach(p -> files.add(p.toFile()));
    log.debug("filtered list of files: {}", files);
    return files;
  }

  public GlobsMatcher getGlobsMatcher() {
    return globsMatcher;
  }
}
