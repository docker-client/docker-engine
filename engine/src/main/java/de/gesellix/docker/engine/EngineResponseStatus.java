package de.gesellix.docker.engine;

import java.util.Objects;

public class EngineResponseStatus {

  private String text;
  private int code;
  private boolean success;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public boolean getSuccess() {
    return success;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }
    EngineResponseStatus that = (EngineResponseStatus) o;
    return code == that.code && success == that.success && Objects.equals(text, that.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, code, success);
  }

  @Override
  public String toString() {
    return "EngineResponseStatus{" +
           "text='" + text + '\'' +
           ", code=" + code +
           ", success=" + success +
           '}';
  }
}
