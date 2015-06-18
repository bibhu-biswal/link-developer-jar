package com.hp.linkdeveloper;

import java.util.HashMap;
import java.util.Map;
import org.boon.json.JsonFactory;

public class ShortTrigger extends Trigger {
  /**
   * Creates a ShortTrigger object via a REST API POST call to the Link Developer API
   * @param ld is the LinkDeveloperSession (which holds the access token for the user)
   * @param name is the name attribute to be given to the ShortTrigger object.
   * @return Returns a new ShortTrigger object.
   * @throws LinkDeveloperException
   */
  public static ShortTrigger create(LinkDeveloperSession ld, String name) throws LinkDeveloperException {
    return (new ShortTrigger(ld, name)).save();
  }
  public String getShortUrl() {
    return getLinks().get("shortURL");
  }
  protected ShortTrigger(LinkDeveloperSession ld, String name) {
    super(ld, name);
  }
  protected ShortTrigger(LinkDeveloperSession ld, Map<String, Object> map) {
    super(ld, map);
  }
  /**
   * Create this object via the API by doing a POST
   * @return
   * @throws LinkDeveloperException
   */
  @Override
  protected ShortTrigger save() throws LinkDeveloperException {
    return (ShortTrigger) super.save();
  }
  @Override
  protected ShortTrigger parse(Map<String, Object> responseMap) {
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
  @Override
  protected Map<String, Object> create_body() {
    Map<String, Object> body = new HashMap<String, Object>();
    Map<String, Object> trigger = new HashMap<String, Object>();
    body.put("trigger", trigger);
    trigger.put("name", getName());
    trigger.put("type", "shorturl");
    trigger.put("startDate", this.getStartDate());
    trigger.put("endDate", this.getEndDate());
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
}
