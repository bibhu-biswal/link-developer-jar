package com.hp.livepaper;

import java.util.HashMap;
import java.util.Map;
import org.boon.json.JsonFactory;

public class QrTrigger extends Trigger {
  private static final String DEFAULT_SUBSCRIPTION = "month";
  /**
   * Creates a QrTrigger object via a REST API POST call to the Live Paper API
   * @param lp is the LivePaperSession (which holds the access token for the user)
   * @param name is the name attribute to be given to the QrTrigger object.
   * @return Returns a new QrTrigger object.
   * @throws LivePaperException
   */
  public static QrTrigger create(LivePaperSession lp, String name) throws LivePaperException {
    return (new QrTrigger(lp, name)).save();
  }
  /**
   * Download the QR Code image (at the API's default image size)
   * @return byte array holding the contents of the image ready to be saved to disk, or displayed, etc.
   * @throws LivePaperException
   */
  public byte[] downloadQrCode() throws LivePaperException {
    return downloadQrCode(0);
  }
  /**
   * Download the QR Code image, at a specific size
   * @param width must be greater than zero. Any other integer value will be ignored (and API's default size will be used).
   * @return byte array holding the contents of the image ready to be saved to disk, or displayed, etc.
   * @throws LivePaperException
   */
  public byte[] downloadQrCode(int width) throws LivePaperException {
    String params = "";
    if (width > 0)
      params = "?width=" + width;
    return ImageStorage.download(lp, "image/png", this.getLinks().get("image") + params);
  }
  protected QrTrigger(LivePaperSession lp, String name) {
    this.lp = lp;
    this.setName(name);
  }
  protected QrTrigger(LivePaperSession lp, Map<String, Object> map) {
    this.lp = lp;
    this.assign_attributes(map);
  }
  /**
   * Create this object via the API by doing a POST
   * @return
   * @throws LivePaperException
   */
  @Override
  protected QrTrigger save() throws LivePaperException {
    return (QrTrigger) super.save();
  }
  @Override
  protected QrTrigger parse(Map<String, Object> responseMap) {
    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) responseMap.get(ITEM_KEY);
    assign_attributes(data);
    return this;
  }
  @Override
  protected void validate_attributes() {
    if (getName() == null)
      throw new IllegalArgumentException("Invalid state for this operation! (missing attribute: name)");
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
}
