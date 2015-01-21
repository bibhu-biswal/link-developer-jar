package com.hp.livepaper;

import java.util.HashMap;
import java.util.Map;
import org.boon.json.JsonFactory;

public class ShortTrigger extends Trigger {
  private static String DEFAULT_SUBSCRIPTION = "month";
  public String getShortUrl() {
    return getLinks().get("shortURL");
  }
  public ShortTrigger(LivePaperSession lp, String name) {
    this.lp = lp;
    this.setName(name);
  }
  public ShortTrigger(LivePaperSession lp, Map<String, Object> map) {
    this.lp = lp;
    this.assign_attributes(map);
  }
  public static ShortTrigger create(LivePaperSession lp, String name) throws LivePaperException {
    return (new ShortTrigger(lp, name)).save();
  }
  @Override
  public ShortTrigger save() throws LivePaperException {
    return (ShortTrigger) super.save();
  }
  @Override
  protected ShortTrigger parse(Map<String, Object> responseMap) {
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
  protected Map<String, Object> create_body() {
    Map<String, Object> body = new HashMap<String, Object>();
    Map<String, Object> trigger = new HashMap<String, Object>();
    Map<String, Object> subscription = new HashMap<String, Object>();
    body.put("trigger", trigger);
    trigger.put("name", getName());
    trigger.put("type", "shorturl");
    subscription.put("package", DEFAULT_SUBSCRIPTION);
    trigger.put("subscription", subscription);
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  @Override
  protected void assign_attributes(Map<String, Object> data) {
    super.assign_attributes(data);
    //@formatter:off
    /*{
       type=shorturl,
       id=kacy0vOFR6-Od3SSiO_WNw,
       name=trigger,
       state=ACTIVE,
       dateCreated=2015-01-13T22:02:54.000+0000,
       dateModified=2015-01-13T22:02:54.000+0000,
       startDate=2015-01-13T22:02:54.952+0000,
       endDate=2017-01-14T03:51:40.952+0000,
       link=[
         {href=http://hpgo.co/jQeT6O,
          rel=shortURL},
         {href=https://www.livepaperapi.com/analytics/v1/triggers/kacy0vOFR6-Od3SSiO_WNw,
          rel=analytics},
         {href=https://www.livepaperapi.com/api/v1/triggers/kacy0vOFR6-Od3SSiO_WNw,
          rel=self}],
       subscription={
         expiryDate=2017-01-14T03:51:40.952+0000,
         startDate=2015-01-13T22:02:54.952+0000},
       typeFriendly=false
      }*/
    //@formatter:on
  }
}
