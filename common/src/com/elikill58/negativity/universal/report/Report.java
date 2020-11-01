package com.elikill58.negativity.universal.report;

import java.util.UUID;

import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;

public class Report {

	private final String reason;
	private final UUID reportedBy;
	private final long timeReport;
	private boolean haveBeenShowned = true;
	
	/**
	 * Create a new Report
	 * 
	 * @param reason the reason of the report
	 * @param reportedBy the UUID of who reported
	 */
	public Report(String reason, UUID reportedBy) {
		this(reason, reportedBy, System.currentTimeMillis());
	}

	/**
	 * Create a new Report
	 * 
	 * @param reason the reason of the report
	 * @param reportedBy the UUID of who reported
	 * @param timeReport the time when the report have been made
	 */
	public Report(String reason, UUID reportedBy, long timeReport) {
		this.reason = reason;
		this.reportedBy = reportedBy;
		this.timeReport = timeReport;
	}
	
	/**
	 * Get the reason of the report
	 * 
	 * @return the report reason
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * Get the UUID of who report
	 * 
	 * @return the reporter UUID
	 */
	public UUID getReportedBy() {
		return reportedBy;
	}
	
	/**
	 * Get the time when the report have been made
	 * In milliseconds
	 * 
	 * @return the time when the report have been made
	 */
	public long getTimeReport() {
		return timeReport;
	}
	
	/**
	 * Check if the report have already be showed to mod
	 * By default, it's true
	 * 
	 * @return true if a mod already have show it
	 */
	public boolean haveBeenShowned() {
		return haveBeenShowned;
	}
	
	/**
	 * Set if the report message have been showed
	 * 
	 * @param haveBeenShowned the new showed value
	 */
	public void setShowned(boolean haveBeenShowned) {
		this.haveBeenShowned = haveBeenShowned;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Convert this report into a JSON string
	 * @return
	 */
	public String toJsonString() {
		JSONObject json = new JSONObject();
		json.put("reportedBy", reportedBy.toString());
		json.put("reason", reason);
		json.put("time", timeReport);
		return json.toJSONString();
	}
	
	/**
	 * Load report from json string
	 * 
	 * @param json the report in json
	 * @return a new report or null
	 */
	public static Report fromJson(String json) {
		if(json == null || json.isEmpty())
			return null;
		try {
			JSONObject obj = (JSONObject) new JSONParser().parse(json);
			return new Report(obj.get("reason").toString(), UUID.fromString(obj.get("reportedBy").toString()), Long.parseLong(obj.get("time").toString()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
