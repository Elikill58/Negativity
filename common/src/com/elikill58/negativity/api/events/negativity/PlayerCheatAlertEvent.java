package com.elikill58.negativity.api.events.negativity;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.Cheat.CheatHover;
import com.elikill58.negativity.universal.report.ReportType;

public class PlayerCheatAlertEvent extends PlayerEvent implements CancellableEvent {

	private final Cheat c;
	private final int relia, ping;
	private final String proof, checkName;
	private final CheatHover hover;
	private final ReportType type;
	private boolean cancel = false, hasRelia, alert;
	private long nbConsole, nbAlert;
	
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String checkName, String proof, CheatHover hover, long nbAlert) {
		this(type, p, c, reliability, hasRelia, ping, checkName, proof, hover, nbAlert, 1);
	}
	
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String checkName, String proof, CheatHover hover, long nbAlert, long nbAlertConsole) {
		super(p);
		this.type = type;
		this.c = c;
		this.relia = reliability;
		this.hasRelia = hasRelia;
		this.alert = hasRelia;
		this.ping = ping;
		this.checkName = checkName;
		this.proof = proof;
		this.hover = hover;
		this.nbAlert = nbAlert;
		this.nbConsole = nbAlertConsole;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public ReportType getReportType() {
		return type;
	}
	
	public Cheat getCheat() {
		return c;
	}
	
	public int getReliability() {
		return relia;
	}
	
	public boolean hasManyReliability() {
		return hasRelia;
	}
	
	public void setAlert(boolean b) {
		alert = b;
	}
	
	public boolean isAlert() {
		return alert;
	}
	
	public int getPing() {
		return ping;
	}
	
	public String getProof() {
		return proof;
	}
	
	public String getCheckName() {
		return checkName;
	}
	
	public CheatHover getHover() {
		return hover;
	}
	
	public long getNbAlert() {
		return nbAlert;
	}
	
	public String getAlertMessageKey() {
		return (nbAlert > 1 ? "negativity.alert_multiple" : "negativity.alert");
	}
	
	public long getNbAlertConsole() {
		return nbConsole;
	}
	
	public void clearNbAlertConsole() {
		this.nbConsole = 0;
	}
}
