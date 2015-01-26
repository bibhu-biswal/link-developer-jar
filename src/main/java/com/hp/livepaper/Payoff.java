package com.hp.livepaper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.boon.json.JsonFactory;
import com.hp.livepaper.LivePaperSession.Method;

public class Payoff extends BaseObject {
  protected static final String API_URL = LivePaper.API_HOST + "payoffs";
  protected static final String ITEM_KEY = "payoff";
  protected static final String LIST_KEY = "payoffs";
  public enum Type { WEB_PAYOFF, /* RICH_PAYOFF, */ UNINITIALIZED }
  /**
   * Returns a Map of all the Payoff objects for the given account.  The Map uses the Id of the object as the key.
   * The value in the Map is the Payoff object itself.
   * @param lp is the LivePaperSession (which holds the access token for the user)
   * @return Returns a Map of all Payoff objects.
   * @throws LivePaperException
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Payoff> list(LivePaperSession lp) throws LivePaperException {
    Map<String, Payoff> payoffs = new HashMap<String, Payoff>();
    Map<String, Object> listOfPayoffs = lp.rest_request(Payoff.API_URL, Method.GET);
    for (Map<String, Object> payoffData : (List<Map<String, Object>>) listOfPayoffs.get(LIST_KEY)) {
      Payoff tr = new Payoff(lp, payoffData);
      payoffs.put(tr.getId(), tr);
    }
    return payoffs;
  }
  /**
   * Obtains a Payoff object, given the id of the object.
   * @param lp is the LivePaperSession (which holds the access token for the user)
   * @param id is the identifier for an existing Payoff object.
   * @return The Payoff object represented by the id is returned.
   * @throws LivePaperException
   */
  public static Payoff get(LivePaperSession lp, String id) throws LivePaperException {
    try {
      return new Payoff(lp, lp.rest_request(API_URL + "/" + id, Method.GET));
    }
    catch (LivePaperException e) {
      throw new LivePaperException("Cannot create " + LivePaperSession.capitalize(ITEM_KEY) + " object with ID of \"" + id + "\"! " + e.getMessage(), e);
    }
  }
  /**
   * Creates a Payoff object via a REST API POST call to the Live Paper API
   * @param lp is the LivePaperSession (which holds the access token for the user)
   * @param name is the name attribute to be given to the Link object.
   * @param type indicates the Type of the Payoff to be created.
   * @param url is the URL "payoff" that is (indirectly) associated with the Trigger (through the Link object)
   * @return Returns a new Payoff object.
   * @throws LivePaperException
   */
  public static Payoff create(LivePaperSession lp, String name, Type type, String url) throws LivePaperException {
    return (new Payoff(lp, name, type, url)).save();
  }
  public String  getUrl() {
    return url;
  }
  public void    setUrl(String url) {
    this.url = url;
  }
  public Type    getType() {
    return type;
  }
  protected Payoff(LivePaperSession lp, String name, Type type, String url) {
    this.lp = lp;
    setName(name);
    setType(type);
    setUrl(url);
  }
  protected Payoff(LivePaperSession lp, Map<String, Object> map) {
    this.assign_attributes(map);
  }
  protected void setType(Type type) {
    this.type = type;
  }
  @Override
  protected String api_url() {
    return API_URL;
  }
  /**
   * Create this object via the API by doing a POST
   * @return
   * @throws LivePaperException
   */
  @Override
  protected Payoff save() throws LivePaperException {
    return (Payoff) super.save();
  }
  @Override
  protected BaseObject parse(Map<String, Object> responseMap) {
    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) responseMap.get(ITEM_KEY);
    assign_attributes(data);
    // send(present?(data[:richPayoff]) ? :parse_richpayoff : :parse_webpayoff, data) //TODO: Support Rich Payoff
    return this;
  }
  @Override
  protected void validate_attributes() {
    StringBuilder sb = new StringBuilder();
    if (getName().length() == 0)
      sb.append("name, ");
    if (getType() == Type.UNINITIALIZED)
      sb.append("Type, ");
    if (getUrl().length() == 0)
      sb.append("Url, ");
    if (sb.length() > 0) {
      sb.setLength(sb.length() - 2);
      throw new IllegalArgumentException("Invalid state for this operation! (missing attributes: " + sb.toString() + ")");
    }
  }
  @Override
  protected void assign_attributes(Map<String, Object> data) {
    super.assign_attributes(data);
    setUrl((String) data.get("URL"));
    //@formatter:off
    /*{
       id=e2lqC2WGS8a01MwbTmnSjg,
       name=My Payoff
       URL=http://www.hp.com,
       dateCreated=2015-01-21T02:56:17.915+0000,
       dateModified=2015-01-21T02:56:17.915+0000,
       link=[
         {href=https://www.livepaperapi.com/api/v1/payoffs/e2lqC2WGS8a01MwbTmnSjg,
          rel=self},
         {href=https://www.livepaperapi.com/analytics/v1/payoffs/e2lqC2WGS8a01MwbTmnSjg,
         rel=analytics}]
      }*/
    //@formatter:on
  }
  @Override
  protected Map<String, Object> create_body() {
    Map<String, Object> payoff = new HashMap<String, Object>();
    switch (getType()) {
      case UNINITIALIZED:
        throw new IllegalStateException("Payoff.create_body() cannot be called on an unitialized object (Type is still set to UNKNOWN)");
      case WEB_PAYOFF:
        payoff.put("name", getName());
        payoff.put("URL", getUrl());
        /*
         * case RICH_PAYOFF: //TODO: Support Rich Payoff
         * //import javax.xml.bind.DatatypeConverter;
         * Map<String, Object> richPayoffData = new HashMap<String, Object>();
         * richPayoffData.put("content-type", getDataType());
         * String data64 = DatatypeConverter.printBase64Binary((getData()).getBytes("UTF-8"));
         * richPayoffData.put("data", JsonFactory.create().writeValueAsString(data64)); // not sure about that jsonfactory call...
         * Map<String, Object> richPayoffBody = new HashMap<String, Object>();
         * richPayoffBody.put("version", "1");
         * richPayoffBody.put("private", richPayoffData);
         * richPayoffBody.put("public", getUrl());
         * body.put("name", getName());
         * body.put("richPayoff", richPayoffBody);
         */
    }
    Map<String, Object> body = new HashMap<String, Object>();
    body.put("payoff", payoff);
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  @Override
  protected Map<String, Object> update_body() {
    return create_body();
  }
  private String url = "";
  private Type type = Type.UNINITIALIZED;
}