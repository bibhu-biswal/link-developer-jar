package com.hp.linkdeveloper;

public class Version {
  public static final String JAR_VERSION = "1.0.0"; // keep in sync manually with "version" in pom.xml
  public static final String API_VERSION = LinkDeveloper.API_VERSION;
  public static void main(String[] args) {
    System.out.println(JAR_VERSION);
  }
}
