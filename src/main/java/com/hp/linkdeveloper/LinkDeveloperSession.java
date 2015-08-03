package com.hp.linkdeveloper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.DatatypeConverter;
import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class LinkDeveloperSession {
  public static LinkDeveloperSession create(String clientId, String secretId) {
    return new LinkDeveloperSession(clientId, secretId);
  }
  
  public static LinkDeveloperSession create(String basicAuth) {
      return new LinkDeveloperSession(basicAuth);
   }
  
  /**
   * Shortens the url passed as the argument.
   * @param name A string used to assign names to the trigger, payoff, and link objects created
   * @param url The URL that needs to be shortened.
   * @return The shortened URL or null if passed string is null, or if access is unauthorized, or in case of server error.
   */
  public String createShortUrl(String name, String url) throws LinkDeveloperException {
    ShortTrigger tr = ShortTrigger.create(this, name);
    Payoff       po = Payoff.create(this, name, Payoff.Type.WEB_PAYOFF, url);
    Link.create(this, name, tr, po);
    return tr.getShortUrl();
  }
  /**
   * Returns a byte representation of the QR code that encodes the passed URL.
   * @param name A string used to assign names to the trigger, payoff, and link objects created
   * @param url The URL that needs to be QR-coded.
   * @param width The width of the QR code image, in pixels.
   * @return The byte representation of the QR code or null if passed string is null, or if access is unauthorized, or in case of server error.
   */
  public byte[] createQrCode(String name, String url, int width) throws LinkDeveloperException {
    QrTrigger tr = QrTrigger.create(this, name);
    Payoff    po = Payoff.create(this, name, Payoff.Type.WEB_PAYOFF, url);
    Link.create(this, name, tr, po);
    return    tr.downloadQrCode(width);
  }
  /**
   * Returns a byte representation of the watermarked JPEG image that encodes the passed URL.
   * @param name A string used to assign names to the trigger, payoff, and link objects created
   * @param strength A watermark strength from 1-10 (1 will result in a weak, poorly scanning watermark, 10 in a very strong and visible watermark)
   * @param resolution Watermark resolution.  See https://link-creation-studio-resources.s3.amazonaws.com/learn/resources/Link_Digital_Watermarking_Guide.pdf
   * @param imageToBeWatermarked The URL or file name where the image to be watermarked is stored (Note that if the image
   * is not being hosted on the Link Developer Storage service, then the image will be copied to Link Developer Storage)
   * @param imageUrlForPayoff The URL to be encoded in the watermarked image.
   * @return The byte array containing the watermarked image.
   * @throws LinkDeveloperException, IOException
   */
    public byte[] createWatermarkedJpgImage(String name, WmTrigger.Strength strength, WmTrigger.Resolution resolution,
            String imageToBeWatermarked, String imageUrlForPayoff) throws LinkDeveloperException, IOException {
        WmTrigger tr = null;
        Payoff po = null;
        Link li = null;

        try {
            String stored_image_url = null;
            if (imageToBeWatermarked.startsWith("http://") || imageToBeWatermarked.startsWith("https://")) {
                stored_image_url = ImageStorage.uploadJpgFromUrl(this, imageToBeWatermarked);
            } else {
                stored_image_url = ImageStorage.uploadJpgFromFile(this, imageToBeWatermarked);
            }
            tr = WmTrigger.create(this, name);
            po = Payoff.create(this, name, Payoff.Type.WEB_PAYOFF, imageUrlForPayoff);
            li = Link.create(this, name, tr, po);
            return tr.watermarkImage(stored_image_url, resolution, strength);
        } catch (LinkDeveloperException | IOException e1) {
            rollback(tr, po, li);
            throw new LinkDeveloperException(e1.getMessage());
        }
    }

    private void rollback(WmTrigger tr, Payoff po, Link li) throws LinkDeveloperException {
        if (li != null && !li.getId().equalsIgnoreCase("")) {
            li.delete();
            tr.delete();
            po.delete();
        }
    }
/**
   * This method allows specifying that network errors should be automatically retried.
   * @param retryCount specifies the number of times to retry in the face of a network error.
   * This parameter defaults to zero (there will be zero/no retries in case of a network failure)
   */
  public static void setNetworkErrorRetryCount(int retryCount) {
    network_error_retry_count = retryCount;
    if (network_error_retry_count < 0)
      network_error_retry_count = 0;
  }
  public static int getNetworkErrorRetryCount() {
    return network_error_retry_count;
  }
  /**
   * When using setNetworkErrorRetryCount(), this method specifies how long to sleep between network
   * errors and a retry.  Default is zero.
   * @param sleepPeriodInMilliseconds
   */
  public  static void setNetworkErrorRetrySleepPeriod(int sleepPeriodInMilliseconds) {
    network_error_retry_sleep_period = sleepPeriodInMilliseconds;
    if (network_error_retry_sleep_period < 0)
      network_error_retry_sleep_period = 1000;
  }
  public  static int getNetworkErrorRetrySleepPeriod() {
    return network_error_retry_sleep_period;
  }
  /**
   * Returns the API access token.  When first called, the access token will have to be obtained from the API
   * (using the clientID and secretID passed to the create() factory method).  The access token will be cached
   * after that, and the cached value will be returned by this method.   Since access tokens eventually expire,
   * the token returned here is NOT guaranteed to be valid all all times.  Users of the token should call
   * resetAccessToken() when the token is seen to be invalid.  This will ensure that future calls to this
   * method will obtain a new access token.
   * @return the API access token.
   * @throws LinkDeveloperException
   */
  public String getAccessToken() throws LinkDeveloperException {
    int maxTries = LinkDeveloperSession.getNetworkErrorRetryCount();
    int tries = 0;
    while (true) {
      try {
        if (access_token.length() > 0)
          return access_token;
        String body = "grant_type=client_credentials&scope=all";
        Builder webResource = createWebResource(LinkDeveloper.API_HOST_AUTH);
        ClientResponse response = webResource.
            header("Content-Type", "application/x-www-form-urlencoded").
            accept("application/json").
            header("Authorization", getBasicAuth()).
            post(ClientResponse.class, body);
        int responseCode = response.getStatus();
        if (responseCode == 200) {
          ObjectMapper mapper = JsonFactory.create();
          @SuppressWarnings("unchecked")
          Map<String, String> ResponseMap = mapper.readValue(response.getEntity(String.class), Map.class);
          access_token = "Bearer " + ResponseMap.get("accessToken");
          return access_token;
        }
        if (++tries > maxTries) {
          System.out.println(responseCode);
          System.out.println(response.getEntity(String.class)); // This is sometimes an HTML formatted error message!
          throw new LinkDeveloperException("Unable to authenticate and obtain access token");
        }
      }
      catch (ClientHandlerException e) {
        if (++tries >= maxTries)
          throw new LinkDeveloperException("Unable to authenticate and obtain access token ("+e.getMessage()+")", e);
        System.err.println("Warning: Network error! retrying (" + tries + " of " + maxTries + ")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LinkDeveloperSession.getNetworkErrorRetrySleepPeriod());
        }
        catch (InterruptedException unused) {
          throw e;
        }
        continue;
      }
    }
  }
  /**
   * Allows "erasing" the existing access token.  Useful when the access token is known to have expired,
   * and the user wants to force getAccessToken() calls to obtain a new access token.
   */
  public void resetAccessToken() {
    access_token = ""; // a new token will be obtained on the next call to getAccessToken()
  }
  protected LinkDeveloperSession(String clientID, String secret) {
    if (clientID == null || secret == null)
      throw new IllegalArgumentException("Null arguments not accepted.");
    if (clientID.length() == 0 || secret.length() == 0)
      throw new IllegalArgumentException("Blank arguments not accepted.");
    basic_auth = "Basic " + DatatypeConverter.printBase64Binary((clientID + ":" + secret).getBytes(Charset.defaultCharset()));
  }
  
  protected LinkDeveloperSession(String basicAuth) {
      if (basicAuth == null)
        throw new IllegalArgumentException("Null arguments not accepted.");
      if (basicAuth.length() == 0)
        throw new IllegalArgumentException("Blank arguments not accepted.");
      basic_auth = basicAuth;
    }
  
  protected static Builder createWebResource(String location) {
    return createWebResourceUnTagged(location).header("X-user-info", "app=link_developer_jar_v" + Version.JAR_VERSION);
  }
  protected static Builder createWebResourceUnTagged(String location) {
    disableCertificateValidation();
    Client client = null;
    client = new Client(new URLConnectionClientHandler(new ConnectionFactory()));
    String to = System.getProperty("PROPERTY_READ_TIMEOUT");
    if (to != null && to.length() > 0)
      client.setReadTimeout(Integer.parseInt(to));
    to = System.getProperty("PROPERTY_CONNECT_TIMEOUT");
    if (to != null && to.length() > 0)
      client.setConnectTimeout(Integer.parseInt(to));
    WebResource webResource = client.resource(UriBuilder.fromUri(location).build());
    return webResource.getRequestBuilder();
  }
  protected static void disableCertificateValidation() {
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[] {
        new X509TrustManager() {
          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
          @Override
          public void checkClientTrusted(X509Certificate[] certs, String authType) {}
          @Override
          public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } };
    // Ignore differences between given hostname and certificate hostname
    HostnameVerifier hv = new HostnameVerifier() {
      @Override
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    };
    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
    catch (Exception e) {}
  }
  protected static byte[] inputStreamToByteArray(InputStream is) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    int next = is.read();
    while (next > -1) {
      bos.write(next);
      next = is.read();
    }
    bos.flush();
    return bos.toByteArray();
  }
  protected Map<String, Object> rest_request(String url, Method method) throws LinkDeveloperException {
    return rest_request(url, method, new HashMap<String, Object>());
  }
  @SuppressWarnings("unchecked")
  protected Map<String, Object> rest_request(String url, Method method, Map<String, Object> bodyMap) throws LinkDeveloperException {
    int responseCode = -1;
    int maxTries = LinkDeveloperSession.getNetworkErrorRetryCount();
    int tries = 0;
    ObjectMapper mapper = JsonFactory.create();
    String body = mapper.writeValueAsString(bodyMap);
    Builder webResource = null;
    webResource = createWebResource(url);
    ClientResponse response = null;
    while (true) {
      try {
        switch (method) {
          case GET:
            response = webResource.
            header("Content-Type", "application/json").
            accept("application/json").
            header("Authorization", getAccessToken()).
            get(ClientResponse.class);
            break;
          case PUT:
            response = webResource.
            header("Content-Type", "application/json").
            accept("application/json").
            header("Authorization", getAccessToken()).
            put(ClientResponse.class, body);
            break;
          case POST:
            response = webResource.
            header("Content-Type", "application/json").
            accept("application/json").
            header("Authorization", getAccessToken()).
            post(ClientResponse.class, body);
            break;
          case DELETE:
            response = webResource.
            header("Content-Type", "application/json").
            accept("application/json").
            header("Authorization", getAccessToken()).
            delete(ClientResponse.class, body);
            break;
        }
        responseCode = response.getStatus();
        if (responseCode == 401) { // authentication problem
          if (++tries >= maxTries)
            throw new LinkDeveloperException("Unable to complete REST \""+method+"\" call! (after " + (tries - 1) + " tries)");
          continue;
        }
        break;
      }
      catch (ClientHandlerException e) {
        if (++tries >= maxTries)
          throw new LinkDeveloperException("Unable to complete REST \""+method+"\" call! (after " + (tries - 1) + " tries)");
        System.err.println("Warning: Network error! retrying (" + tries + " of " + maxTries + ")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LinkDeveloperSession.getNetworkErrorRetrySleepPeriod());
        }
        catch (InterruptedException e1) {
          throw e;
        }
        continue;
      }
    }
    if (method == Method.DELETE && responseCode == 204 ||
        method == Method.DELETE && responseCode == 200    ){
        System.out.println("Deleted");
      return null;}
    if (   method == Method.GET  && responseCode == 200
        || method == Method.PUT  && responseCode == 200
        || method == Method.POST && responseCode == 201 ) {
      String responseStr = response.getEntity(String.class);
      return mapper.readValue(responseStr, Map.class);
    }
    String message = htmlStripToH1(response.getEntity(String.class));
    throw new LinkDeveloperException(responseCode + ": " + message + " (" + method + " " + url + ")");
  }
  protected static String capitalize(String line) {
    return Character.toUpperCase(line.charAt(0)) + line.substring(1);
  }
  /**
   * LinkDeveloper REST API calls sometimes return HTML with the important message stored in the H1 tag.
   * @param possibleHtmlEncodedRestMessage the REST error message that is possibly encoded in HTML.
   * @return the original string (if it was not HTML encoded), or the contents of the H1 tag if encoded.
   */
  protected static String htmlStripToH1(String possibleHtmlEncodedRestMessage) {
    Matcher m = htmlEncodedRestRespsonseMessagePattern.matcher(possibleHtmlEncodedRestMessage);
    if (m.find())
      return m.group(1);
    return possibleHtmlEncodedRestMessage;
  }
  protected final static Pattern htmlEncodedRestRespsonseMessagePattern = Pattern.compile("^.*<h1>(.*)</h1>.*$");
  protected enum Method {
    GET, PUT, POST, DELETE;
  }
  private String getBasicAuth() {
    return basic_auth;
  }
  private transient String access_token = "";
  private transient String basic_auth   = "";
  private static int network_error_retry_sleep_period = 0;
  private static int network_error_retry_count = 0;
}
