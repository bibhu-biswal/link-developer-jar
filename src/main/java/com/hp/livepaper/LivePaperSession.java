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
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class LivePaperSession {
  public enum Method {
    GET, PUT, POST, DELETE;
  }
  public static LivePaperSession create(String clientID, String secret) {
    return new LivePaperSession(clientID, secret);
  }
  private String lpp_access_token = "";
  private String lpp_basic_auth   = "";
  private static int network_error_retry_sleep_period = 3000;
  public  static void setNetworkErrorRetrySleepPeriod(int sleepPeriodInMilliseconds) {
    network_error_retry_sleep_period = sleepPeriodInMilliseconds;
    if (network_error_retry_sleep_period < 0)
      network_error_retry_sleep_period = 1000;
  }
  public  static int getNetworkErrorRetrySleepPeriod() {
    return network_error_retry_sleep_period;
  }
  private static int network_error_retry_count = 5;
  public static void setNetworkErrorRetryCount(int retryCount) {
    network_error_retry_count = retryCount;
    if (network_error_retry_count < 0)
      network_error_retry_count = 0;
  }
  public static int getNetworkErrorRetryCount() {
    return network_error_retry_count;
  }
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
  public void resetLppAccessToken() {
    lpp_access_token = "";
  }
  public String getLppBasicAuth() {
    return lpp_basic_auth;
  }
  private LivePaperSession(String clientID, String secret) {
    if (clientID == null || secret == null)
      throw new IllegalArgumentException("Null arguments not accepted.");
    if (clientID.length() == 0 || secret.length() == 0)
      throw new IllegalArgumentException("Blank arguments not accepted.");
    lpp_basic_auth = "Basic " + DatatypeConverter.printBase64Binary((clientID + ":" + secret).getBytes(StandardCharsets.UTF_8));
  }
  static Builder createWebResource(String location) {
    return createWebResourceUnTagged(location).header("x_user_info", "app=live_paper_jar_v" + Version.VERSION);
  }
  static Builder createWebResourceUnTagged(String location) {
    disableCertificateValidation();
    Client client = null;
    client = Client.create(new DefaultClientConfig());
    // the following line of code, and the ConnectionFactory class, are from this article:
    // http://stackoverflow.com/questions/10415607/jersey-client-set-proxy (search "easier approach")
    // these changes are to get the download of the user's image (for watermarking) to work
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
  private static void disableCertificateValidation() {
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
  public static byte[] inputStreamToByteArray(InputStream is) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    int next = is.read();
    while (next > -1) {
      bos.write(next);
      next = is.read();
    }
    bos.flush();
    return bos.toByteArray();
  }
  public byte[] getImageBytes(String imageType, String imageUrl) throws LivePaperException {
    int maxTries = LivePaperSession.getNetworkErrorRetryCount();
    int tries = 0;
    while (true) {
      try {
        ClientResponse response = createWebResource(imageUrl).
            accept(imageType).
            header("Authorization", getLppAccessToken()).
            get(ClientResponse.class);
        return LivePaperSession.inputStreamToByteArray(response.getEntityInputStream());
      }
      catch (IOException | ClientHandlerException e) {
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
  public Map<String, Object> rest_request(String url, Method method) throws LivePaperException {
    return rest_request(url, method, new HashMap<String, Object>());
  }
  @SuppressWarnings("unchecked")
  public Map<String, Object> rest_request(String url, Method method, Map<String, Object> bodyMap) throws LivePaperException {
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
    switch (method) {
      case GET:
        if (responseCode == 200) // 200: list/get object
          return mapper.readValue(response.getEntity(String.class), Map.class);
        // 404: fail to list/get non-existent object
        break;
      case PUT:
        if (responseCode == 200) // 200: update object
          return mapper.readValue(response.getEntity(String.class), Map.class);
        break;
      case POST:
        if (responseCode == 201) // 201: created new object
          return mapper.readValue(response.getEntity(String.class), Map.class);
        break;
      case DELETE:
        if (responseCode == 204)
          return null;
        if (responseCode == 200) // 200: delete object
          return null;
        break;
    }
    String message = htmlStripToH1(response.getEntity(String.class));
    throw new LivePaperException(responseCode + ": " + message + " (" + method + " " + url + ")");
  }
  private final static Pattern htmlEncodedRestMessagePattern = Pattern.compile("(^.*<h1>)(.*)(</h1>.*$)");
  /**
   * LivePaper REST API calls sometimes return HTML with the important message strored in the H1 tag.
   * @param possibleHtmlEncodedRestMessage the Rest error message that is possibly encoded in html.
   * @return the original string if not encoded; the contents of the H1 tag if encoded
   */
  private static String htmlStripToH1(String possibleHtmlEncodedRestMessage) {
    Matcher m = htmlEncodedRestMessagePattern.matcher(possibleHtmlEncodedRestMessage);
    if (m.find())
      return m.group(2);
    return possibleHtmlEncodedRestMessage;
  }
  public static String capitalize(String line) {
    return Character.toUpperCase(line.charAt(0)) + line.substring(1);
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
   * Returns a byte representation of the watermarked image that encodes the passed URL.
   * @param imageLoc The the URL where the image is hosted
   * @param url The URL that needs to be encoded in the image
   * @return The byte representation of the watermarked image or null if passed string is null, or if access is unauthorized, or in case of server error.
   * @throws LivePaperException
   */
  public byte[] createWatermarkedImage(String name, WmTrigger.Strength strength, WmTrigger.Resolution resolution, String urlForImageToBeWatermarked, String imageUrlForPayoff) throws LivePaperException {
    String    stored_image_url = ImageStorageService.upload(this, urlForImageToBeWatermarked);
    WmTrigger tr = WmTrigger.create(this, name, strength, resolution, stored_image_url);
    Payoff    po = Payoff.create(this, name, Payoff.Type.WEB_PAYOFF, imageUrlForPayoff);
    Link.create(this, name, tr, po);
    return tr.downloadWatermarkedImage();
  }
}
