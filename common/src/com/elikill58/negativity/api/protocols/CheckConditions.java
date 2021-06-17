package com.elikill58.negativity.api.protocols;

import java.util.function.Function;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.Player;

public enum CheckConditions {

	SURVIVAL((p) -> p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)),
	
	FLYING(Player::isFlying),
	INSIDE_VEHICLE(Player::isInsideVehicle),
	
	SPRINT(Player::isSprinting),
	SNEAK(Player::isSneaking),
	SLEEP(Player::isSleeping),
	SWIM(Player::isSwimming),
	ELYTRA(Player::hasElytra),
	
	NO_SPRINT((p) -> !p.isSprinting()),
	NO_SNEAK((p) -> !p.isSneaking()),
	NO_SLEEP((p) -> !p.isSleeping()),
	NO_SWIM((p) -> !p.isSwimming()),
	NO_ELYTRA((p) -> !p.hasElytra());
	
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
