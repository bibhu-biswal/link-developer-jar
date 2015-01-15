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
/*
  private String url = null;
  private void setUrl(String url) {
    this.url = url;
  }
  public String getUrl() {
    return url;
  }
*/
  private Trigger trigger = null;
  private void setTrigger(Trigger trigger) {
    this.trigger = trigger;
  }
  public Trigger getTrigger() {
    return trigger;
  }
  private Payoff payoff = null;
  private void setPayoff(Payoff payoff) {
    this.payoff = payoff;
  }
  public Payoff getPayoff() {
    return payoff;
  }
  // Constructor
  public Link(String name, Trigger trigger, Payoff payoff) {
    setName(name);
    setTrigger(trigger);
    setPayoff(payoff);
  }
  public Link(Map<String, Object> map) {
    this.assign_attributes(map);
  }
  public static Link create(String name, Trigger trigger, Payoff payoff) throws Exception {
    return (new Link(name, trigger, payoff)).save();
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
    if (getTrigger() == null)
      sb.append("Trigger, ");
    if (getPayoff() == null)
      sb.append("Payoff, ");
    if ( sb.length() > 0 ) {
      sb.setLength(sb.length() - 2);
      throw new IllegalArgumentException("Invalid state for this operation! (missing attributes: "+sb.toString()+")");
    }
  }
  @Override
  protected Map<String, Object> create_body() {
    Map<String, Object> linkContent = new HashMap<String, Object>();
    linkContent.put("name", this.getTrigger().getName());
    linkContent.put("triggerId", this.getTrigger().getId());
    linkContent.put("payoffId",  this.getPayoff().getId());
    Map<String, Object> body = new HashMap<String, Object>();
    body.put("link", linkContent);
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  @Override
  protected void assign_attributes(Map<String, Object> data) {
    super.assign_attributes(data);
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
