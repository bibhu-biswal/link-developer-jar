package com.hp.livepaper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import com.hp.livepaper.LivePaperSession.Method;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;

public abstract class BaseObject {
  public static final String LP_API_HOST = "https://www.livepaperapi.com";
  public static final String AUTH_URL    = LP_API_HOST + "/auth/token";
  private String             id          = null;
  public String getId() {
    return id;
  }
  protected void setId(String id) {
    this.id = id;
  }
  private String name = "";
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  private String date_created = null;
  public String getDateCreated() {
    return date_created;
  }
  protected void setDateCreated(String date_created) {
    this.date_created = date_created;
  }
  private String date_modified = null;
  public String getDateModified() {
    return date_modified;
  }
  protected void setDateModified(String date_modified) {
    this.date_modified = date_modified;
  }
  private Map<String, String> links = new HashMap<String, String>();
  public Map<String, String> getLinks() {
    return links;
  }
  public BaseObject save() throws LivePaperException {
    validate_attributes();
    if (getId() == null || getId().length() == 0) {
      Map<String, Object> response = rest_request_post(api_url(), Method.POST, create_body());
      parse(response);
    }
    return this;
  }
  public Map<String, Object> rest_request_post(String url, Method method, Map<String, Object> bodyMap) throws LivePaperException {
    // TODO: support "x_user_info: app=live_paper_jar" (so that API can track the source of the API calls)
    int responseCode = -1;
    int maxTries = LivePaperSession.getNetworkErrorRetryCount();
    int tries = 0;
    ObjectMapper mapper = JsonFactory.create();
    String body = mapper.writeValueAsString(bodyMap);
    Builder webResource = null;
    if ( this instanceof WmTrigger /*and this is an image download*/) {
      webResource = LivePaperSession.createWebResourceUnTagged(url);
    } else {
      webResource = LivePaperSession.createWebResource(url);
    }
    ClientResponse response = null;
    while (true) {
      try {
        if (method == Method.POST) {
          response = webResource.
              header("Content-Type", "application/json").
              accept("application/json").
              header("Authorization", LivePaperSession.getLppAccessToken()).
              post(ClientResponse.class, body);
        }
        responseCode = response.getStatus();
        if (responseCode == 401) { // authentication problem
          tries++;
          if (tries > maxTries)
            throw new LivePaperException("Unable to create object with POST! (after " + (tries - 1) + " tries)");
          continue;
        }
        break;
      }
      catch (com.sun.jersey.api.client.ClientHandlerException e) {
        tries++;
        if (tries > maxTries)
          throw new LivePaperException("Unable to create object with POST! (after " + (tries - 1) + " tries)");
        System.err.println("Warning: Network error! retrying (" + tries + " of "+maxTries+")...");
        System.err.println("  (error was \"" + e.getMessage() + "\")");
        try {
          Thread.sleep(LivePaperSession.getRetrySleepPeriod());
        }
        catch (InterruptedException e1) {
          throw e;
        }
        continue;
       }
    }
    if (responseCode == 201) {
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
  protected abstract Map<String, Object> create_body();
  protected abstract BaseObject parse(Map<String, Object> responseMap);
  @SuppressWarnings("unchecked")
  protected void assign_attributes(Map<String, Object> data) {
    setId((String) data.get("id"));
    setDateCreated((String) data.get("dateCreated"));
    setDateModified((String) data.get("dateModified"));
    for (Map<String, String> map : (List<Map<String, String>>) data.get("link"))
      getLinks().put(map.get("rel"), map.get("href"));
  }
}