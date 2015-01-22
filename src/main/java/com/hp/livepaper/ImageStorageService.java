package com.hp.livepaper;

import java.io.IOException;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;

public class ImageStorageService {
  private ImageStorageService() {}
  public static String upload(LivePaperSession lp, String imageUrl) throws LivePaperException {
    if (imageUrl == null || imageUrl.length() == 0)
      throw new java.lang.IllegalArgumentException("image_url cannot be null or blank");
    // return the original img uri if it is LivePaper storage
    if (imageUrl.contains(LivePaper.API_HOST_STORAGE))
      return imageUrl;
    byte[] bytes = downloadFromUrl(imageUrl);
    int maxTries = LivePaperSession.getNetworkErrorRetryCount();
    int tries = 0;
    while (true) {
      try {
        Builder webResource = LivePaperSession.createWebResource(LivePaper.API_HOST_STORAGE);
        ClientResponse response = webResource.
            header("Content-Type", "image/jpg").
            header("Authorization", lp.getLppAccessToken()).
            post(ClientResponse.class, bytes);
        return response.getHeaders().getFirst("location");
      }
      catch (ClientHandlerException e) {
        tries++;
        if (tries > maxTries)
          throw new LivePaperException("Failed to upload the image to be watermarked to the storage service!", e);
        System.err.println("Warning: Network error! retrying (" + tries + " of " + maxTries + ")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LivePaperSession.getNetworkErrorRetrySleepPeriod());
        }
        catch (InterruptedException unused) {
          throw new LivePaperException("Failed to upload the image to be watermarked to the storage service!", e);
        }
        continue;
      }
    }
  }
  private static byte[] downloadFromUrl(String imageUrl) throws LivePaperException {
    int maxTries = LivePaperSession.getNetworkErrorRetryCount();
    int tries = 0;
    while (true) {
      try {
        Builder webResource = LivePaperSession.createWebResource(imageUrl);
        ClientResponse imgResponse = null;
        imgResponse = webResource.
            accept("image/jpg").
            get(ClientResponse.class);
        return LivePaperSession.inputStreamToByteArray(imgResponse.getEntityInputStream());
      }
      catch (IOException | ClientHandlerException e) {
        tries++;
        if (tries > maxTries)
          throw new LivePaperException("Unable to obtain image to be watermarked! (from " + imageUrl + ")", e);
        System.err.println("Warning: Network error! retrying (" + tries + " of " + maxTries + ")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LivePaperSession.getNetworkErrorRetrySleepPeriod());
        }
        catch (InterruptedException unused) {
          throw new LivePaperException("Unable to obtain image to be watermarked! (from " + imageUrl + ")", e);
        }
        continue;
      }
    }
  }
}