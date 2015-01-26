package com.hp.livepaper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;

/**
 * Class used to create an HttpURLConnection with a proxy, if the Java proxy properties
 * have been set with -Dhttps.proxyHost=<proxy host> and -Dhttps.proxyPort=<proxy port>
 * (based on the "There is an easier approach" solution proposed at
 * "http://stackoverflow.com/questions/10415607/jersey-client-set-proxy")
 */
public class ConnectionFactory implements HttpURLConnectionFactory {
  public ConnectionFactory() {}
  @Override
  public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
    String proxyHost = System.getProperty("https.proxyHost");
    String proxyPort = System.getProperty("https.proxyPort");
    if (   proxyHost != null && proxyHost.length() > 0
        && proxyPort != null && proxyPort.length() > 0 ) {
      Proxy proxy = new Proxy(Proxy.Type.HTTP,
          new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
      return (HttpURLConnection) url.openConnection(proxy);
    }
    return (HttpURLConnection) url.openConnection();
  }
}