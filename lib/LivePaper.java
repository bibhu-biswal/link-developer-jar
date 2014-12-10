package com.hp;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.boon.json.JsonFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.UriBuilder;
import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import org.boon.json.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Provides a Java interface to the Live Paper service by HP for
 * creating watermarked images, QR codes, and mobile-friendly 
 * shortened URLs. 
 * @version 0.0.2
 */
public abstract class LivePaper {

	protected final String LP_API_HOST = "https://www.livepaperapi.com";
	protected String token = null;  
	protected String accessHeader = null;
	protected int responseCode;

	/**
	 * Authorize the client. 
	 * @param clientID The clientID provided in the access credentials
	 * @param secret The client secret provided in the access credentials
	 * @return An authorized instance of LivePaper that allows access to Live Paper services 
	 *		   or null if authorization fails. 
	 */
	public static LivePaper auth(String clientID, String secret) throws java.io.UnsupportedEncodingException
	{
		if (clientID == null || secret == null)
			throw new NullPointerException("Null arguments not accepted.");
		LivePaper lp = new LivePaperSession();
		lp.authorize(clientID, secret);

		if(lp.responseCode != 200)
		{
			lp.token = null;
			return null;
		}
			
		return lp;
	}
	
	/**
	 * Shortens the url passed as the argument. 
	 * @param longURL The URL that needs to be shortened.
	 * @return The shortened URL or null if passed string is null, or if access is unauthorized, or in case of server error.    
	 */
	public abstract String shorten(String longURL);
	
	/**
	 * Returns a byte representation of the QR code that encodes the passed URL.
	 * @param url The URL that needs to be QR-coded. 
	 * @return The byte representation of the QR code or null if passed string is null, or if access is unauthorized, or in case of server error.    
	 */
	public abstract byte[] qr_bytes(String url);
	
	/**
	 * Returns a byte representation of the watermarked image that encodes the passed URL.
	 * @param imageLoc The the URL where the image is hosted
	 * @param url The URL that needs to be encoded in the image 
	 * @return The byte representation of the watermarked image or null if passed string is null, or if access is unauthorized, or in case of server error.    
	 */
	public abstract byte[] watermark_bytes(String imageLoc, String url);

	protected abstract void authorize(String clientID, String secret) throws java.io.UnsupportedEncodingException;
	
	static class LivePaperSession extends LivePaper
	{

		/**
		 * Shortens the url passed as the argument. 
		 * @param longURL The URL that needs to be shortened.
		 * @return The shortened URL or null if passed string is null, or if access is unauthorized, or in case of server error.    
		 */
		public String shorten(String longURL)
		{
			if (token == null)
				return null;
			if(longURL == null)
				return null;	

			return createLink("shorturl", longURL, "shortURL", null, null);
		}


		/**
		 * Returns a byte representation of the QR code that encodes the passed URL.
		 * @param url The URL that needs to be QR-coded. 
		 * @return The byte representation of the QR code or null if passed string is null, or if access is unauthorized, or in case of server error.    
		 */
		@GET
		public byte[] qr_bytes(String url) 
		{
			if (token == null)
				return null;
			if(url == null)
				return null;

			String location = createLink("qrcode", url, "image", null, null) + "?width=200";

			WebResource webResource = createWebResource(location);		
			ClientResponse response =  webResource.accept("image/png").header("Authorization", accessHeader).get(ClientResponse.class);
			byte[] bytes;
			try {
				bytes = IOUtils.toByteArray(response.getEntityInputStream());
			}
			catch(IOException e)
			{
				return null;
			}
			return bytes;
		}


		/**
		 * Returns a byte representation of the watermarked image that encodes the passed URL.
		 * @param imageLoc The the URL where the image is hosted
		 * @param url The URL that needs to be encoded in the image 
		 * @return The byte representation of the watermarked image or null if passed string is null, or if access is unauthorized, or in case of server error.    
		 */
		@GET
		public byte[] watermark_bytes(String imageLoc, String url) 
		{
			if (token == null)
				return null;
			if(imageLoc == null || url == null)
				return null;

			String image;
			try {
				image = img_upload(imageLoc);
			}
			catch(IOException e)
			{
				return null;
			}
			Map<String, String> watermark = new HashMap<String, String>();
			watermark.put("imageURL",image);		
			String location = createLink("watermark", url, "image", watermark, "watermark");
			WebResource webResource = createWebResource(location);	
			ClientResponse response =  webResource.header("Authorization", accessHeader).accept("image/jpeg").get(ClientResponse.class);
			byte[] bytes;
			try {
				bytes = IOUtils.toByteArray(response.getEntityInputStream());
			}
			catch(IOException e)
			{
				return null;
			}
			return bytes;
		}

		//////////////////////////////////// Private helper methods //////////////////////////////////////

		/* Upload the image to livepaper storage */
		@POST
		private String img_upload(String imageLoc) throws IOException
		{
			String url = "https://storage.livepaperapi.com/objects/v1/files";

			if(imageLoc.contains(url))
				return imageLoc;

			//get the image bytes from the image hosting website, and upload the image on livepaper storage
			WebResource source = createWebResource(imageLoc);		
			ClientResponse imgResponse =  source.accept("image/jpg").get(ClientResponse.class);
			byte[] bytes = IOUtils.toByteArray(imgResponse.getEntityInputStream());		

			WebResource webResource = createWebResource(url);		
			ClientResponse response = webResource.header("Content-Type", "image/jpg").header("Authorization", accessHeader).post(ClientResponse.class, bytes);

			return response.getHeaders().getFirst("location");
		}


		@POST
		@SuppressWarnings("unchecked")
		protected void authorize(String clientID, String secret) throws java.io.UnsupportedEncodingException
		{
			String toBeSent = DatatypeConverter.printBase64Binary((clientID + ":" + secret).getBytes("UTF-8"));
			toBeSent = "Basic "+toBeSent;

			String body = "grant_type=client_credentials&scope=all";
			WebResource webResource = createWebResource(LP_API_HOST+"/auth/v1/token");

			ClientResponse response = webResource.header("Content-Type", "application/x-www-form-urlencoded").accept("application/json").header("Authorization", toBeSent).post(ClientResponse.class, body);
			responseCode = response.getStatus();

			if(responseCode== 200)
			{	
				ObjectMapper mapper = JsonFactory.create();
				Map<String, String> ResponseMap = mapper.readValue(response.getEntity(String.class), Map.class);
				token = ResponseMap.get("accessToken");
				accessHeader = "Bearer "+ token; 
			}


		}


		private Map<String, Object> trigger(String type, Map<String, String> options, String optionName)
		{
			Map<String, Object> body = new HashMap<String, Object>();
			Map<String, Object> trigger = new HashMap<String, Object>();
			trigger.put("type", type);
			trigger.put("name", "trigger");
			if(options != null)
			{
				trigger.put(optionName, options);
			}
			body.put("trigger", trigger);
			return create_resource("trigger", body);
		}


		private Map<String, Object> url_payoff(String longURL)
		{
			Map<String, Object> body = new HashMap<String, Object>();
			Map<String, String> payoff = new HashMap<String, String>();
			payoff.put("URL", longURL);
			payoff.put("name", "payoff");
			body.put("payoff", payoff);
			return create_resource("payoff", body);
		}


		private Map<String, Object> link(String triggerID, String payoffID)
		{
			Map<String, Object> body = new HashMap<String, Object>();
			Map<String, String> link = new HashMap<String, String>();
			link.put("triggerId", triggerID);
			link.put("payoffId", payoffID);
			link.put("name", "link");
			body.put("link", link);
			return create_resource("link", body);
		}

		@POST
		@SuppressWarnings("unchecked")
		private Map<String, Object> create_resource(String resource, Map<String, Object> bodyMap)
		{
			String HostURL = LP_API_HOST + "/api/v1/" + resource+"s";

			ObjectMapper mapper = JsonFactory.create();
			String body = mapper.writeValueAsString(bodyMap);
			WebResource webResource = createWebResource(HostURL);
			ClientResponse response = webResource.header("Content-Type", "application/json").accept("application/json").header("Authorization", accessHeader).post(ClientResponse.class, body);
			responseCode = response.getStatus();
			if(responseCode == 201)
			{	
				Map<String, Object> responseMap = mapper.readValue(response.getEntity(String.class), Map.class);
				return (Map<String, Object>)responseMap.get(resource);
			}

			else 
			{
				System.out.println(responseCode);
				System.out.println(response.getEntity(String.class));
			}
			return null;
		}	

		private WebResource createWebResource(String location)  {

			LivePaper.disableCertificateValidation();
			ClientConfig config = new DefaultClientConfig();

			Client client = Client.create(config);
			WebResource webResource = client.resource(UriBuilder.fromUri(location).build());
			return webResource;
		} 


		@SuppressWarnings("unchecked")
		private String createLink(String triggerType, String longURL, String type, Map<String, String> opts, String optName) {
			Map<String, Object> trig = trigger(triggerType, opts, optName);
			Map<String, Object> payoff = url_payoff(longURL);

			link((String)trig.get("id"), (String)payoff.get("id"));
			List<Map<String, String>> listLink = (List<Map<String,String>>) trig.get("link");
			for(Map<String, String> map: listLink) 
			{
				if(map.get("rel").equals(type))
					return map.get("href");
			}

			return null;
		}
	}

	protected static void disableCertificateValidation()
	{
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { 
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() { 
						return new X509Certificate[0]; 
					}
					public void checkClientTrusted(X509Certificate[] certs, String authType) {}
					public void checkServerTrusted(X509Certificate[] certs, String authType) {}
				}};

		// Ignore differences between given hostname and certificate hostname
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) { return true; }
		};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {}
	}


}
