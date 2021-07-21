package de.gesellix.docker.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobsMatcher {

  private static final Logger log = LoggerFactory.getLogger(GlobsMatcher.class);

  private final File base;
  private final List<String> globs;
  private List<Matcher> matchers;

  public GlobsMatcher(File base, List<String> globs) {
    this.base = base;
    this.globs = globs;
  }

  public void initMatchers() {
    if (this.matchers == null) {
      final FileSystem fileSystem = FileSystems.getDefault();
      this.matchers = new ArrayList<>();
      globs.stream()
          .flatMap(glob -> {
            if (glob.endsWith("/")) {
              return Stream.of(
                  new Matcher(fileSystem, glob.replaceAll("/$", "")),
                  new Matcher(fileSystem, glob.replaceAll("/$", "/**")));
            }
            else {
              return Stream.of(new Matcher(fileSystem, glob));
            }
          })
          .collect(Collectors.toCollection(ArrayDeque::new))
          .descendingIterator() // reverse the stream
          .forEachRemaining(matchers::add);
      if (log.isDebugEnabled()) {
        matchers.forEach((m) -> log.debug("pattern: " + m.getPattern()));
      }
    }
  }

  public boolean matches(File path) {
    initMatchers();

    final Path relativePath = base.getAbsoluteFile().toPath().relativize(path.getAbsoluteFile().toPath());
    Optional<Matcher> matcher = matchers.stream().filter((m) -> m.matches(relativePath)).findFirst();
    if (!matcher.isPresent() && relativePath.getParent() != null) {
      matcher = matchers.stream().filter((m) -> m.matches(relativePath.getParent())).findFirst();
    }

    return matcher.isPresent() && !matcher.get().getNegate();
  }

  public List<Matcher> getMatchers() {
    return matchers;
  }

  public static class Matcher implements PathMatcher {

    private final String pattern;
    private final PathMatcher matcher;
    private final boolean negate;

    public Matcher(FileSystem fileSystem, String pattern) {
      // According to https://docs.docker.com/engine/reference/builder/#dockerignore-file
      // and https://golang.org/pkg/path/filepath/#Clean we clean paths
      // by removing trailing slashes and also by replacing slashes with the path separator.
      String replacement = File.separatorChar == '\\' ? "\\\\" : File.separatorChar + "";
      this.pattern = String.join(replacement, pattern.replaceAll("/", replacement).split(replacement));

      String negation = "!";
      this.negate = pattern.startsWith(negation);
      if (this.negate) {
        String invertedPattern = this.pattern.substring(negation.length());
        this.matcher = createGlob(fileSystem, invertedPattern);
      }
      else {
        this.matcher = createGlob(fileSystem, this.pattern);
      }
    }

    public static PathMatcher createGlob(FileSystem fileSystem, final String glob) {
      return fileSystem.getPathMatcher("glob:" + glob);
    }

    @Override
    public boolean matches(Path path) {
      return matcher.matches(path);
    }

    public String getPattern() {
      return pattern;
    }

    public boolean getNegate() {
      return negate;
    }

    @Override
    public String toString() {
      return "matching " + (negate ? "!" : "") + pattern;
    }
  }
}
