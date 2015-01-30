package com.hp.livepaper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import org.boon.json.JsonFactory;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * Class used to upload and download images from the Live Paper Storage API.
 * @author gsisson
 *
 */
public class ImageStorage {
  public enum Type { JPEG, PNG }
  /**
   * Since you can only watermark images stored at the Live Paper Storage service, this method exists to allow
   * you to upload an images to the Storage ervice.  It returns a new URL, for the location of the copied image
   * which now resides at the Storage service.
   * @param lp is the LivePaperSession (which holds the access token for the user)
   * @param imageUrl The URL of the JPEG image that you want to upload to the by Live Paper Storage service.
   * @return A string holding the URL of the image on the Live Paper Storage service.
   * @throws LivePaperException
   */
  public static String uploadJpgFromUrl(LivePaperSession lp, String imageUrl) throws LivePaperException {
    if (imageUrl == null || imageUrl.length() == 0)
      throw new java.lang.IllegalArgumentException("image_url cannot be null or blank");
    if ( imageIsAlreadyOnLivePaperStorageService(imageUrl) )
      return imageUrl;
    byte[] imageBytes = download(null, Type.JPEG, imageUrl);
    return uploadBytes(lp, imageBytes);
  }
  public static String uploadJpgFromFile(LivePaperSession lp, String filename) throws LivePaperException, IOException {
    if (filename == null || filename.length() == 0)
      throw new java.lang.IllegalArgumentException("filename cannot be null or blank");
    FileInputStream fis = new FileInputStream(filename);
    byte[] imageBytes = LivePaperSession.inputStreamToByteArray(fis);
    fis.close();
    return uploadBytes(lp, imageBytes);
  }
  protected static String uploadBytes(LivePaperSession lp, byte[] imageBytes) throws LivePaperException {
    int maxTries = LivePaperSession.getNetworkErrorRetryCount();
    int tries = 0;
    while (true) {
      try {
        Builder webResource = LivePaperSession.createWebResource(LivePaper.API_HOST_STORAGE);
        ClientResponse response = webResource.
            header("Content-Type", "image/jpg").
            header("Authorization", lp.getLppAccessToken()).
            post(ClientResponse.class, imageBytes);
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
  /**
   * Returns an image from the Live Paper Storage service.  This may be a non-watermarked image (that you
   * previously uploaded with uploadJpg()).  Or it may be a watermarked version of an image you previously
   * uploaded with uploadJpg().   Or it may be a QR Code image that you are downloading.
   * @param lp is the LivePaperSession (which holds the access token for the user).  If downloading from
   * a URL that is not backed by Live Paper, leave this null, to indicate that the download call does not
   * need a Live Paper authentication header.
   * @param trigger A Trigger object (which internally knows the URL from which to download it's image)
   * @param imageType indicates whether the image should be downloaded as a JPEG or a PNG.  QR Code images
   * are always PNG type, and images to be watermarked, or already watermarked, are always JPEG type.
   * @return the byte array containing the image
   * @throws LivePaperException
   */
  public static byte[] download(LivePaperSession lp, Trigger trigger, Type type) throws LivePaperException {
    return download(lp, trigger, type, "");
  }
  /**
   * Returns an image from the Live Paper Storage service.  This may be a non-watermarked image (that you
   * previously uploaded with uploadJpg()).  Or it may be a watermarked version of an image you previously
   * uploaded with uploadJpg().   Or it may be a QR Code image that you are downloading.
   * @param lp is the LivePaperSession (which holds the access token for the user).  If downloading from
   * a URL that is not backed by Live Paper, leave this null, to indicate that the download call does not
   * need a Live Paper authentication header.
   * @param trigger A Trigger object (which internally knows the URL from which to download it's image)
   * @param imageType indicates whether the image should be downloaded as a JPEG or a PNG.  QR Code images
   * are always PNG type, and images to be watermarked, or already watermarked, are always JPEG type.
   * @param params Any additional params to be added to the URL when downloading the image.
   * @return the byte array containing the image
   * @throws LivePaperException
   */
  public static byte[] download(LivePaperSession lp, Trigger trigger, Type type, String params) throws LivePaperException {
    String imageUrl = trigger.getLinks().get("image")+params;
    return download(lp, type, imageUrl);
  }
  protected static byte[] download(LivePaperSession lp, Type type, String imageUrl) throws LivePaperException {
    String imageType = "image/"+type.toString().toLowerCase();
    int maxTries = LivePaperSession.getNetworkErrorRetryCount();
    int tries = 0;
    while (true) {
      try {
        Builder builder = LivePaperSession.createWebResource(imageUrl);
        builder.accept(imageType);
        if ( lp != null )
          builder.header("Authorization", lp.getLppAccessToken());
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus() != 200) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = JsonFactory.create().readValue(response.getEntity(String.class), Map.class);
          throw new LivePaperException(map.toString()); // throw to handler below, for retry support
        }
        return LivePaperSession.inputStreamToByteArray(response.getEntityInputStream());
      }
      catch (IOException | ClientHandlerException | LivePaperException e) {
        if (++tries >= maxTries)
          throw new LivePaperException("Failed to download \"" + imageType + "\" image! (from " + imageUrl + ")", e);
        System.err.println("Warning: Network error! retrying (" + tries + " of " + maxTries + ")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LivePaperSession.getNetworkErrorRetrySleepPeriod());
        }
        catch (InterruptedException e1) {
          throw new LivePaperException("Failed to download \"" + imageType + "\" image! (from " + imageUrl + ")", e);
        }
        continue;
      }
    }
  }
  /**
   * Checks a URL to see if it is from the Live Paper Storage service.
   * @param imageUrl
   * @return
   */
  protected static boolean imageIsAlreadyOnLivePaperStorageService(String imageUrl) {
    return imageUrl.toLowerCase().contains(LivePaper.API_HOST_STORAGE.toLowerCase());
  }
  private ImageStorage() {}
}