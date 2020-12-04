package com.elikill58.negativity.universal.ban;

import java.util.HashMap;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.report.ReportType;

public class AltAccountBan {

	private final HashMap<ReportType, Integer> conditions = new HashMap<>();
	private final int altNb;
	private final BanAltAction action;
	@Nullable
	private final String alertMessage;
	private final long banTime;
	private final boolean banDef;
	
	public AltAccountBan(int altNb, Configuration config) {
		this.altNb = altNb;
		this.action = BanAltAction.valueOf(config.getString("action").toUpperCase());
		this.alertMessage = Utils.coloredMessage(config.getString("message"));
		this.banTime = config.getLong("time", -1);
		this.banDef = config.getBoolean("def", banTime == -1);
		Configuration condConf = config.getSection("condition");
		condConf.getKeys().forEach((s) -> conditions.put(ReportType.valueOf(s.toUpperCase()), condConf.getInt(s, 0)));
	}
	
	public int getAltNb() {
		return altNb;
	}
	
	/**
	 * Get the action of the ban to do with alt
	 * 
	 * @return the action
	 */
	public BanAltAction getAction() {
		return action;
	}
	
	public HashMap<ReportType, Integer> getConditions() {
		return conditions;
	}
	
	@Nullable
	public String getAlertMessage() {
		return alertMessage;
	}
	
	/**
	 * Get the time of ban in milliseconds.
	 * Add the configurated time to the current.
	 * 
	 * Return -1 for no-ban action
	 * 
	 * @return the time of ban
	 */
	public long getBanTime() {
		return System.currentTimeMillis() + banTime;
	}
	
	/**
	 * Know if the ban is definitely
	 * If the ban time is -1, it's a definitive ban
	 * 
	 * @return true if the ban is definitely
	 */
	public boolean isBanDef() {
		return banDef;
	}
	
	/**
	 * Check if players has enough warn/violations... to be banned
	 * 
	 * @param warns the warn of all players
	 * @param violations the violations of all players
	 * @return true if has enough warns/violations
	 */
	public boolean hasCondition(int warns, int violations) {
		if(conditions.isEmpty())
			return true;
		int minWarn = conditions.getOrDefault(ReportType.WARNING, 0);
		int minViolation = conditions.getOrDefault(ReportType.VIOLATION, 0);
		if(warns < minWarn)
			return false;
		return (warns + violations) >= minViolation;
	}
	
	public enum BanAltAction {
		
		ALERT,		
		ALERT_MOD,
		BAN,
		BAN_ALL
	
	}
}
