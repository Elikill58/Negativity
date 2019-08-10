package com.elikill58.negativity.spigot.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;

public class PlayerPacketsClearEvent extends Event {
	
	private Player p;
	private SpigotNegativityPlayer np;
	
	public PlayerPacketsClearEvent(Player p, SpigotNegativityPlayer np) {
		this.p = p;
		this.np = np;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public SpigotNegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public HashMap<String, Integer> getPackets(){
		HashMap<String, Integer> hash = new HashMap<>();
		hash.put("FLYING", np.FLYING);
		hash.put("POSITION", np.POSITION);
		hash.put("POSITION_LOOK", np.POSITION_LOOK);
		hash.put("KEEP_ALIVE", np.KEEP_ALIVE);
		hash.put("BLOCK_PLACE", np.BLOCK_PLACE);
		hash.put("BLOCK_DIG", np.BLOCK_DIG);
		hash.put("ARM", np.ARM);
		hash.put("USE_ENTITY", np.USE_ENTITY);
		hash.put("ENTITY_ACTION", np.ENTITY_ACTION);
		hash.put("ALL", np.ALL);
		return hash;
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
