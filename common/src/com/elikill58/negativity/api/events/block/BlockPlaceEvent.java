package com.elikill58.negativity.api.events.block;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;

public class BlockPlaceEvent extends PlayerEvent implements CancellableEvent {

	private final Block b, oldBlock;
	private boolean cancel;
	
	public BlockPlaceEvent(Player p, Block b, Block oldblock) {
		super(p);
		this.b = b;
		this.oldBlock = oldblock;
	}
	
	public Block getBlock() {
		return b;
	}
	
	public Block getOldBlock() {
		return oldBlock;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
