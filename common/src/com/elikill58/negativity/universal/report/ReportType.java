package com.elikill58.negativity.universal.report;

public enum ReportType {

    VIOLATION(4, "Violation"), 
    WARNING(3, "Warning"),
    INFO(2, "Info"), 
    REPORT(1, "Report"),
    /**
     * @deprecated Prefer use {@link #INFO} because this one is never used somewhere.
     */
    @Deprecated
    NONE(0, "None");
	
	private final String name;
	private final int power;
	
	ReportType(int power, String name) {
		this.name = name;
		this.power = power;
	}
	
	public String getName(){
		return name;
	}
	
	public int getPower() {
		return power;
	}
	
	public boolean isStronger(ReportType o) {
		return o.getPower() > getPower();
	}
}
