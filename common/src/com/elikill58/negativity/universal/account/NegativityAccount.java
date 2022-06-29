package com.elikill58.negativity.universal.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.Report;

/**
 * Contains player-related data that can be accessed when the player is offline.
 */
public final class NegativityAccount {

	private final UUID playerId;
	private String lang, playerName, ip;
	private final Minerate minerate;
	private int mostClicksPerSecond;
	private final List<Report> reports;
	private final Map<String, Long> warns;
	private final long creationTime;
	private boolean inBanning = false, isMcLeaks = false, showAlert = true;

	public NegativityAccount(UUID playerId) {
		this(playerId, null, TranslatedMessages.getDefaultLang(), new Minerate(), 0, new HashMap<>(), new ArrayList<>(), "0.0.0.0", System.currentTimeMillis(), true);
	}

	public NegativityAccount(UUID playerId, String playerName, String lang, Minerate minerate, int mostClicksPerSecond, Map<String, Long> warns, List<Report> reports, String ip, long creationTime, boolean showAlert) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.lang = lang;
		this.minerate = minerate;
		this.mostClicksPerSecond = mostClicksPerSecond;
		this.warns = warns;
		this.reports = reports;
		this.ip = ip;
		this.creationTime = creationTime;
		this.showAlert = showAlert;
		Adapter.getAdapter().isUsingMcLeaks(playerId).thenAccept(isUsingMcLeaks -> {
			this.isMcLeaks = isUsingMcLeaks;
		});
	}

	public UUID getPlayerId() {
		return playerId;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Minerate getMinerate() {
		return minerate;
	}

	public int getMostClicksPerSecond() {
		return mostClicksPerSecond;
	}

	public void setMostClicksPerSecond(int mostClicksPerSecond) {
		this.mostClicksPerSecond = mostClicksPerSecond;
	}

	public long getWarn(Cheat cheat) {
		return getWarn(cheat.getKey());
	}

	public long getWarn(CheatKeys cheatKey) {
		return getWarn(cheatKey.getLowerKey());
	}

	public long getWarn(String cheatKey) {
		return warns.getOrDefault(cheatKey, 0l);
	}

	public void setWarnCount(Cheat cheat, long count) {
		setWarnCount(cheat.getKey(), count);
	}

	public void setWarnCount(CheatKeys cheatKey, long count) {
		setWarnCount(cheatKey.getLowerKey(), count);
	}

	public void setWarnCount(String cheatKey, long count) {
		warns.put(cheatKey, count);
	}
	
	public long countAllWarns() {
		return warns.values().stream().reduce(0l, Long::sum);
	}

	public Map<String, Long> getAllWarns() {
		return Collections.unmodifiableMap(warns);
	}

	@NonNull
	public static NegativityAccount get(UUID accountId) {
		return Adapter.getAdapter().getAccountManager().getNow(accountId);
	}
	
	public List<Report> getReports() {
		return reports;
	}

	public long getCreationTime() {
		return creationTime;
	}
	
	public boolean isMcLeaks() {
		return isMcLeaks;
	}
	
	public void setInBanning(boolean b) {
		this.inBanning = b;
	}

	public boolean isInBanning() {
		return inBanning;
	}
	
	public boolean isShowAlert() {
		return showAlert;
	}
	
	public void setShowAlert(boolean showAlert) {
		this.showAlert = showAlert;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
}
