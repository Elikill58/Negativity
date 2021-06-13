package com.elikill58.negativity.api.protocols;

import java.util.function.Function;

import com.elikill58.negativity.api.entity.Player;

public enum CheckConditions {

	FLYING(Player::isFlying);
	
	private final Function<Player, Boolean> function;
	
	private CheckConditions(Function<Player, Boolean> function) {
		this.function = function;
	}
	
	public Function<Player, Boolean> getFunction() {
		return function;
	}
	
	public boolean check(Player p) {
		return function.apply(p);
	}
}
