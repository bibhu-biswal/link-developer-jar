import java.io.IOException;
import java.util.HashMap;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.boon.json.JsonFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.UriBuilder;
import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import org.boon.json.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.commons.codec.binary.Base64;

/*
 * Simple class to authorize a client using client id and secret. 
 */
public class SampleClient {

	final String LP_API_HOST = "https://www.livepaperapi.com";
	public String token = null;  
	private String accessHeader = null;
	public int responseCode;
	
	/*
	 * Remove after testing with yml file.
	 */
	public void auth(HashMap<String, String> map)  
	{											  
		 String id = map.get("id");
		 String secret = map.get("secret");
		 authorize(id, secret);
		 
	}
	
	/**
	 * Authorize the client. 
	 * @param clientID The clientID provided in the access credentials
	 * @param secret The client secret provided in the access credentials
	 * 
	 */
	public void auth(String clientID, String secret)
	{
		if (token == null)
		 System.out.println("Unauthorized!"); 
		
		 if (clientID == null || secret == null)
		  	throw new NullPointerException("Null arguments not accepted.");
		 
		authorize(clientID, secret);
	}
	
	
	/**
	 * Shortens the url passed as the argument. 
	 * @param longURL The URL that needs to be shortened. 
	 */
	public String shorten(String longURL)
	{
		if (token == null)
			return null;

		return createLink("shorturl", longURL, "shortURL", null, null);
	}


	/**
	 * Returns a byte representation of the QR code that encodes the passed URL.
	 * @param url The URL that needs to be QR-coded. 
	 */
    @GET
    public byte[] qr_bytes(String url) throws IOException 
    {
    	if (token == null)
			return null;
				
		String location = createLink("qrcode", url, "image", null, null);
	
		WebResource webResource = createWebResource(location);		
		ClientResponse response =  webResource.accept("image/png").header("Authorization", accessHeader).get(ClientResponse.class);
		byte[] bytes = IOUtils.toByteArray(response.getEntityInputStream());
		return bytes;
    }
    
 
   

	
    
    
	
	//////////////////////////////////// Private helper methods //////////////////////////////////////
	
  
    
	@POST
	@SuppressWarnings("unchecked")
	private void authorize(String clientID, String secret) 
	{
		//cannot use encodeBase64String() for completely unknown reason, need to convert to byte[] to encode 
		String toBeSent = new String(Base64.encodeBase64((clientID+":"+secret).getBytes()));
		toBeSent = "Basic "+toBeSent;
		
		String body = "grant_type=client_credentials&scope=all";
		WebResource webResource = createWebResource(LP_API_HOST+"/auth/token");

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
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1); 
		if(options != null) 
		{
			trigger.put(optionName, options);
		}
		trigger.put("expiryDate", cal.getTime());
		trigger.put("type", type);
	    trigger.put("name", "trigger");
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
		
		SampleClient.disableCertificateValidation();
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
	
	private static void disableCertificateValidation()
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