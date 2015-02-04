package com.hp.linkdeveloper;

@SuppressWarnings("serial")
public class LinkDeveloperException extends Exception {
  public LinkDeveloperException(String string) {
    super(string);
  }
  public LinkDeveloperException(String string, Throwable e) {
    super(string, e);
  }
}