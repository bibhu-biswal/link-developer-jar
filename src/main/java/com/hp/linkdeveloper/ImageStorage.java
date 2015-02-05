package com.hp.linkdeveloper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import org.boon.json.JsonFactory;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * Class used to upload and download images from the Link Developer Storage API.
 * @author gsisson
 *
 */
public class ImageStorage {
  public enum Type { JPEG, PNG }
  /**
   * Since you can only watermark images stored at the Link Developer Storage service, this method exists to allow
   * you to upload an images to the Storage ervice.  It returns a new URL, for the location of the copied image
   * which now resides at the Storage service.
   * @param ld is the LinkDeveloperSession (which holds the access token for the user)
   * @param imageUrl The URL of the JPEG image that you want to upload to the by Link Developer Storage service.
   * @return A string holding the URL of the image on the Link Developer Storage service.
   * @throws LinkDeveloperException
   */
  public static String uploadJpgFromUrl(LinkDeveloperSession ld, String imageUrl) throws LinkDeveloperException {
    if (imageUrl == null || imageUrl.length() == 0)
      throw new java.lang.IllegalArgumentException("image_url cannot be null or blank");
    if ( imageIsAlreadyOnLinkDeveloperStorageService(imageUrl) )
      return imageUrl;
    byte[] imageBytes = download(null, Type.JPEG, imageUrl);
    return uploadBytes(ld, imageBytes);
  }
  public static String uploadJpgFromFile(LinkDeveloperSession ld, String filename) throws LinkDeveloperException, IOException {
    if (filename == null || filename.length() == 0)
      throw new java.lang.IllegalArgumentException("filename cannot be null or blank");
    FileInputStream fis = new FileInputStream(filename);
    byte[] imageBytes = LinkDeveloperSession.inputStreamToByteArray(fis);
    fis.close();
    return uploadBytes(ld, imageBytes);
  }
  protected static String uploadBytes(LinkDeveloperSession ld, byte[] imageBytes) throws LinkDeveloperException {
    int maxTries = LinkDeveloperSession.getNetworkErrorRetryCount();
    int tries = 0;
    while (true) {
      try {
        Builder webResource = LinkDeveloperSession.createWebResource(LinkDeveloper.API_HOST_STORAGE);
        ClientResponse response = webResource.
            header("Content-Type", "image/jpg").
            header("Authorization", ld.getAccessToken()).
            post(ClientResponse.class, imageBytes);
        return response.getHeaders().getFirst("location");
      }
      catch (ClientHandlerException e) {
        tries++;
        if (tries > maxTries)
          throw new LinkDeveloperException("Failed to upload the image to be watermarked to the storage service!", e);
        System.err.println("Warning: Network error! retrying (" + tries + " of " + maxTries + ")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LinkDeveloperSession.getNetworkErrorRetrySleepPeriod());
        }
        catch (InterruptedException unused) {
          throw new LinkDeveloperException("Failed to upload the image to be watermarked to the storage service!", e);
        }
        continue;
      }
    }
  }
  /**
   * Returns an image from the Link Developer Storage service.  This may be a non-watermarked image (that you
   * previously uploaded with uploadJpg()).  Or it may be a watermarked version of an image you previously
   * uploaded with uploadJpg().   Or it may be a QR Code image that you are downloading.
   * @param ld is the LinkDeveloperSession (which holds the access token for the user).  If downloading from
   * a URL that is not backed by Link Developer, leave this null, to indicate that the download call does not
   * need a Link Developer authentication header.
   * @param trigger A Trigger object (which internally knows the URL from which to download it's image)
   * @param imageType indicates whether the image should be downloaded as a JPEG or a PNG.  QR Code images
   * are always PNG type, and images to be watermarked, or already watermarked, are always JPEG type.
   * @return the byte array containing the image
   * @throws LinkDeveloperException
   */
  public static byte[] download(LinkDeveloperSession ld, Trigger trigger, Type type) throws LinkDeveloperException {
    return download(ld, trigger, type, "");
  }
  /**
   * Returns an image from the Link Developer Storage service.  This may be a non-watermarked image (that you
   * previously uploaded with uploadJpg()).  Or it may be a watermarked version of an image you previously
   * uploaded with uploadJpg().   Or it may be a QR Code image that you are downloading.
   * @param ld is the LinkDeveloperSession (which holds the access token for the user).  If downloading from
   * a URL that is not backed by Link Developer, leave this null, to indicate that the download call does not
   * need a Link Developer authentication header.
   * @param trigger A Trigger object (which internally knows the URL from which to download it's image)
   * @param imageType indicates whether the image should be downloaded as a JPEG or a PNG.  QR Code images
   * are always PNG type, and images to be watermarked, or already watermarked, are always JPEG type.
   * @param params Any additional params to be added to the URL when downloading the image.
   * @return the byte array containing the image
   * @throws LinkDeveloperException
   */
  public static byte[] download(LinkDeveloperSession ld, Trigger trigger, Type type, String params) throws LinkDeveloperException {
    String imageUrl = trigger.getLinks().get("image")+params;
    return download(ld, type, imageUrl);
  }
  protected static byte[] download(LinkDeveloperSession ld, Type type, String imageUrl) throws LinkDeveloperException {
    String imageType = "image/"+type.toString().toLowerCase();
    int maxTries = LinkDeveloperSession.getNetworkErrorRetryCount();
    int tries = 0;
    while (true) {
      try {
        Builder builder = LinkDeveloperSession.createWebResource(imageUrl);
        builder.accept(imageType);
        if ( ld != null )
          builder.header("Authorization", ld.getAccessToken());
        ClientResponse response = builder.get(ClientResponse.class);
        if (response.getStatus() != 200) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = JsonFactory.create().readValue(response.getEntity(String.class), Map.class);
          throw new LinkDeveloperException(map.toString()); // throw to handler below, for retry support
        }
        return LinkDeveloperSession.inputStreamToByteArray(response.getEntityInputStream());
      }
      catch (IOException | ClientHandlerException | LinkDeveloperException e) {
        if (++tries >= maxTries)
          throw new LinkDeveloperException("Failed to download \"" + imageType + "\" image! (from " + imageUrl + ")", e);
        System.err.println("Warning: Network error! retrying (" + tries + " of " + maxTries + ")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LinkDeveloperSession.getNetworkErrorRetrySleepPeriod());
        }
        catch (InterruptedException e1) {
          throw new LinkDeveloperException("Failed to download \"" + imageType + "\" image! (from " + imageUrl + ")", e);
        }
        continue;
      }
    }
  }
  /**
   * Checks a URL to see if it is from the Link Developer Storage service.
   * @param imageUrl
   * @return
   */
  protected static boolean imageIsAlreadyOnLinkDeveloperStorageService(String imageUrl) {
    return imageUrl.toLowerCase().contains(LinkDeveloper.API_HOST_STORAGE.toLowerCase());
  }
  private ImageStorage() {}
}