import java.util.HashMap;
import java.util.Calendar;
import java.util.List;

import org.boon.json.JsonFactory;

import javax.ws.rs.POST;
import javax.ws.rs.core.UriBuilder;

import org.boon.json.ObjectMapper;

import java.util.Map;

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
	public int responseCode = 0;
	
	/*
	 * Authorize the client. 
	 * @param clientID The clientID provided in the access credentials
	 * @param secret The client secret provided in the access credentials
	 * 
	 */
	public void auth(HashMap<String, String> map)  //change param to HashMap<String, String> map if testing with yml file
	{											   // else String clientID, String secret
		//While refactoring: Convert to static method that returns an authorized object of inner class (possibly)
		
		
		 //COMMENT IF NOT TESTING WITH YML FILE
		 String id = map.get("id");
		 String secret = map.get("secret");
		 authorize(id, secret);
		 //if (token == null)
			//	 System.out.println("Unauthorized!"); 
		//uncomment if not testing with yml file
		 /*
		  * if (clientID == null || secret == null)
		  * 	throw new NullPointerException("Null arguments not accepted.");
		  */
		//authorize(clientID, secret);
		 
		
	}

    @SuppressWarnings("unchecked")
	public String shorten(String longURL)
	{
		if (token == null)
			return null;
		
		Map<String, Object> trig = trigger("shorturl");
		Map<String, Object> payoff = url_payoff(longURL);
		link((String)trig.get("id"), (String)payoff.get("id"));
		
		// trig["link"].select { |item| item["rel"] == "shortURL" }.first["href"]
		List<Map<String, String>> listLink = (List<Map<String,String>>) trig.get("link");
		for(Map<String, String> map: listLink) 
		{
			if(map.get("rel").equals("shortURL"))
				return map.get("href");
		}
		return null;
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
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource webResource = client.resource(UriBuilder.fromUri(LP_API_HOST+"/auth/token").build());

		ClientResponse response = webResource.header("Content-Type", "application/x-www-form-urlencoded").accept("application/json").header("Authorization", toBeSent).post(ClientResponse.class, body);
		responseCode = response.getStatus();
		
		//Exceptions and other response codes handling: In the JAR, ideally there should be some sort of exception, or null
		//obejct returned
		if(responseCode== 200)
		{	
			ObjectMapper mapper = JsonFactory.create();
			Map<String, String> ResponseMap = mapper.readValue(response.getEntity(String.class), Map.class);
			token = ResponseMap.get("accessToken");
		}
	}

	
	private Map<String, Object> trigger(String type)
	{
		Map<String, Object> body = new HashMap<String, Object>();
		Map<String, Object> trigger = new HashMap<String, Object>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1); 
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
		String toBeSent = "Bearer "+ token; 
		ObjectMapper mapper = JsonFactory.create();
		String body = mapper.writeValueAsString(bodyMap);
		
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource webResource = client.resource(UriBuilder.fromUri(HostURL).build());
		ClientResponse response = webResource.header("Content-Type", "application/json").accept("application/json").header("Authorization", toBeSent).post(ClientResponse.class, body);
		responseCode = response.getStatus();
		if(responseCode == 201)
		{	
			Map<String, Object> responseMap = mapper.readValue(response.getEntity(String.class), Map.class);
            //JSON.parse(response.body)[resource]
			return (Map<String, Object>)responseMap.get(resource);
		}
 		return null;
	}	
}
