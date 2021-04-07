package de.gesellix.docker.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class DockerVersion implements Comparable<DockerVersion> {

  private int major;
  private int minor;
  private int patch;
  private String meta;

  public static DockerVersion parseDockerVersion(String version) {
    final Pattern versionPattern = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+)(.*))?");

    final DockerVersion parsedVersion = new DockerVersion();
    Matcher matcher = versionPattern.matcher(version);
    matcher.matches();
    parsedVersion.setMajor(Integer.parseInt(matcher.group(1)));
    parsedVersion.setMinor(Integer.parseInt(matcher.group(2)));
    final String s = matcher.group(3);
    parsedVersion.setPatch(Integer.parseInt(s != null && !s.isEmpty() ? s : "0"));
    final String s1 = matcher.group(4);
    parsedVersion.setMeta(s1 != null && !s1.isEmpty() ? s1 : "");
    return parsedVersion;
  }

  @Override
  public String toString() {
    return getMajor() + "." + getMinor() + "." + getPatch() + getMeta();
  }

  @Override
  public int compareTo(DockerVersion other) {
    final ArrayList<Integer> self = new ArrayList<>(Arrays.asList(this.major, this.minor, this.patch));
    final ArrayList<Integer> that = new ArrayList<>(Arrays.asList(other.getMajor(), other.getMinor(), other.getPatch()));

    AtomicInteger result = new AtomicInteger(0);
    IntStream.range(0, 2).forEach((index) -> {
      int compared = self.get(index).compareTo(that.get(index));
      if (compared != 0 && result.get() == 0) {
        result.set(compared);
      }
    });

    return result.get();
  }

  public int getMajor() {
    return major;
  }

  public void setMajor(int major) {
    this.major = major;
  }

  public int getMinor() {
    return minor;
  }

  public void setMinor(int minor) {
    this.minor = minor;
  }

  public int getPatch() {
    return patch;
  }

  public void setPatch(int patch) {
    this.patch = patch;
  }

  public String getMeta() {
    return meta;
  }

  public void setMeta(String meta) {
    this.meta = meta;
  }
}
