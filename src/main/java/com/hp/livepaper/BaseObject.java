package com.hp.livepaper;

import java.util.Map;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

import com.hp.livepaper.LivePaperSession.Method;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public abstract class BaseObject {
	public static final String LP_API_HOST = "https://www.livepaperapi.com";
	public static final String AUTH_URL    = LP_API_HOST + "/auth/token";
  private String id = null;
  public  String getId()              { return id; }
  public  void   setId(String id)     { this.id = id; }
	private String name = "";
	public  String getName()            { return name; }
	public  void   setName(String name) { this.name = name;	}
  private String date_created = null;
  public  String getDateCreated()                    { return date_created; }
  public  void   setDateCreated(String date_created) { this.date_created = date_created; }
  private String date_modified = null;
  public  String getDateModified()                     { return date_modified; }
  public  void   setDateModified(String date_modified) { this.date_modified = date_modified; }
  private String link = "";
  public  String getLink()            { return link; }
  public  void   setLink(String link)  { this.link = link; }
	public BaseObject save() throws Exception {
		validate_attributes();
		if ( getId() == null || getId().length() == 0 ) {
			Map<String, Object> response = rest_request(api_url(), Method.POST, create_body() );
			parse(response);
		}
		return this;
	}
  public  static Map<String, Object> /*create_resource*/rest_request(String url, Method method, Map<String, Object> bodyMap) throws Exception {
    //String header_x_user_info   = "app=live_paper_jar";
    int responseCode = -1;
    int tries = 0;
    ObjectMapper mapper = JsonFactory.create();
    String body = mapper.writeValueAsString(bodyMap);
    WebResource webResource = LivePaperSession.createWebResource(url);
    ClientResponse response = null;
    while ( true ) {
      if ( method == Method.POST ) {
        response = webResource.
            header("Content-Type", "application/json").
            accept("application/json").
            header("Authorization", LivePaperSession.getLppAccessToken()).
            post(ClientResponse.class, body);
      }
      responseCode = response.getStatus();
      if ( responseCode == 401 ) { // authentication problem - token may have timed out
        tries++;
        if ( tries >= 3 )
          throw new Exception("Unable to Authenticate!");
        LivePaperSession.resetLppAccessToken();
        continue;
      }
      break;
    }
    if ( responseCode == 201 ) {
      @SuppressWarnings("unchecked")
      Map<String, Object> responseMap = mapper.readValue(response.getEntity(String.class), Map.class);
      return responseMap;
    } else {
      System.out.println(responseCode);
      System.out.println(response.getEntity(String.class));
    }
    return null;
  }
	protected abstract void validate_attributes();
	protected abstract String api_url();
	protected abstract Map<String, Object> create_body() throws Exception;
  protected abstract BaseObject parse(Map<String, Object> responseMap);
  protected abstract void assign_attributes(Map<String, Object> data);
}