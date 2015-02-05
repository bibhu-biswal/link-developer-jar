package com.hp.linkdeveloper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import org.boon.json.JsonFactory;
import com.hp.linkdeveloper.LinkDeveloperSession.Method;

public class Payoff extends BaseObject {
  protected static final String API_URL = LinkDeveloper.API_HOST + "payoffs";
  protected static final String ITEM_KEY = "payoff";
  protected static final String LIST_KEY = "payoffs";
  public enum Type { WEB_PAYOFF, RICH_PAYOFF, UNINITIALIZED }
  /**
   * Returns a Map of all the Payoff objects for the given account.  The Map uses the Id of the object as the key.
   * The value in the Map is the Payoff object itself.
   * @param ld is the LinkDeveloperSession (which holds the access token for the user)
   * @return Returns a Map of all Payoff objects.
   * @throws LinkDeveloperException
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Payoff> list(LinkDeveloperSession ld) throws LinkDeveloperException {
    Map<String, Payoff> returnList = new HashMap<String, Payoff>();
    Map<String, Object> list = ld.rest_request(API_URL, Method.GET);
    for (Map<String, Object> data : (List<Map<String, Object>>) list.get(LIST_KEY)) {
      Payoff item = new Payoff(ld, data);
      returnList.put(item.getId(), item);
    }
    return returnList;
  }
  /**
   * Obtains a Payoff object, given the id of the object.
   * @param ld is the LinkDeveloperSession (which holds the access token for the user)
   * @param id is the identifier for an existing Payoff object.
   * @return The Payoff object represented by the id is returned.
   * @throws LinkDeveloperException
   */
  @SuppressWarnings("unchecked")
  public static Payoff get(LinkDeveloperSession ld, String id) throws LinkDeveloperException {
    try {
      return new Payoff(ld, (Map<String,Object>)(ld.rest_request(API_URL + "/" + id, Method.GET).get(ITEM_KEY)));
    }
    catch (LinkDeveloperException e) {
      throw new LinkDeveloperException("Cannot create " + LinkDeveloperSession.capitalize(ITEM_KEY) + " object with ID of \"" + id + "\"! " + e.getMessage(), e);
    }
  }
  /**
   * Creates a Payoff object via a REST API POST call to the Link Developer API
   * @param ld is the LinkDeveloperSession (which holds the access token for the user)
   * @param name is the name attribute to be given to the Link object.
   * @param type indicates the Type of the Payoff to be created.
   * @param url is the URL "payoff" that is (indirectly) associated with the Trigger (through the Link object)
   * @return Returns a new Payoff object.
   * @throws LinkDeveloperException
   */
  public static Payoff create(LinkDeveloperSession ld, String name, Type type, String url) throws LinkDeveloperException {
    return (new Payoff(ld, name, type, url)).save();
  }
  public String  getUrl() {
    return url;
  }
  public void    setUrl(String url) {
    this.url = url;
  }
  public Type    getType() {
    return type;
  }
  /**
   * Returns the rich payoff data type previously set with setRichPayoffDataType()
   * @return the rich payoff data type previously set with setRichPayoffDataType()
   */
  public String getRichPayoffDataType() {
    return richPayoffDataType;
  }
  /**
   * Specifies the type of rich payoff data type.
   * @param richPayoffDataType specifies the type of rich payoff data.
   * Supported values are documented on the [Link Developer
   * Site](https://www.linkcreationstudio.com/api/doc/richpayoff/)
   */
  public void setRichPayoffDataType(String richPayoffDataType) {
    this.richPayoffDataType = richPayoffDataType;
  }
  /**
   * Returns the rich payoff data previously set with setRichPayoffData()
   * @return the rich payoff data previously set with setRichPayoffData()
   */
  public Map<String, Object> getRichPayoffData() {
    return richPayoffData;
  }
  /**
   * Specifies the rich payoff data
   * @param richPayoffData specifies the rich payoff data.
   * Supported content is as described on the [Link Developer
   * Site](https://www.linkcreationstudio.com/api/doc/richpayoff/).
   * The data is passed in a Map format, which will be converted to
   * JSON structure, base64encoded, and sent to the API.
   */
  public void setRichPayoffData(Map<String, Object> richPayoffData) {
    this.richPayoffData = richPayoffData;
  }
  protected Payoff(LinkDeveloperSession ld, String name, Type type, String url) {
    this.ld = ld;
    setName(name);
    setType(type);
    setUrl(url);
  }
  protected Payoff(LinkDeveloperSession ld, Map<String, Object> map) {
    this.ld = ld;
    this.assign_attributes(map);
  }
  protected void setType(Type type) {
    this.type = type;
  }
  @Override
  protected String api_url() {
    return API_URL;
  }
  /**
   * Create this object via the API by doing a POST
   * @return
   * @throws LinkDeveloperException
   */
  @Override
  protected Payoff save() throws LinkDeveloperException {
    return (Payoff) super.save();
  }
  @Override
  protected Payoff parse(Map<String, Object> responseMap) {
    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) responseMap.get(ITEM_KEY);
    assign_attributes(data);
    return this;
  }
  @Override
  protected void validate_attributes() {
    StringBuilder sb = new StringBuilder();
    if (getName().length() == 0)
      sb.append("Name, ");
    if (getType() == Type.UNINITIALIZED)
      sb.append("Type, ");
    if (getUrl().length() == 0)
      sb.append("Url, ");
    if (getType() == Type.RICH_PAYOFF) {
      if (getRichPayoffDataType().length() == 0)
        sb.append("RichPayoffDataType, ");
      if (getRichPayoffData() == null)
        sb.append("RichPayoffData, ");
    }
    if (sb.length() > 0) {
      sb.setLength(sb.length() - 2);
      throw new IllegalArgumentException("Invalid state for this operation! (missing attributes: " + sb.toString() + ")");
    }
  }
  @Override
  protected void assign_attributes(Map<String, Object> data) {
    super.assign_attributes(data);
    if ( data.get("richPayoff") != null )
      assign_attributes_rich_payoff(data);
    else
      assign_attributes_web_payoff(data);
  }
  protected void assign_attributes_web_payoff(Map<String, Object> data) {
    //@formatter:off
    /*{ WEB_PAYOFF
       ...
       URL=http://www.hp.com,
       ...
      }*/
    //@formatter:on
    setType(Type.WEB_PAYOFF);
    setUrl((String) data.get("URL"));
  }
  @SuppressWarnings("unchecked")
  protected void assign_attributes_rich_payoff(Map<String, Object> data) {
    //@formatter:off
    // Here is what the incoming data looks like that we need to parse
    /*{
        ...
        richPayoff={
          private={
            content-type=custom-base64,
            data=eyJ0eXBlIjoiY29udGVudCBhY3Rpb24gbGF5b3V0IiwidmVyc2lvbiI6MSwiZGF0YSI...
          },
          public={
            url=https://www.somewhere.com/s/wgg_march
          },
          version=1.0
        }
    }*/
    //@formatter:on
    setType(Type.RICH_PAYOFF);
    Map<String, Object> richData = (Map<String, Object>) data.get("richPayoff");
    //@url = data[:public][:url]
    Map<String, Object> publicData = (Map<String, Object>) richData.get("public");
    setUrl((String)publicData.get("url"));
    Map<String, Object> privateData = (Map<String, Object>) richData.get("private");
    setRichPayoffDataType((String)privateData.get("content-type"));
    //@formatter:off
    // Here is what the privateData.get("data") looks like, which we will now extract and store in a Map
    /*{
        data={
          actions=[
            {
              data={
                URL=http://www.abc.com
              },
              icon={
                id=536
              },
              label=ABC,
              type=webpage
            },
            {
              data={
                URL=http://www.nbc.com
              },
              icon={
                id=529
              },
              label=NBC,
              type=webpage
            },
            {
              data={
                URL=http://www.cbs.com
              },
              icon={
                id=534
              },
              label=CBS,
              type=webpage
            }
          ],
          content=
            {
              data={
                URL=https://pbs.twimg.com/profile_images/3540976963/ccf11950bf0bc7e8bef76a8eb7b5a0f0_400x400.jpeg
              },
              label=Some Label,
              type=image
            }
        },
        type=content action layout,
        version=1
      }*/
    //@formatter:on
    String base64encodedDataBytes = (String)privateData.get("data");
    byte[] dataBytes = DatatypeConverter.parseBase64Binary(base64encodedDataBytes);
    String dataString = new String(dataBytes);
    Map<String, Object> richDataMapResponse = JsonFactory.create().readValue(dataString, Map.class);
    setRichPayoffData(richDataMapResponse);
  }
  @Override
  protected Map<String, Object> create_body() {
    switch (getType()) {
      case WEB_PAYOFF:
        return create_web_payoff_body();
      case RICH_PAYOFF:
        return create_rich_payoff_body();
      case UNINITIALIZED:
        throw new IllegalStateException("Payoff.create_body() cannot be called on an unitialized object (Type is still set to UNKNOWN)");
    }
    return null;
  }
  protected Map<String, Object> create_web_payoff_body() {
    //@formatter:off
    // We need to build something like this
    /*{
        name: getName(),
        URL: getUrl()
      }*/
    //@formatter:on
    Map<String, Object> payoff = new HashMap<String, Object>();
    payoff.put("name", getName());
    payoff.put("URL", getUrl());
    Map<String, Object> body = new HashMap<String, Object>();
    body.put("payoff", payoff);
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  protected Map<String, Object> create_rich_payoff_body() {
    //@formatter:off
    // We need to build something like this
    /*{
        name: @name,
        richPayoff: {
          version: 1,
          private: {
            content-type: getRichPayoffDataType(),
            data:         getRichPayoffData().to_json.base64encode
          },
          public: {
            url: getUrl()
          }
        }
      }*/
    //@formatter:on
    Map<String, Object> richPayoffPrivateData = new HashMap<String, Object>();
    richPayoffPrivateData.put("content-type", getRichPayoffDataType());
    String richDataAsJson = JsonFactory.create().writeValueAsString(getRichPayoffData());
    String richDataAsJsonBase64 = DatatypeConverter.printBase64Binary(richDataAsJson.getBytes(StandardCharsets.UTF_8));
    richPayoffPrivateData.put("data", JsonFactory.create().writeValueAsString(richDataAsJsonBase64));
    Map<String, Object> richPayoffBody = new HashMap<String, Object>();
    richPayoffBody.put("version", "1");
    richPayoffBody.put("private", richPayoffPrivateData);
    Map<String, Object> richPayoffPublicData = new HashMap<String, Object>();
    richPayoffPublicData.put("url", getUrl());
    richPayoffBody.put("public", richPayoffPublicData);
    Map<String, Object> body = new HashMap<String, Object>();
    body.put("name", getName());
    body.put("richPayoff", richPayoffBody);
    @SuppressWarnings("unused")
    String bodytxt = JsonFactory.create().writeValueAsString(body);
    return body;
  }
  @Override
  protected Map<String, Object> update_body() {
    return create_body();
  }
  private String url = "";
  private Type type = Type.UNINITIALIZED;
  private String richPayoffDataType = "";
  private Map<String, Object> richPayoffData = null;
}