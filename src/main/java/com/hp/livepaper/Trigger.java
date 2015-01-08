package com.hp.livepaper;

public abstract class Trigger extends BaseObject {
  @Override
  protected String api_url() {
    return LivePaperSession.LP_API_HOST + "/api/v1/" + "triggers";
  }
  public static String getItemKey() {
    return "trigger";
  }
  public static String getListKey() {
    return "triggers";
  }
  protected abstract void validate_attributes();
}
