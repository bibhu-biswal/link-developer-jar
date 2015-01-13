package com.hp.livepaper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.boon.json.JsonFactory;
import com.sun.jersey.api.client.ClientResponse;

public class WmTrigger extends Trigger {
  private static String DEFAULT_SUBSCRIPTION = "month";
  private Strength   strength   = null;
  private Resolution resolution = null;
  private String     imageUrl   = null;
  public WmTrigger(String name, WmTrigger.Strength strength, WmTrigger.Resolution resolution, String imageUrl) {
    this.setName(name);
    this.setImageUrl(imageUrl);
    this.setStrength(strength);
    this.setResolution(resolution);
  }
  public static WmTrigger create(String name, WmTrigger.Strength strength, WmTrigger.Resolution resolution, String imageUrl) throws Exception {
    return (new WmTrigger(name, strength, resolution, imageUrl)).save();
  }
  public WmTrigger save() throws LivePaperException {
    return (WmTrigger) super.save();
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
  protected Map<String, Object> create_body() {
    Map<String, Object> body         = new HashMap<String, Object>();
    Map<String, Object> trigger      = new HashMap<String, Object>();
    Map<String, Object> watermark    = new HashMap<String, Object>();
    Map<String, Object> subscription = new HashMap<String, Object>();
    subscription.put("package", DEFAULT_SUBSCRIPTION);
    watermark.put("outputImageFormat", "JPEG");
    watermark.put("resolution", ""+this.resolution.getResolution());
    watermark.put("strength", ""+this.strength.getStrength());
    watermark.put("imageURL", imageUrl);
    trigger.put("name", getName());
    trigger.put("watermark", watermark);
    trigger.put("subscription", subscription);
    body.put("trigger", trigger);
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
  public byte[] downloadWatermarkedImage() throws LivePaperException {
    ClientResponse response = com.hp.livepaper.LivePaperSession.createWebResource(this.getLinks().get("image")).
        accept("image/jpeg").
        header("Authorization", com.hp.livepaper.LivePaperSession.getLppAccessToken()).
        get(ClientResponse.class);
    byte[] bytes;
    try {
      bytes = LivePaperSession.inputStreamToByteArray(response.getEntityInputStream());
      return bytes;
    }
    catch (IOException e) {
      throw new com.hp.livepaper.LivePaperException("Failed to download watermarked image!", e);
    }
  }
  public Strength getStrength() {
    return strength;
  }
  public void setStrength(Strength strength) {
    if ( strength == null )
      throw new IllegalArgumentException("Strength cannot be null.");
    this.strength = strength;
  }
  public Resolution getResolution() {
    return resolution;
  }
  public void setResolution(Resolution resolution) {
    if ( resolution == null )
      throw new IllegalArgumentException("Resolution cannot be null.");
    this.resolution = resolution;
  }
  public String getImageUrl() {
    return imageUrl;
  }
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  public static class Resolution {
    private int resolution = 0;
    public static final int MIN_RESOLUTION = 1;
    public static final int MAX_RESOLUTION = 2400;
    public Resolution(int resolution) {
      if ( resolution < MIN_RESOLUTION || resolution > MAX_RESOLUTION )
        throw new IllegalArgumentException(
              "Resolution must be a positive integer ranging from "+Resolution.MIN_RESOLUTION+" to "+Resolution.MAX_RESOLUTION+".");
      this.resolution = resolution;
    }
    public int getResolution() {
      return resolution;
    }
    @Override
    public String toString() {
      return ""+resolution;      
    }
  }
  public static class Strength {
    private int strength = 0;
    public static final int MIN_STRENGTH = 1;
    public static final int MAX_STRENGTH = 10;
    public Strength(int strength) {
      if ( strength < MIN_STRENGTH || strength > MAX_STRENGTH )
        throw new IllegalArgumentException(
            "Strength must be a positive integer ranging from "+Strength.MIN_STRENGTH+" to "+Strength.MAX_STRENGTH+".");
      this.strength = strength;
    }
    public int getStrength() {
      return strength;
    }
    @Override
    public String toString() {
      return ""+strength;      
    }
  }
}
