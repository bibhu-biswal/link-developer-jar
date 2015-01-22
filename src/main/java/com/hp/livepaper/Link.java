package com.hp.livepaper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.boon.json.JsonFactory;
import com.hp.livepaper.LivePaperSession.Method;

public class Link extends BaseObject {
  public static final String API_URL = LivePaper.API_HOST + "links";
  // public static methods
  public static String getItemKey() {
    return "link";
  }
  public static String getListKey() {
    return "links";
  }
  // member fields
  private Trigger trigger = null;
  private void setTrigger(Trigger trigger) {
    if (getTriggerId().length() > 0 && !(trigger.getId().equals(getTriggerId())))
      throw new IllegalStateException("Link.setTrigger() called (for trigger id of " + trigger.getId() + "), but the Link already has a different TriggerId! [id:" + getTriggerId() + "]");
    this.trigger = trigger;
  }
  public Trigger getTrigger() throws LivePaperException {
    if (trigger == null && getTriggerId().length() > 0)
      trigger = Trigger.get(lp, getTriggerId());
    return trigger;
  }
  private String triggerId = "";
  public String getTriggerId() {
    if (trigger != null)
      return trigger.getId();
    return triggerId;
  }
  public void setTriggerId(String triggerId) {
    if (trigger != null) {
      if (trigger.getId().equals(triggerId))
        return;
      throw new IllegalStateException("Link.setTriggerId(" + triggerId + ") called, but the Link already has a Trigger! [with id:" + trigger.getId() + "]");
    }
    this.triggerId = triggerId;
  }
  private String payoffId = "";
  public String getPayoffId() {
    if (payoff != null)
      return payoff.getId();
    return payoffId;
  }
  public void setPayoffId(String payoffId) {
    if (payoff != null) {
      if (payoff.getId().equals(payoffId))
        return;
      throw new IllegalStateException("Link.setPayoffId(" + payoffId + ") called, but the Link already has a Payoff! [with id:" + payoff.getId() + "]");
    }
    this.payoffId = payoffId;
  }
  private Payoff payoff = null;
  private void setPayoff(Payoff payoff) {
    if (getPayoffId().length() > 0 && !(payoff.getId().equals(getPayoffId())))
      throw new IllegalStateException("Link.setPayoff() called (for payoff id of " + payoff.getId() + "), but the Link already has a different PayoffId! [id:" + getPayoffId() + "]");
    this.payoff = payoff;
  }
  public Payoff getPayoff() throws LivePaperException {
    if (payoff == null && getPayoffId().length() > 0)
      payoff = Payoff.get(lp, getPayoffId());
    return payoff;
  }
  // Constructor
  public Link(LivePaperSession lp, String name, Trigger trigger, Payoff payoff) {
    this.lp = lp;
    setName(name);
    setTrigger(trigger);
    setPayoff(payoff);
  }
  public Link(LivePaperSession lp, String name, String triggerId, String payoffId) {
    this.lp = lp;
    setName(name);
    setTriggerId(triggerId);
    setPayoffId(payoffId);
  }
  public Link(Map<String, Object> map) {
    this.assign_attributes(map);
  }
  private Link(LivePaperSession lp, String id) throws LivePaperException {
    this(lp.rest_request(API_URL + "/" + id, Method.GET));
  }
  public static Link get(LivePaperSession lp, String id) throws LivePaperException {
    try {
      return new Link(lp, id);
    }
    catch (LivePaperException e) {
      throw new LivePaperException("Cannot create " + LivePaperSession.capitalize(getItemKey()) + " object with ID of \"" + id + "\"! " + e.getMessage(), e);
    }
  }
  public static Link create(LivePaperSession lp, String name, Trigger trigger, Payoff payoff) throws LivePaperException {
    return (new Link(lp, name, trigger, payoff)).save();
  }
  public static Link create(LivePaperSession lp, String name, String triggerId, String payoffId) throws LivePaperException {
    return (new Link(lp, name, triggerId, payoffId)).save();
  }
  // Overrides
  @Override
  protected String api_url() {
    return API_URL;
  }
  @Override
  public Link save() throws LivePaperException {
    if (trigger.getId().length() == 0)
      trigger.save();
    if (payoff.getId().length() == 0)
      payoff.save();
    return (Link) super.save();
  }
  @Override
  protected BaseObject parse(Map<String, Object> responseMap) {
    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) responseMap.get(getItemKey());
    assign_attributes(data);
    return this;
  }
  @Override
  protected void validate_attributes() {
    StringBuilder sb = new StringBuilder();
    if (getName().length() == 0)
      sb.append("name, ");
    if (trigger == null && getTriggerId().length() == 0)
      sb.append("Trigger, ");
    if (payoff == null && getPayoffId().length() == 0)
      sb.append("Payoff, ");
    if (sb.length() > 0) {
      sb.setLength(sb.length() - 2);
      throw new IllegalArgumentException("Invalid state for this operation! (missing attributes: " + sb.toString() + ")");
    }
  }
  @Override
  protected Map<String, Object> create_body() {
    Map<String, Object> linkContent = new HashMap<String, Object>();
    linkContent.put("name", trigger.getName());
    linkContent.put("triggerId", trigger.getId());
    linkContent.put("payoffId", payoff.getId());
    Map<String, Object> body = new HashMap<String, Object>();
    body.put("link", linkContent);
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  @Override
  protected Map<String, Object> update_body() {
    Map<String, Object> linkContent = new HashMap<String, Object>();
    linkContent.put("name", trigger.getName());
    Map<String, Object> body = new HashMap<String, Object>();
    body.put("link", linkContent);
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  @Override
  protected void assign_attributes(Map<String, Object> data) {
    super.assign_attributes(data);
    setTriggerId((String) data.get("triggerId"));
    setPayoffId((String) data.get("payoffId"));
    //@formatter:off
    /*{
       id=B6GFx2ZGSkSZr884L3MziA,
       name=My ShortTrigger,
       dateCreated=2015-01-21T03:01:48.643+0000,
       dateModified=2015-01-21T03:01:48.643+0000,
       payoffId=tjNPTElRSmukFO9u74eSBw,
       triggerId=46YCC30lS8ur4c_cuKx0BQ,
       link=[
         {href=https://www.livepaperapi.com/api/v1/links/B6GFx2ZGSkSZr884L3MziA,
          rel=self},
         {href=https://www.livepaperapi.com/analytics/v1/links/B6GFx2ZGSkSZr884L3MziA,
          rel=analytics},
         {href=https://www.livepaperapi.com/api/v1/payoffs/tjNPTElRSmukFO9u74eSBw,
          rel=payoff},
         {href=https://www.livepaperapi.com/api/v1/triggers/46YCC30lS8ur4c_cuKx0BQ,
          rel=trigger}]
      }*/
    //@formatter:on
  }
  @SuppressWarnings("unchecked")
  public static Map<String, Link> list(LivePaperSession lp) throws LivePaperException {
    Map<String, Link> links = new HashMap<String, Link>();
    Map<String, Object> listOfLinks = lp.rest_request(Link.API_URL, Method.GET);
    for (Map<String, Object> linkData : (List<Map<String, Object>>) listOfLinks.get(getListKey())) {
      Link tr = new Link(linkData);
      links.put(tr.getId(), tr);
    }
    return links;
  }
}
