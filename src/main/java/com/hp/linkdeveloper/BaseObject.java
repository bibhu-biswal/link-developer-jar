package com.hp.linkdeveloper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hp.linkdeveloper.LinkDeveloperSession.Method;

public abstract class BaseObject {
  public String  getId() {
    return id;
  }
  public String getName() {
    return name;
  }
  public void   setName(String name) {
    this.name = name;
  }
  public String getDateCreated() {
    return date_created;
  }
  public String getDateModified() {
    return date_modified;
  }
  /**
   * @return Map of REST API urls available to manipulate this object
   */
  public Map<String, String> getLinks() {
    return links;
  }
  /**
   * Update this object via the API by doing a PUT
   * @return
   * @throws LinkDeveloperException
   */
  public BaseObject update() throws LinkDeveloperException {
    if (getId().length() == 0)
      throw new IllegalStateException("update() method cannot be called on an unititialized " + this.getClass().getName() + " object!");
    validate_attributes();
    Map<String, Object> response = ld.rest_request(api_url()+ "/" + getId(), Method.PUT, update_body());
    if (response == null)
      throw new LinkDeveloperException("Unable to update new " + this.getClass().getName() + " object!");
    parse(response);
    return this;
  }
  /**
   * Delete this object via the API by doing a DELETE
   * @return
   * @throws LinkDeveloperException
   */
  public void delete() throws LinkDeveloperException {
    if (getId().length() == 0)
      throw new IllegalStateException("delete() method cannot be called on an unititialized " + this.getClass().getName() + " object!");
    ld.rest_request(this.api_url() + "/" + getId(), Method.DELETE);
    setId("");
  }
  /**
   * Create this object via the API by doing a POST
   * @return
   * @throws LinkDeveloperException
   */
  protected BaseObject save() throws LinkDeveloperException {
    validate_attributes();
    if (getId().length() == 0) {
      Map<String, Object> response = ld.rest_request(api_url(), Method.POST, create_body());
      if (response == null) // this can happen if you are at your limit in creating resources
        throw new LinkDeveloperException("Unable to create new " + this.getClass().getName() + " object!");
      parse(response);
    }
    return this;
  }
  protected void setId(String id) {
    this.id = id;
  }
  protected void setDateCreated(String date_created) {
    this.date_created = date_created;
  }
  protected void setDateModified(String date_modified) {
    this.date_modified = date_modified;
  }
  protected abstract void validate_attributes();
  protected abstract String api_url();
  protected abstract Map<String, Object> create_body();
  protected abstract Map<String, Object> update_body();
  protected abstract BaseObject parse(Map<String, Object> responseMap);
  @SuppressWarnings("unchecked")
  protected void assign_attributes(Map<String, Object> map) {
    //@formatter:off
    /*{
       id=e2lqC2WGS8a01MwbTmnSjg,
       name=My Payoff
       dateCreated=2015-01-21T02:56:17.915+0000,
       dateModified=2015-01-21T02:56:17.915+0000,
       link=[
         {href=https://www.livepaperapi.com/api/v1/payoffs/e2lqC2WGS8a01MwbTmnSjg,
          rel=self},
         {href=https://www.livepaperapi.com/analytics/v1/payoffs/e2lqC2WGS8a01MwbTmnSjg,
         rel=analytics}]
      }*/
    //@formatter:on
    setId((String) map.get("id"));
    setName((String) map.get("name"));
    setDateCreated((String) map.get("dateCreated"));
    setDateModified((String) map.get("dateModified"));
    for (Map<String, String> list : (List<Map<String, String>>) map.get("link"))
      getLinks().put(list.get("rel"), list.get("href"));
  }
  protected LinkDeveloperSession ld = null;
  private String id = "";
  private String name = "";
  private String date_created = "";
  private String date_modified = "";
  private Map<String, String> links = new HashMap<String, String>();
}