package com.hp.livepaper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hp.livepaper.LivePaperSession.Method;

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
      Map<String, Object> response = LivePaperSession.rest_request(api_url(), Method.POST, create_body());
      if ( response == null ) // this can happen if you are at your limit in creating resources
        throw new LivePaperException("Unable to create new "+this.getClass().getName()+" object!");
      parse(response);
    }
    return this;
  }
  protected abstract void validate_attributes();
  protected abstract String api_url();
  protected abstract Map<String, Object> create_body();
  protected abstract BaseObject parse(Map<String, Object> responseMap);
  @SuppressWarnings("unchecked")
  protected void assign_attributes(Map<String, Object> map) {
    setId((String) map.get("id"));
    setName((String) map.get("name"));
    setDateCreated((String) map.get("dateCreated"));
    setDateModified((String) map.get("dateModified"));
    for (Map<String, String> list : (List<Map<String, String>>) map.get("link"))
      getLinks().put(list.get("rel"), list.get("href"));
  }
}