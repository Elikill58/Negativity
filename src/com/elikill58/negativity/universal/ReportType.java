package com.elikill58.negativity.universal;

public enum ReportType {

    VIOLATION("Violation"), 
    WARNING("Warning"),
    INFO("Info"), 
    REPORT("Report"),
    NONE("None");
	
	private String name;
	
	ReportType(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
