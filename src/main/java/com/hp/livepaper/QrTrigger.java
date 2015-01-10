package com.hp.livepaper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.boon.json.JsonFactory;
import com.sun.jersey.api.client.ClientResponse;

public class QrTrigger extends Trigger {
  private static String DEFAULT_SUBSCRIPTION = "month";
  public String getQrCodeUrl() {
    return getLinks().get("????shortURL????");
  }
  public QrTrigger(String name) {
    this.setName(name);
  }
  public static QrTrigger create(String name) throws Exception {
    return (new QrTrigger(name)).save();
  }
  public QrTrigger save() throws Exception {
    return (QrTrigger) super.save();
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
    trigger.put("type", "qrcode");
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
  public byte[] downloadQrCode() throws LivePaperException {
  //String location = createLink("qrcode", url, "image", null, null) + "?width=200";
    String imageUrl = this.getLinks().get("image") + "?width=200";
    ClientResponse response = com.hp.livepaper.LivePaperSession.createWebResource(imageUrl).
        accept("image/png").
        header("Authorization", com.hp.livepaper.LivePaperSession.getLppAccessToken()).
        get(ClientResponse.class);
    byte[] bytes;
    try {
      bytes = LivePaperSession.inputStreamToByteArray(response.getEntityInputStream());
      return bytes;
    }
    catch (IOException e) {
      throw new com.hp.livepaper.LivePaperException("Failed to download QR code image!", e);
    }
  }
}
