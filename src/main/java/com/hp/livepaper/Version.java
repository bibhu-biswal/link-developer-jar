package com.hp.livepaper;

public class Version {
  public static final String JAR_VERSION = "1.0.2"; // keep in sync manually with "version" in pom.xml
  public static final String API_VERSION = LivePaper.API_VERSION;
  public static void main(String[] args) {
    System.out.println(JAR_VERSION);
  }
}
