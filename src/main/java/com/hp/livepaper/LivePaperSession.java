package com.hp.livepaper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
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
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

@SuppressWarnings("restriction")
public class LivePaperSession {
  protected static final String LP_API_HOST = "https://www.livepaperapi.com";
  public enum Method {
    GET, POST, PUT, DELETE;
  }
  private static String lpp_access_token = null; // TODO: these statics are not thread-safe!
  private static String lpp_basic_auth   = null;
  private static int network_error_retry_sleep_period = 3000;
  public static void setNetworkErrorRetrySleepPeriod(int sleepPeriodInMilliseconds) {
    network_error_retry_sleep_period = sleepPeriodInMilliseconds;
    if ( network_error_retry_sleep_period < 0 )
      network_error_retry_sleep_period = 1000;
  }
  public static int getRetrySleepPeriod() {
    return network_error_retry_sleep_period;
  }
  private static int network_error_retry_count = 3;
  public static void setNetworkErrorRetryCount(int retryCount) {
    network_error_retry_count = retryCount;
    if ( network_error_retry_count < 0 )
      network_error_retry_count = 0;
  }
  public static int getNetworkErrorRetryCount() {
    return network_error_retry_count;
  }
  public static String getLppAccessToken() {
    int maxTries = LivePaperSession.getNetworkErrorRetryCount();
    int tries = 0;
    while (true) {
      try {
        if (lpp_access_token != null && lpp_access_token.length() > 0)
          return lpp_access_token;
        String body = "grant_type=client_credentials&scope=all";
        WebResource webResource = createWebResource(LP_API_HOST + "/auth/v1/token");
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
      catch (com.sun.jersey.api.client.ClientHandlerException e) {
        tries++;
        if (tries > maxTries)
          throw e;
        System.err.println("Warning: Network error! retrying (" + tries + " of "+maxTries+")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LivePaperSession.getRetrySleepPeriod());
        }
        catch (InterruptedException unused) {
          throw e;
        }
        continue;
      }
    }
  }
  public static void resetLppAccessToken() {
    LivePaperSession.lpp_access_token = null;
  }
  public static String getLppBasicAuth() {
    return LivePaperSession.lpp_basic_auth;
  }
  public static void setLppBasicAuth(String clientID, String secret) throws UnsupportedEncodingException {
    if (clientID == null || secret == null)
      throw new NullPointerException("Null arguments not accepted.");
    if (clientID.length() == 0 || secret.length() == 0)
      throw new NullPointerException("Blank arguments not accepted.");
    LivePaperSession.lpp_basic_auth = "Basic " + DatatypeConverter.printBase64Binary((clientID + ":" + secret).getBytes("UTF-8"));
  }
  public static WebResource createWebResource(String location) {
    disableCertificateValidation();
    ClientConfig config = new DefaultClientConfig();
    Client client = Client.create(config);
    WebResource webResource = client.resource(UriBuilder.fromUri(location).build());
    return webResource;
  }
  private static void disableCertificateValidation() {
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[] {
        new X509TrustManager() {
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }
          public void checkClientTrusted(X509Certificate[] certs, String authType) {}
          public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } };
    // Ignore differences between given hostname and certificate hostname
    HostnameVerifier hv = new HostnameVerifier() {
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
  public  static byte[] inputStreamToByteArray(InputStream is) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    int next = is.read();
    while (next > -1) {
      bos.write(next);
      next = is.read();
    }
    bos.flush();
    return bos.toByteArray();
  }
}
