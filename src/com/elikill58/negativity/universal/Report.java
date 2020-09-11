package com.elikill58.negativity.universal;

import java.util.UUID;

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
}
