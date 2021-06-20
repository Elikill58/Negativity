package com.elikill58.negativity.api.events.block;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;

public class BlockPlaceEvent extends PlayerEvent {

	private final Block b;
	private boolean cancel;
	
	public BlockPlaceEvent(Player p, Block b) {
		super(p);
		this.b = b;
	}
	
	public Block getBlock() {
		return b;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
