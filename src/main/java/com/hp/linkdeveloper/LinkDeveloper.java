package com.hp.linkdeveloper;

/**
 * Provides a Java interface to the Link Developer service by HP for
 * creating watermarked images, QR codes, and mobile-friendly
 * shortened URLs.
 */
public class LinkDeveloper {
  public static final String API_VERSION      = "v1";
  public static final String API_HOST         = "https://www.livepaperapi.com/api/"+API_VERSION+"/";
  public static final String API_HOST_AUTH    = "https://www.livepaperapi.com/auth/"+API_VERSION+"/token";
  public static final String API_HOST_STORAGE = "https://storage.livepaperapi.com/objects/"+API_VERSION+"/files";
  /**
   * Create an authorized session, thus allowing interaction with the rest of the Link Developer API.
   * @param clientID The clientID provided in the access credentials
   * @param secret The client secret provided in the access credentials
   * @return An authorized instance of LinkDeveloper that allows access to Link Developer services
   *         or null if authorization fails.
   */
  public static LinkDeveloperSession createSession(String clientID, String secret) {
    return LinkDeveloperSession.create(clientID, secret);
  }
}