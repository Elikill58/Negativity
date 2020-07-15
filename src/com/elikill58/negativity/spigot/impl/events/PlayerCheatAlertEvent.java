package com.elikill58.negativity.spigot.impl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.negativity.IPlayerCheatAlertEvent;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Cheat.CheatHover;

public class PlayerCheatAlertEvent extends Event implements IPlayerCheatAlertEvent {

	private final Player p;
	private final Cheat c;
	private boolean cancel = false, hasRelia, alert;
	private int relia, ping, nbAlert;
	private int nbConsole;
	private String proof;
	private CheatHover hover;
	private ReportType type;
	
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, CheatHover hover, int nbAlert) {
		this(type, p, c, reliability, hasRelia, ping, proof, hover, nbAlert, 1);
	}
	
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, CheatHover hover, int nbAlert, int nbAlertConsole) {
		this.type = type;
		this.p = p;
		this.c = c;
		this.relia = reliability;
		this.hasRelia = hasRelia;
		this.alert = hasRelia;
		this.ping = ping;
		this.proof = proof;
		this.hover = hover;
		this.nbAlert = nbAlert;
		this.nbConsole = nbAlertConsole;
	}
	
	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public ReportType getReportType() {
		return type;
	}
	
	public Player getPlayer() {
		return p;
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
	
	public CheatHover getHover() {
		return hover;
	}
	
	public int getNbAlert() {
		return nbAlert;
	}
	
	public String getAlertMessageKey() {
		return (nbAlert > 1 ? "negativity.alert_multiple" : "negativity.alert");
	}
	
	public int getNbAlertConsole() {
		return nbConsole;
	}
	
	public void clearNbAlertConsole() {
		this.nbConsole = 0;
	}

	@Override
	public IPlayerCheatAlertEvent update(ReportType type, int reliability, boolean hasRelia, int ping, String proof, CheatHover hover, int nbAlert, int nbAlertConsole) {
		this.type = type;
		this.relia = reliability;
		this.hasRelia = hasRelia;
		this.alert = hasRelia;
		this.ping = ping;
		this.proof = proof;
		this.hover = hover;
		this.nbAlert = nbAlert;
		this.nbConsole = nbAlertConsole;
		return this;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
