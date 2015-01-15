package com.hp.livepaper;

import java.util.HashMap;
import java.util.Map;
import org.boon.json.JsonFactory;

public class QrTrigger extends Trigger {
  private static String DEFAULT_SUBSCRIPTION = "month";
  public QrTrigger(String name) {
    this.setName(name);
  }
  public QrTrigger(Map<String, Object> map) {
    this.assign_attributes(map);
  }
  public static QrTrigger create(String name) throws Exception {
    return (new QrTrigger(name)).save();
  }
  public QrTrigger save() throws LivePaperException {
    return (QrTrigger) super.save();
  }
  @Override
  protected QrTrigger parse(Map<String, Object> responseMap) {
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
    trigger.put("type", "qrcode");
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
       type=qrcode,
       id=vL5It1V1TtenOeaqxJCqoQ,
       name=trigger,
       state=ACTIVE,
       dateCreated=2015-01-07T19:49:32.000+0000,
       dateModified=2015-01-07T19:49:32.000+0000,
       startDate=2015-01-07T19:49:32.001+0000,
       endDate=2017-01-08T01:38:18.001+0000,
       link=[
         {href=https://www.livepaperapi.com/api/v1/triggers/vL5It1V1TtenOeaqxJCqoQ/image,
          rel=image},
         {href=https://www.livepaperapi.com/analytics/v1/triggers/vL5It1V1TtenOeaqxJCqoQ,
          rel=analytics},
         {href=https://www.livepaperapi.com/api/v1/triggers/vL5It1V1TtenOeaqxJCqoQ,
          rel=self}],
       subscription={
         expiryDate=2017-01-08T01:38:18.001+0000,
         startDate=2015-01-07T19:49:32.001+0000}
      }*/
  //@formatter:on
  }
  public byte[] downloadQrCode() throws LivePaperException {
    return LivePaperSession.getImageBytes("image/png", this.getLinks().get("image") + "?width=200");
  }
}
