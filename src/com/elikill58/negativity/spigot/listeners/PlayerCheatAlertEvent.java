package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.ReportType;

public class PlayerCheatAlertEvent extends PlayerEvent implements Cancellable {

	private boolean cancel = false, hasRelia, alert;
	private final Cheat c;
	private final int relia, ping, nbAlert;
	private int nbConsole;
	private final String proof;
	private final CheatHover hover;
	private final ReportType type;

	/**
	 * 
	 * @deprecated Use constructor with "CheatHover" and not string.
	 */
	@Deprecated
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, String hover_proof) {
		this(type, p, c, reliability, hasRelia, ping, proof, new CheatHover.Literal(hover_proof), 1, 1);
	}

	/**
	 * 
	 * @deprecated Use constructor with "CheatHover" and not string.
	 */
	@Deprecated
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, String hover_proof, int nbAlert) {
		this(type, p, c, reliability, hasRelia, ping, proof, new CheatHover.Literal(hover_proof), nbAlert, 1);
	}
	
	/**
	 * 
	 * @deprecated Use constructor with "CheatHover" and not string.
	 */
	@Deprecated
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, String hover_proof, int nbAlert, int nbAlertConsole) {
		this(type, p, c, reliability, hasRelia, ping, proof, new CheatHover.Literal(hover_proof), nbAlert, nbAlertConsole);
	}
	
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, CheatHover hover, int nbAlert) {
		this(type, p, c, reliability, hasRelia, ping, proof, hover, nbAlert, nbAlert);
	}
	
	public PlayerCheatAlertEvent(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, CheatHover hover, int nbAlert, int nbAlertConsole) {
		super(p);
		this.type = type;
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
	
	@Deprecated
	public String getHoverProof() {
		return hover == null ? null : hover.getKey();
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
	public HandlerList getHandlers() {
		return handlers;
	}

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
