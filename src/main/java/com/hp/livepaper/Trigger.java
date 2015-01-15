package com.hp.livepaper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hp.livepaper.LivePaperSession.Method;

public abstract class Trigger extends BaseObject {
  public static final String API_URL = LivePaperSession.LP_API_HOST + "/api/v1/" + "triggers";
  public static Trigger create(Map<String, Object> map) {
    String type = (String)map.get("type");
    if ( type .equals( "shorturl" ))
      return new ShortTrigger(map);
    if ( type .equals( "qrcode" ))
      return new QrTrigger(map);
    if ( type .equals( "watermark" ))
      return new WmTrigger(map);
    throw new IllegalArgumentException("Trigger.create() passed data that does not represent a Trigger!");
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
  protected abstract void validate_attributes();
  @SuppressWarnings("unchecked")
  public static Map<String,Trigger> list() throws LivePaperException {
    Map<String,Trigger> triggers = new HashMap<String,Trigger>();
    Map<String, Object> listOfTriggers = LivePaperSession.rest_request(Trigger.API_URL, Method.GET);
    for (Map<String, Object> triggerData : (List<Map<String, Object>>) listOfTriggers.get(getListKey())) {
      Trigger tr = Trigger.create(triggerData);
      triggers.put(tr.getId(),tr);
    }
    return triggers;
  }
}
