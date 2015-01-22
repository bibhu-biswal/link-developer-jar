package com.hp.livepaper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;

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