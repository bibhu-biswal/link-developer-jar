package com.hp.linkdeveloper;

import java.util.HashMap;
import java.util.Map;
import org.boon.json.JsonFactory;

public class WmTrigger extends Trigger {

  /**
   * Creates a WmTrigger object via a REST API POST call to the Link Developer API
   * @param ld is the LinkDeveloperSession (which holds the access token for the user)
   * @param name is the name attribute to be given to the WmTrigger object.
   * @return Returns a new WmTrigger object.
   * @throws LinkDeveloperException
   */
  public static WmTrigger create(LinkDeveloperSession ld, String name) throws LinkDeveloperException {
    return (new WmTrigger(ld, name)).save();
  }
  /**
   * Applies this trigger to an image that was previously uploaded to the LPP storage server and whose URL is provided, and 
   * returns a watermarked image with the provided watermark strength and watermark resolution. 
   * @param resolution is the watermark resolution. The allowed values range from 1 to 2400. Optional
   * @param strength is the watermark strength. The allowed values range from 1 to 10. Optional
   * @param urlForImageToBeWatermarked is the URL of the uploaded image that you want to be watermarked with this trigger. Required 
   * return Returns the byte array containing the image data.
   * @throws LinkDeveloperException
   */
  public byte[] watermarkImage(String urlForImageToBeWatermarked, Resolution resolution, Strength strength) throws LinkDeveloperException {
	
	if (urlForImageToBeWatermarked == null)
		throw new IllegalArgumentException( "urlForImageToBeWatermarked can not be null ");
	
	String params = "?imageURL=" + urlForImageToBeWatermarked;
	
	if (strength != null) {
		params = params + "&strength=" + strength.getStrength();
	};

	if (resolution != null) {
		params = params + "&resolution=" + resolution.getResolution();
	};		
 
    return ImageStorage.download(ld, this, ImageStorage.Type.JPEG, params);
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
  protected WmTrigger(LinkDeveloperSession ld, String name) {
	  super(ld, name);
  }
  
  protected WmTrigger(LinkDeveloperSession ld, Map<String, Object> map) {
	  super(ld, map);
  }
  /**
   * Create this object via the API by doing a POST
   * @return
   * @throws LinkDeveloperException
   */
  @Override
  protected WmTrigger save() throws LinkDeveloperException {
    return (WmTrigger) super.save();
  }
  
  /**
   * Create this object via the API by doing a POST
   * @return
   * @throws LinkDeveloperException
   */
  @Override
public void delete() throws LinkDeveloperException {
     super.delete();
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
  protected Map<String, Object> create_body() {
    Map<String, Object> body = new HashMap<String, Object>();
    Map<String, Object> trigger = new HashMap<String, Object>();
    trigger.put("name", getName());
    trigger.put("type", "watermark");
    trigger.put("startDate", this.getStartDate());
    trigger.put("endDate", this.getEndDate());
    body.put("trigger", trigger);
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
}
