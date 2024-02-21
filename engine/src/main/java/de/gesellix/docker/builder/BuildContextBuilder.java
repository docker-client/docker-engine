package de.gesellix.docker.builder;

import de.gesellix.util.IOUtils;
import okio.Okio;
import okio.Source;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

public class BuildContextBuilder {

  private static final Logger log = LoggerFactory.getLogger(BuildContextBuilder.class);

  public static void archiveTarFilesRecursively(File base, File targetFile) throws IOException {
    List<File> filenames = new DockerignoreFileFilter(base, new ArrayList<>(Collections.singletonList(targetFile.getAbsolutePath()))).collectFiles(base);
    log.debug("found {} files in buildContext.", filenames.size());
    archiveTarFiles(base, filenames.stream().map(File::getAbsolutePath).collect(Collectors.toList()), targetFile);
  }

  public static void archiveTarFilesRecursively(File base, OutputStream target) throws IOException {
    List<File> filenames = new DockerignoreFileFilter(base, new ArrayList<>()).collectFiles(base);
    log.debug("found {} files in buildContext.", filenames.size());
    archiveTarFiles(base, filenames.stream().map(File::getAbsolutePath).collect(Collectors.toList()), target);
  }

  public static void archiveTarFiles(File base, List<String> filenames, File targetFile) throws IOException {
    archiveTarFiles(base, filenames, Files.newOutputStream(targetFile.toPath()));
  }

  public static void archiveTarFiles(File base, List<String> filenames, OutputStream target) throws IOException {
    try (TarArchiveOutputStream tos = new TarArchiveOutputStream(new GZIPOutputStream(target))) {
      tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
      tos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
      for (String filename : filenames) {
        String relativeFileName = relativize(base, new File(filename));
        log.debug("adding {} as {}", filename, relativeFileName);
        addAsTarEntry(new File(filename), relativeFileName, tos);
      }
    }
  }

  public static void addAsTarEntry(File file, String relativeFileName, TarArchiveOutputStream tos) throws IOException {
    TarArchiveEntry tarEntry = new TarArchiveEntry(file);
    tarEntry.setName(relativeFileName);

    if (!file.isDirectory()) {
      if (Files.isExecutable(file.toPath())) {
        tarEntry.setMode(tarEntry.getMode() | 0755);
      }
    }

    tos.putArchiveEntry(tarEntry);

    if (!file.isDirectory()) {
      copyFile(file, tos);
    }

    tos.closeArchiveEntry();
  }

  public static String relativize(File base, File absolute) {
    return base.toPath().relativize(absolute.toPath()).toString();
  }

  public static long copyFile(File input, OutputStream output) throws IOException {
    Source source = null;
    try {
      source = Okio.source(input);
      return IOUtils.copy(source, Okio.sink(output));
    }
    finally {
      IOUtils.closeQuietly(source);
    }
  }
}
