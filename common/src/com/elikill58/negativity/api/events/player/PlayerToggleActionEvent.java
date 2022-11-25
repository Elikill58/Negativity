package com.elikill58.negativity.api.events.player;

import java.util.function.BiConsumer;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerToggleActionEvent extends PlayerEvent implements CancellableEvent {

	private final ToggleAction action;
	private boolean cancel;
	
	public PlayerToggleActionEvent(Player p, ToggleAction action, boolean cancel) {
		super(p);
		this.action = action;
		this.cancel = cancel;
	}
	
	public ToggleAction getAction() {
		return action;
	}
	
	public static enum ToggleAction {
		
		SNEAK(Player::setSneaking),
		SPRINT(Player::setSprinting);
		
		private BiConsumer<Player, Boolean> canceller;
		
		private ToggleAction(BiConsumer<Player, Boolean> canceller) {
			this.canceller = canceller;
		}
		
		public BiConsumer<Player, Boolean> getCanceller() {
			return canceller;
		}
		
		public void cancel(Player p) {
			canceller.accept(p, false);
		}
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancel = b;
	}
}
