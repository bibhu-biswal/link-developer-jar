package com.hp.livepaper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.boon.json.JsonFactory;
import com.hp.livepaper.LivePaperSession.Method;

public class Payoff extends BaseObject {
  public static final String API_URL = LivePaperSession.LP_API_HOST + "/api/v1/" + "payoffs";
  public enum Type {
    WEB_PAYOFF /* , RICH_PAYOFF; */
  } // TODO: support Rich Payoff
  // public static methods
  public static String getItemKey() {
    return "payoff";
  }
  public static String getListKey() {
    return "payoffs";
  }
  // member fields
  private String url = null;
  private void setUrl(String url) {
    this.url = url;
  }
  public String getUrl() {
    return url;
  }
  private Type type = null;
  private void setType(Type type) {
    this.type = type;
  }
  public Type getType() {
    return type;
  }
  // Constructor
  public Payoff(String name, Type type, String url) {
    setName(name);
    setType(type);
    setUrl(url);
  }
  public Payoff(Map<String, Object> map) {
    this.assign_attributes(map);
  }
  public static Payoff get(String id) throws LivePaperException {
    return new Payoff(LivePaperSession.rest_request(API_URL+"/"+id, Method.GET));
  }
  public static Payoff create(String name, Type type, String url) throws Exception {
    return (new Payoff(name, type, url)).save();
  }
  // Overrides
  @Override
  protected String api_url() {
    return API_URL;
  }
  public Payoff save() throws LivePaperException {
    return (Payoff) super.save();
  }
  @Override
  protected BaseObject parse(Map<String, Object> responseMap) {
    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) responseMap.get(getItemKey());
    assign_attributes(data);
    // send(present?(data[:richPayoff]) ? :parse_richpayoff : :parse_webpayoff, data) //TODO: Support Rich Payoff
    return this;
  }
  @Override
  protected void validate_attributes() {
    StringBuilder sb = new StringBuilder(); 
    if (getName() == null)
      sb.append("name, ");
    if (getType() == null)
      sb.append("Type, ");
    if (getUrl() == null)
      sb.append("Url, ");
    if ( sb.length() > 0 ) {
      sb.setLength(sb.length() - 2);
      throw new IllegalArgumentException("Invalid state for this operation! (missing attributes: "+sb.toString()+")");
    }
  }
  @Override
  protected Map<String, Object> create_body() {
    Map<String, Object> payoff = new HashMap<String, Object>();
    switch (getType()) {
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
  protected void assign_attributes(Map<String, Object> data) {
    super.assign_attributes(data);
    setUrl((String) data.get("URL"));
  }
  @SuppressWarnings("unchecked")
  public static Map<String,Payoff> list() throws LivePaperException {
    Map<String,Payoff> payoffs = new HashMap<String,Payoff>();
    Map<String, Object> listOfPayoffs = LivePaperSession.rest_request(Payoff.API_URL, Method.GET);
    for (Map<String, Object> payoffData : (List<Map<String, Object>>) listOfPayoffs.get(getListKey())) {
      Payoff tr = new Payoff(payoffData);
      payoffs.put(tr.getId(),tr);
    }
    return payoffs;
  }
}
