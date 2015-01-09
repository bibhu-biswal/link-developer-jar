package com.hp.livepaper;

import java.util.HashMap;
import java.util.Map;
import org.boon.json.JsonFactory;

public class ShortTrigger extends Trigger {
  private static String DEFAULT_SUBSCRIPTION = "month";
  public String getShortUrl() {
    return getLinks().get("shortURL");
  }
  public ShortTrigger(String name) {
    this.setName(name);
  }
  public static ShortTrigger create(String name) throws Exception {
    return (new ShortTrigger(name)).save();
  }
  public ShortTrigger save() throws Exception {
    return (ShortTrigger) super.save();
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
    if (getName() == null)
      throw new IllegalArgumentException("Invalid state for this operation! (missing attribute: name)");
  }
  @Override
  protected Map<String, Object> create_body() throws Exception {
    Map<String, Object> body = new HashMap<String, Object>();
    Map<String, Object> trigger = new HashMap<String, Object>();
    Map<String, Object> subscription = new HashMap<String, Object>();
    body.put("trigger", trigger);
    trigger.put("name", getName());
    trigger.put("type", "shorturl");
    subscription.put("package", DEFAULT_SUBSCRIPTION);
    trigger.put("subscription", subscription);
    // if (options != null) {
    // trigger.put(optionName, options);
    // }
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  @Override
  protected void assign_attributes(Map<String, Object> data) {
    super.assign_attributes(data);
  }
}
