package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.spigot.utils.Cheat;

public class PlayerCheatAlertEvent extends Event implements Cancellable {

	private boolean cancel = false, hasRelia, alert;
	private Player p;
	private Cheat c;
	private int relia, ping;
	private String proof, hover_proof;
	
	public PlayerCheatAlertEvent(Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, String hover_proof) {
		this.p = p;
		this.c = c;
		this.relia = reliability;
		this.hasRelia = hasRelia;
		this.alert = hasRelia;
		this.ping = ping;
		this.proof = proof;
		this.hover_proof = hover_proof;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
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
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
