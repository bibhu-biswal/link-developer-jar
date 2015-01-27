package com.hp.livepaper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

public class LivePaperSession {
  public static LivePaperSession create(String clientId, String secretId) {
    return new LivePaperSession(clientId, secretId);
  }
  /**
   * Shortens the url passed as the argument.
   * @param longURL The URL that needs to be shortened.
   * @return The shortened URL or null if passed string is null, or if access is unauthorized, or in case of server error.
   */
  public String createShortUrl(String name, String url) throws LivePaperException {
    ShortTrigger tr = ShortTrigger.create(this, name);
    Payoff       po = Payoff.create(this, name, Payoff.Type.WEB_PAYOFF, url);
    Link.create(this, name, tr, po);
    return tr.getShortUrl();
  }
  /**
   * Returns a byte representation of the QR code that encodes the passed URL.
   * @param url The URL that needs to be QR-coded.
   * @return The byte representation of the QR code or null if passed string is null, or if access is unauthorized, or in case of server error.
   */
  public byte[] createQrCode(String name, String url, int width) throws LivePaperException {
    QrTrigger tr = QrTrigger.create(this, name);
    Payoff    po = Payoff.create(this, name, Payoff.Type.WEB_PAYOFF, url);
    Link.create(this, name, tr, po);
    return    tr.downloadQrCode(width);
  }
  /**
   * Returns a byte representation of the watermarked JPEG image that encodes the passed URL.
   * @param imageLoc The the URL where the image to be watermarked is hosted (Note that if the image
   * is not being hosted on the Live Paper Storage service, then the image will be copied to Live Paper Storage)
   * @param url The URL to be encoded in the watermarked image.
   * @return The byte array containing the watermarked image.
   * @throws LivePaperException
   */
  public byte[] createWatermarkedJpgImage(String name, WmTrigger.Strength strength, WmTrigger.Resolution resolution, String urlForJpgImageToBeWatermarked, String imageUrlForPayoff) throws LivePaperException {
    String    stored_image_url = ImageStorage.uploadJpg(this, urlForJpgImageToBeWatermarked);
    WmTrigger tr = WmTrigger.create(this, name, strength, resolution, stored_image_url);
    Payoff    po = Payoff.create(this, name, Payoff.Type.WEB_PAYOFF, imageUrlForPayoff);
    Link.create(this, name, tr, po);
    return tr.downloadWatermarkedJpgImage();
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
   * resetLppAccessToken() when the token is seen to be invalid.  This will ensure that future calls to this
   * method will obtain a new access token.
   * @return the API access token.
   */
  public String getLppAccessToken() {
    int maxTries = LivePaperSession.getNetworkErrorRetryCount();
    int tries = 0;
    while (true) {
      try {
        if (lpp_access_token.length() > 0)
          return lpp_access_token;
        String body = "grant_type=client_credentials&scope=all";
        Builder webResource = createWebResource(LivePaper.API_HOST_AUTH);
        ClientResponse response = webResource.
            header("Content-Type", "application/x-www-form-urlencoded").
            accept("application/json").
            header("Authorization", getLppBasicAuth()).
            post(ClientResponse.class, body);
        int responseCode = response.getStatus();
        if (responseCode == 200) {
          ObjectMapper mapper = JsonFactory.create();
          @SuppressWarnings("unchecked")
          Map<String, String> ResponseMap = mapper.readValue(response.getEntity(String.class), Map.class);
          lpp_access_token = "Bearer " + ResponseMap.get("accessToken");
          return lpp_access_token;
        }
        if (tries > maxTries) {
          System.out.println(responseCode);
          System.out.println(response.getEntity(String.class)); // This is sometimes an HTML formatted error message!
          // throw response.getEntity(String.class)
        }
      }
      catch (ClientHandlerException e) {
        if (++tries >= maxTries)
          throw e;
        System.err.println("Warning: Network error! retrying (" + tries + " of " + maxTries + ")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LivePaperSession.getNetworkErrorRetrySleepPeriod());
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
   * and the user wants to force getLppAccessToken() calls to obtain a new access token.
   */
  public void resetLppAccessToken() {
    lpp_access_token = ""; // a new token will be obtained on the next call to getLppAccessToken()
  }
  protected LivePaperSession(String clientID, String secret) {
    if (clientID == null || secret == null)
      throw new IllegalArgumentException("Null arguments not accepted.");
    if (clientID.length() == 0 || secret.length() == 0)
      throw new IllegalArgumentException("Blank arguments not accepted.");
    lpp_basic_auth = "Basic " + DatatypeConverter.printBase64Binary((clientID + ":" + secret).getBytes(StandardCharsets.UTF_8));
  }
  protected static Builder createWebResource(String location) {
    return createWebResourceUnTagged(location).header("x_user_info", "app=live_paper_jar_v" + Version.JAR_VERSION);
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
  protected Map<String, Object> rest_request(String url, Method method) throws LivePaperException {
    return rest_request(url, method, new HashMap<String, Object>());
  }
  @SuppressWarnings("unchecked")
  protected Map<String, Object> rest_request(String url, Method method, Map<String, Object> bodyMap) throws LivePaperException {
    // TODO: support "x_user_info: app=live_paper_jar" (so that API can track the source of the API calls)
    int responseCode = -1;
    int maxTries = LivePaperSession.getNetworkErrorRetryCount();
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
            header("Authorization", getLppAccessToken()).
            get(ClientResponse.class);
            break;
          case PUT:
            response = webResource.
            header("Content-Type", "application/json").
            accept("application/json").
            header("Authorization", getLppAccessToken()).
            put(ClientResponse.class, body);
            break;
          case POST:
            response = webResource.
            header("Content-Type", "application/json").
            accept("application/json").
            header("Authorization", getLppAccessToken()).
            post(ClientResponse.class, body);
            break;
          case DELETE:
            response = webResource.
            header("Content-Type", "application/json").
            accept("application/json").
            header("Authorization", getLppAccessToken()).
            delete(ClientResponse.class, body);
            break;
        }
        responseCode = response.getStatus();
        if (responseCode == 401) { // authentication problem
          if (++tries >= maxTries)
            throw new LivePaperException("Unable to create object with POST! (after " + (tries - 1) + " tries)");
          continue;
        }
        break;
      }
      catch (ClientHandlerException e) {
        if (++tries >= maxTries)
          throw new LivePaperException("Unable to create object with POST! (after " + (tries - 1) + " tries)");
        System.err.println("Warning: Network error! retrying (" + tries + " of " + maxTries + ")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LivePaperSession.getNetworkErrorRetrySleepPeriod());
        }
        catch (InterruptedException e1) {
          throw e;
        }
        continue;
      }
    }
    if (method == Method.DELETE && responseCode == 204 ||
        method == Method.DELETE && responseCode == 200    )
      return null;
    if (   method == Method.GET  && responseCode == 200
        || method == Method.PUT  && responseCode == 200
        || method == Method.POST && responseCode == 201 ) {
      String responseStr = response.getEntity(String.class);
      return mapper.readValue(responseStr, Map.class);
    }
    String message = htmlStripToH1(response.getEntity(String.class));
    throw new LivePaperException(responseCode + ": " + message + " (" + method + " " + url + ")");
  }
  protected static String capitalize(String line) {
    return Character.toUpperCase(line.charAt(0)) + line.substring(1);
  }
  /**
   * LivePaper REST API calls sometimes return HTML with the important message stored in the H1 tag.
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
  private String getLppBasicAuth() {
    return lpp_basic_auth;
  }
  private transient String lpp_access_token = "";
  private transient String lpp_basic_auth   = "";
  private static int network_error_retry_sleep_period = 0;
  private static int network_error_retry_count = 0;
}
