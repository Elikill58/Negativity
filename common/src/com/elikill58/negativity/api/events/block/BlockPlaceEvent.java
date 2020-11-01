package com.elikill58.negativity.api.events.block;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;

public class BlockPlaceEvent implements Event {

	private final Player p;
	private final Block b;
	private boolean cancel;
	
	public BlockPlaceEvent(Player p, Block b) {
		this.p = p;
		this.b = b;
	}
	
	public Block getBlock() {
		return b;
	}
	
	public Player getPlayer() {
		return p;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
