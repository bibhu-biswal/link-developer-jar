package com.hp.livepaper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hp.livepaper.LivePaperSession.Method;

public abstract class Trigger extends BaseObject {
  public static final String API_URL = LivePaper.API_HOST + "triggers";
  public static Trigger create(LivePaperSession lp, Map<String, Object> map) {
    String type = (String) map.get("type");
    if (type.equals("shorturl"))
      return new ShortTrigger(lp, map);
    if (type.equals("qrcode"))
      return new QrTrigger(lp, map);
    if (type.equals("watermark"))
      return new WmTrigger(lp, map);
    throw new IllegalArgumentException("Trigger.create() passed data that does not represent a Trigger!");
  }
  public static Trigger get(LivePaperSession lp, String id) throws LivePaperException {
    try {
      return create(lp, lp.rest_request(API_URL + "/" + id, Method.GET));
    }
    catch (LivePaperException e) {
      throw new LivePaperException("Cannot create " + LivePaperSession.capitalize(getItemKey()) + " object with ID of \"" + id + "\"! " + e.getMessage(), e);
    }
  }
  @Override
  protected String api_url() {
    return API_URL;
  }
  public static String getItemKey() {
    return "trigger";
  }
  public static String getListKey() {
    return "triggers";
  }
  private String state = "";
  public String getState() {
    return state;
  }
  public void setState(String state) {
    this.state = state;
  }
  private String startDate = "";
  public String getStartDate() {
    return startDate;
  }
  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }
  private String endDate = "";
  public String getEndDate() {
    return endDate;
  }
  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }
  private String subscriptionStartDate = "";
  public String getSubscriptionStartDate() {
    return subscriptionStartDate;
  }
  public void setSubscriptionStartDate(String subscriptionStartDate) {
    this.subscriptionStartDate = subscriptionStartDate;
  }
  private String subscriptionExpiryDate = "";
  public String getSubscriptionExpiryDate() {
    return subscriptionExpiryDate;
  }
  public void setSubscriptionExpiryDate(String subscriptionExpiryDate) {
    this.subscriptionExpiryDate = subscriptionExpiryDate;
  }
  @Override
  protected abstract void validate_attributes();
  @SuppressWarnings("unchecked")
  public static Map<String, Trigger> list(LivePaperSession lp) throws LivePaperException {
    Map<String, Trigger> triggers = new HashMap<String, Trigger>();
    Map<String, Object> listOfTriggers = lp.rest_request(Trigger.API_URL, Method.GET);
    for (Map<String, Object> triggerData : (List<Map<String, Object>>) listOfTriggers.get(getListKey())) {
      Trigger tr = Trigger.create(lp, triggerData);
      triggers.put(tr.getId(), tr);
    }
    return triggers;
  }
  @SuppressWarnings("unchecked")
  @Override
  protected void assign_attributes(Map<String, Object> data) {
    super.assign_attributes(data);
    setState((String) data.get("state"));
    setStartDate((String) data.get("startDate"));
    setEndDate((String) data.get("endDate"));
    if (data.get("subscription") != null) {
      Map<String, String> subscription = (Map<String, String>) data.get("subscription");
      setSubscriptionStartDate(subscription.get("startDate"));
      setSubscriptionExpiryDate(subscription.get("expiryDate"));
    }
  }
}
