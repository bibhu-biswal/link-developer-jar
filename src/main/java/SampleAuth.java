import java.util.HashMap;

import javax.ws.rs.POST;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.apache.commons.codec.binary.Base64;

/*
 * Simple class to authorize a client using client id and secret. 
 */
public class SampleAuth {

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
		  
		//uncomment if not testing with yml file
		 /*
		  * if (clientID == null || secret == null)
		  * 	throw new NullPointerException("Null arguments not accepted.");
		  */
		//authorize(clientID, secret);
		 
		
	}
	
	
	//////////////////////////////////// Private helper methods //////////////////////////////////////
	
	@POST
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
			token = tokenizer(response.getEntity(String.class)); 
	}

	/*
	 * Extracts the access token from the JSON response. 
	 * Could also use a JSON parser, but decided against it - extra imports and dependencies.
	 * Will need to change in case the response type/format changes!
	 */
	private String tokenizer(String entity) {
		if(entity == null)
			return null;
		
		String[] toks = entity.split("\"");
		int index = 0;
		for(int i = 0; i < toks.length; i++)
		{
			//Will work as long as response remains the same, else change based on 
			//response format
			if(toks[i].equals("accessToken"))
			{
				index = i+2;
				break;
			}
		}
		return toks[index];
		
	}

}
