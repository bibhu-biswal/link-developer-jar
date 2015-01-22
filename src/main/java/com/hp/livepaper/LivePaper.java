package com.hp.livepaper;

/**
 * Provides a Java interface to the Live Paper service by HP for
 * creating watermarked images, QR codes, and mobile-friendly
 * shortened URLs.
 */
public class LivePaper {
  public static final String API_VERSION      = "v1";
  public static final String API_HOST         = "https://www.livepaperapi.com/api/"+API_VERSION+"/";
  public static final String API_HOST_AUTH    = "https://www.livepaperapi.com/auth/"+API_VERSION+"/token";
  public static final String API_HOST_STORAGE = "https://storage.livepaperapi.com/objects/"+API_VERSION+"/files";
  /**
   * Create an authorized session for the client.
   * @param clientID The clientID provided in the access credentials
   * @param secret The client secret provided in the access credentials
   * @return An authorized instance of LivePaper that allows access to Live Paper services
   *         or null if authorization fails.
   */
  public static LivePaperSession createSession(String clientID, String secret) {
    return LivePaperSession.create(clientID, secret);
  }
}