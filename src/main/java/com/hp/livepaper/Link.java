package com.hp.livepaper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.boon.json.JsonFactory;
import com.hp.livepaper.LivePaperSession.Method;

public class Link extends BaseObject {
  public static final String API_URL = LivePaperSession.LP_API_HOST + "/api/v1/" + "links";
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
    if ( getTriggerId().length() > 0 && ! ( trigger.getId() .equals(getTriggerId()) ) )
      throw new IllegalStateException("Link.setTrigger() called (for trigger id of "+trigger.getId()+"), but the Link already has a different TriggerId! [id:"+getTriggerId()+"]");
    this.trigger = trigger;
  }
  public Trigger getTrigger() throws LivePaperException {
    if ( trigger == null && getTriggerId().length() > 0 )
      trigger = Trigger.get(getTriggerId());
    return trigger;
  }
  private String triggerId = "";
  public String getTriggerId() {
    if ( trigger != null )
      return trigger.getId();
    return triggerId;
  }
  public void setTriggerId(String triggerId) {
    if ( trigger != null ) {
      if ( trigger.getId() .equals (triggerId) )
        return;
      throw new IllegalStateException("Link.setTriggerId("+triggerId+") called, but the Link already has a Trigger! [with id:"+trigger.getId()+"]");
    }
    this.triggerId = triggerId;
  }
  private String payoffId = "";
  public String getPayoffId() {
    if ( payoff != null )
      return payoff.getId();
    return payoffId;
  }
  public void setPayoffId(String payoffId) {
    if ( payoff != null ) {
      if ( payoff.getId() .equals (payoffId) )
        return;
      throw new IllegalStateException("Link.setPayoffId("+payoffId+") called, but the Link already has a Payoff! [with id:"+payoff.getId()+"]");
    }
    this.payoffId = payoffId;
  }
  private Payoff payoff = null;
  private void setPayoff(Payoff payoff) {
    if ( getPayoffId().length() > 0 && ! ( payoff.getId() .equals(getPayoffId()) ) )
      throw new IllegalStateException("Link.setPayoff() called (for payoff id of "+payoff.getId()+"), but the Link already has a different PayoffId! [id:"+getPayoffId()+"]");
    this.payoff = payoff;
  }
  public Payoff getPayoff() throws LivePaperException {
    if ( payoff == null && getPayoffId().length() > 0 )
      payoff = Payoff.get(getPayoffId());
    return payoff;
  }
  // Constructor
  public Link(String name, Trigger trigger, Payoff payoff) {
    setName(name);
    setTrigger(trigger);
    setPayoff(payoff);
  }
  public Link (String name, String triggerId, String payoffId) {
    setName(name);
    setTriggerId(triggerId);
    setPayoffId(payoffId);
  }
  public Link(Map<String, Object> map) {
    this.assign_attributes(map);
  }
  public static Link create(String name, Trigger trigger, Payoff payoff) throws LivePaperException {
    return (new Link(name, trigger, payoff)).save();
  }
  public static Link create(String name, String triggerId, String payoffId) throws LivePaperException {
    return (new Link(name, triggerId, payoffId)).save();
  }
  // Overrides
  @Override
  protected String api_url() {
    return API_URL;
  }
  public Link save() throws LivePaperException {
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
    if (getName() == null)
      sb.append("name, ");
    if (trigger == null && getTriggerId().length() == 0 )
      sb.append("Trigger, ");
    if (payoff == null && getPayoffId().length() == 0 )
      sb.append("Payoff, ");
    if ( sb.length() > 0 ) {
      sb.setLength(sb.length() - 2);
      throw new IllegalArgumentException("Invalid state for this operation! (missing attributes: "+sb.toString()+")");
    }
  }
  @Override
  protected Map<String, Object> create_body() {
    Map<String, Object> linkContent = new HashMap<String, Object>();
    linkContent.put("name", trigger.getName());
    linkContent.put("triggerId", trigger.getId());
    linkContent.put("payoffId",  payoff.getId());
    Map<String, Object> body = new HashMap<String, Object>();
    body.put("link", linkContent);
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  @Override
  protected void assign_attributes(Map<String, Object> data) {
    super.assign_attributes(data);
    setTriggerId((String)data.get("triggerId"));
    setPayoffId((String)data.get("payoffId"));
  }
  @SuppressWarnings("unchecked")
  public static Map<String,Link> list() throws LivePaperException {
    Map<String,Link> links = new HashMap<String,Link>();
    Map<String, Object> listOfLinks = LivePaperSession.rest_request(Link.API_URL, Method.GET);
    for (Map<String, Object> linkData : (List<Map<String, Object>>) listOfLinks.get(getListKey())) {
      Link tr = new Link(linkData);
      links.put(tr.getId(),tr);
    }
    return links;
  }
}
