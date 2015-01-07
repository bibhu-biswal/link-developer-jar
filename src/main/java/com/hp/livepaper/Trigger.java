package com.hp.livepaper;

import java.util.Map;

public abstract class Trigger extends BaseObject {
	protected String api_url() {
		return LivePaperSession.LP_API_HOST + "/api/v1/" + "triggers";
	}
  public static String getItemKey() { return "trigger";  }
  public static String getListKey() { return "triggers"; }
	protected abstract void validate_attributes();
  protected abstract void assign_attributes(Map<String, Object> data);
}
