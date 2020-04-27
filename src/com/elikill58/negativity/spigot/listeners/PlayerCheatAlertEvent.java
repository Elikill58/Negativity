package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ReportType;

public class PlayerCheatAlertEvent extends Event implements Cancellable {

	private boolean cancel = false, hasRelia, alert;
	private Player p;
	private final Cheat c;
	private final int relia, ping, nbAlert;
	private int nbConsole;
	private final String proof, hover_proof;
	private final ReportType type;
	
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, String hover_proof) {
		this(type, p, c, reliability, hasRelia, ping, proof, hover_proof, 1);
	}
	
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, String hover_proof, int nbAlert) {
		this(type, p, c, reliability, hasRelia, ping, proof, hover_proof, nbAlert, 1);
	}
	
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, String hover_proof, int nbAlert, int nbAlertConsole) {
		this.type = type;
		this.p = p;
		this.c = c;
		this.relia = reliability;
		this.hasRelia = hasRelia;
		this.alert = hasRelia;
		this.ping = ping;
		this.proof = proof;
		this.hover_proof = hover_proof;
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
	
	public String getHoverProof() {
		return hover_proof;
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
	public HandlerList getHandlers() {
		return handlers;
	}

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
