package com.hp.livepaper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.boon.json.JsonFactory;
import com.hp.livepaper.LivePaperSession.Method;

public abstract class Trigger extends BaseObject {
  protected static final String API_URL = LivePaper.API_HOST + "triggers";
  protected static final String ITEM_KEY = "trigger";
  protected static final String LIST_KEY = "triggers";
  public enum State { ACTIVE, DISABLED, INACTIVE, UNINITIALIZED };
  /**
   * Returns a Map of all the Trigger objects for the given account.  The Map uses the Id of the object as the key.
   * The value in the Map is the Trigger object itself.
   * @param lp is the LivePaperSession (which holds the access token for the user)
   * @return Returns a Map of all Link objects.
   * @throws LivePaperException
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Trigger> list(LivePaperSession lp) throws LivePaperException {
    Map<String, Trigger> triggers = new HashMap<String, Trigger>();
    Map<String, Object> listOfTriggers = lp.rest_request(Trigger.API_URL, Method.GET);
    for (Map<String, Object> triggerData : (List<Map<String, Object>>) listOfTriggers.get(LIST_KEY)) {
      Trigger tr = Trigger.create(lp, triggerData);
      triggers.put(tr.getId(), tr);
    }
    return triggers;
  }
  /**
   * Obtains a Trigger object, given the id of the object.
   * @param lp is the LivePaperSession (which holds the access token for the user)
   * @param id is the identifier for an existing Trigger object.
   * @return The Trigger object represented by the id is returned.
   * @throws LivePaperException
   */
  public static Trigger get(LivePaperSession lp, String id) throws LivePaperException {
    try {
      return create(lp, lp.rest_request(API_URL + "/" + id, Method.GET));
    }
    catch (LivePaperException e) {
      throw new LivePaperException("Cannot create " + LivePaperSession.capitalize(ITEM_KEY) + " object with ID of \"" + id + "\"! " + e.getMessage(), e);
    }
  }
  public State  getState() {
    return state;
  }
  public void   setState(State state) {
    if (state == State.INACTIVE || state == State.UNINITIALIZED)
      throw new IllegalArgumentException(
          "State may only be set to ACTIVE or DISABLED (INACTIVE is a valid state, but only the "+
          "API itself sets the state to INACTIVE when the getSubscriptionExpiryDate() has passed)");
    this.state = state;
  }
  public String getStartDate() {
    return startDate;
  }
  public String getEndDate() {
    return endDate;
  }
  public String getSubscriptionStartDate() {
    return subscriptionStartDate;
  }
  public String getSubscriptionExpiryDate() {
    return subscriptionExpiryDate;
  }
  /**
   * Factory method to create a subtype of Trigger.
   * @param lp is the LivePaperSession (which holds the access token for the user)
   * @param map a Map object, containing all the data needed to instantiate a new Trigger object
   * of the specified type.
   * @return Returns a Trigger object, of a concrete subtype class.
   */
  protected static Trigger create(LivePaperSession lp, Map<String, Object> map) {
    String type = (String) map.get("type");
    if (type.equals("shorturl"))
      return new ShortTrigger(lp, map);
    if (type.equals("qrcode"))
      return new QrTrigger(lp, map);
    if (type.equals("watermark"))
      return new WmTrigger(lp, map);
    throw new IllegalArgumentException("Trigger.create() passed data that does not represent a Trigger!");
  }
  protected void   setStartDate(String startDate) {
    this.startDate = startDate;
  }
  protected void   setEndDate(String endDate) {
    this.endDate = endDate;
  }
  protected void   setSubscriptionStartDate(String subscriptionStartDate) {
    this.subscriptionStartDate = subscriptionStartDate;
  }
  protected void   setSubscriptionExpiryDate(String subscriptionExpiryDate) {
    this.subscriptionExpiryDate = subscriptionExpiryDate;
  }
  @Override
  protected String api_url() {
    return API_URL;
  }
  @Override
  protected abstract void validate_attributes();
  @SuppressWarnings("unchecked")
  @Override
  protected void assign_attributes(Map<String, Object> data) {
    super.assign_attributes(data);
    setState(State.valueOf((String) data.get("state")));
    setStartDate((String) data.get("startDate"));
    setEndDate((String) data.get("endDate"));
    if (data.get("subscription") != null) {
      Map<String, String> subscription = (Map<String, String>) data.get("subscription");
      setSubscriptionStartDate(subscription.get("startDate"));
      setSubscriptionExpiryDate(subscription.get("expiryDate"));
    }
  }
  @Override
  protected Map<String, Object> update_body() {
    Map<String, Object> body = new HashMap<String, Object>();
    Map<String, Object> trigger = new HashMap<String, Object>();
    body.put("trigger", trigger);
    trigger.put("name", getName());
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  private State state = State.UNINITIALIZED;
  private String startDate = "";
  private String endDate = "";
  private String subscriptionStartDate = "";
  private String subscriptionExpiryDate = "";
}
