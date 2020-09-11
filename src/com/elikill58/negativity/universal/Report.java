package com.elikill58.negativity.universal;

import java.util.UUID;

import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;

public class Report {

	private final String reason;
	private final UUID reportedBy;
	private boolean haveBeenShowned = true;
	
	public Report(String reason, UUID reportedBy) {
		this.reason = reason;
		this.reportedBy = reportedBy;
	}
	
	public String getReason() {
		return reason;
	}
	
	public UUID getReportedBy() {
		return reportedBy;
	}
	
	public boolean haveBeenShowned() {
		return haveBeenShowned;
	}
	
	public void setShowned(boolean haveBeenShowned) {
		this.haveBeenShowned = haveBeenShowned;
	}
	
	@SuppressWarnings("unchecked")
	public String toJsonString() {
		JSONObject json = new JSONObject();
		json.put("reportedBy", reportedBy.toString());
		json.put("reason", reason);
		return json.toJSONString();
	}
	
	public static Report fromJson(String json) {
		try {
			JSONObject obj = (JSONObject) new JSONParser().parse(json);
			return new Report(obj.get("reason").toString(), UUID.fromString(obj.get("reportedBy").toString()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
