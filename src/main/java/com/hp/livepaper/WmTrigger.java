package com.hp.livepaper;

import java.util.HashMap;
import java.util.Map;
import org.boon.json.JsonFactory;

public class WmTrigger extends Trigger {
  private static final String DEFAULT_SUBSCRIPTION = "month";
  /**
   * Creates a WmTrigger object via a REST API POST call to the Live Paper API
   * @param lp is the LivePaperSession (which holds the access token for the user)
   * @param name is the name attribute to be given to the WmTrigger object.
   * @param watermark strength value for watermarked image. The allowed value ranges from 1 to 10.
   * @param resolution is the watermark resolution value for the watermarked image.  The allowed value ranges from 1 to 2400.
   * @param urlForImageToBeWatermarked is the URL of the image that you want to be watermarked.
   * @return Returns a new WmTrigger object.
   * @throws LivePaperException
   */
  public static WmTrigger create(LivePaperSession lp, String name, WmTrigger.Strength strength, WmTrigger.Resolution resolution, String urlForImageToBeWatermarked) throws LivePaperException {
    return (new WmTrigger(lp, name, strength, resolution, urlForImageToBeWatermarked)).save();
  }
  /**
   * Allows downloading of the watermarked image that this Trigger represents.
   * @return Returns the byte array containing the image data.
   * @throws LivePaperException
   */
  public byte[]     downloadWatermarkedJpgImage() throws LivePaperException {
    return ImageStorage.download(lp, this, ImageStorage.Type.JPEG);
  }
  public Strength   getStrength() {
    return strength;
  }
  public void       setStrength(Strength strength) {
    if (strength == null)
      throw new IllegalArgumentException("Strength cannot be null.");
    this.strength = strength;
  }
  public Resolution getResolution() {
    return resolution;
  }
  public void       setResolution(Resolution resolution) {
    if (resolution == null)
      throw new IllegalArgumentException("Resolution cannot be null.");
    this.resolution = resolution;
  }
  public String     getImageUrl() {
    return imageUrl;
  }
  public void       setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  public static class Resolution {
    public static final int MIN_RESOLUTION = 1;
    public static final int MAX_RESOLUTION = 2400;
    public Resolution(int resolution) {
      if (resolution < MIN_RESOLUTION || resolution > MAX_RESOLUTION)
        throw new IllegalArgumentException(
            "Resolution must be a positive integer ranging from " + Resolution.MIN_RESOLUTION + " to " + Resolution.MAX_RESOLUTION + ".");
      this.resolution = resolution;
    }
    public Resolution(Integer resolution) {
      this(resolution.intValue());
    }
    public Resolution(String resolution) {
      this(Integer.parseInt(resolution));
    }
    public int getResolution() {
      return resolution;
    }
    @Override
    public String toString() {
      return "" + resolution;
    }
    private int resolution = 0;
  }
  public static class Strength {
    public static final int MIN_STRENGTH = 1;
    public static final int MAX_STRENGTH = 10;
    public Strength(int strength) {
      if (strength < MIN_STRENGTH || strength > MAX_STRENGTH)
        throw new IllegalArgumentException(
            "Strength must be a positive integer ranging from " + Strength.MIN_STRENGTH + " to " + Strength.MAX_STRENGTH + ".");
      this.strength = strength;
    }
    public Strength(Integer strength) {
      this(strength.intValue());
    }
    public Strength(String strength) {
      this(Integer.parseInt(strength));
    }
    public int getStrength() {
      return strength;
    }
    @Override
    public String toString() {
      return "" + strength;
    }
    private int strength = 0;
  }
  protected WmTrigger(LivePaperSession lp, String name, WmTrigger.Strength strength, WmTrigger.Resolution resolution, String imageUrl) {
    this.lp = lp;
    this.setName(name);
    this.setImageUrl(imageUrl);
    this.setStrength(strength);
    this.setResolution(resolution);
  }
  protected WmTrigger(LivePaperSession lp, Map<String, Object> map) {
    this.lp = lp;
    this.assign_attributes(map);
  }
  /**
   * Create this object via the API by doing a POST
   * @return
   * @throws LivePaperException
   */
  @Override
  protected WmTrigger save() throws LivePaperException {
    return (WmTrigger) super.save();
  }
  @Override
  protected WmTrigger parse(Map<String, Object> responseMap) {
    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) responseMap.get(ITEM_KEY);
    assign_attributes(data);
    return this;
  }
  @Override
  protected void validate_attributes() {
    if (getName().length() == 0)
      throw new IllegalArgumentException("Invalid state for this operation! (missing attribute: name)");
  }
  @Override
  protected void assign_attributes(Map<String, Object> map) {
    super.assign_attributes(map);
    @SuppressWarnings("unchecked")
    Map<String, Object> watermark = (Map<String, Object>) map.get("watermark");
    if (watermark != null) {
      setImageUrl((String) watermark.get("imageURL"));
      resolution = new Resolution((Integer) watermark.get("resolution"));
      strength = new Strength((Integer) watermark.get("strength"));
    }
    //@formatter:off
    /*{
       type=watermark,
       id=PHDsCQzISqiy-lPvEVXpSw,
       name=Business Card - Back 600ppi,
       state=ACTIVE,
       dateCreated=2015-01-13T21:27:59.000+0000,
       dateModified=2015-01-13T21:27:59.000+0000,
       startDate=2015-01-13T21:27:57.887+0000,
       endDate=2017-01-13T21:27:57.887+0000,
       link=[
         {href=https://watermark.livepaperapi.com/watermark/v1/triggers/PHDsCQzISqiy-lPvEVXpSw/image,
          rel=image},
         {href=https://www.livepaperapi.com/analytics/v1/triggers/PHDsCQzISqiy-lPvEVXpSw,
          rel=analytics},
         {href=https://www.livepaperapi.com/api/v1/triggers/PHDsCQzISqiy-lPvEVXpSw,
          rel=self}],
       subscription={
         expiryDate=2017-01-13T21:27:57.887+0000,
         startDate=2015-01-13T21:27:57.887+0000},
       watermark={
         imageURL=https://storage.livepaperapi.com/objects/files/nbXURFpISzW7IVUZjD7rMA,
         resolution=75,
         strength=7}
      }*/
    //@formatter:on
  }
  @Override
  protected Map<String, Object> create_body() {
    Map<String, Object> body = new HashMap<String, Object>();
    Map<String, Object> trigger = new HashMap<String, Object>();
    Map<String, Object> watermark = new HashMap<String, Object>();
    Map<String, Object> subscription = new HashMap<String, Object>();
    subscription.put("package", DEFAULT_SUBSCRIPTION);
    watermark.put("outputImageFormat", "JPEG");
    watermark.put("resolution", "" + this.resolution.getResolution());
    watermark.put("strength", "" + this.strength.getStrength());
    watermark.put("imageURL", imageUrl);
    trigger.put("name", getName());
    trigger.put("watermark", watermark);
    trigger.put("subscription", subscription);
    body.put("trigger", trigger);
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  private Strength   strength   = null;
  private Resolution resolution = null;
  private String     imageUrl   = "";
}
