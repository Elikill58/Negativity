package com.elikill58.negativity.api.protocols;

import java.util.function.Predicate;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.utils.LocationUtils;

public enum CheckConditions {

	SURVIVAL((p) -> p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)),
	
	FLYING(Player::isFlying),
	INSIDE_VEHICLE(Player::isInsideVehicle),
	IS_NO_BEDROCK((p) -> !NegativityPlayer.getNegativityPlayer(p).isBedrockPlayer()),
	NOT_USE_TRIDENT((p) -> !p.getItemInHand().getType().getId().contains("TRIDENT")),
	NOT_USE_SLIME((p) -> !NegativityPlayer.getNegativityPlayer(p).isUsingSlimeBlock),
	NOT_USE_ELEVATOR((p) -> !LocationUtils.isUsingElevator(p)),
	
	SPRINT(Player::isSprinting),
	SNEAK(Player::isSneaking),
	SLEEP(Player::isSleeping),
	SWIM(Player::isSwimming),
	ELYTRA(Player::hasElytra),
	FLY(Player::isFlying),
	
	NO_SPRINT((p) -> !p.isSprinting()),
	NO_SNEAK((p) -> !p.isSneaking()),
	NO_SLEEP((p) -> !p.isSleeping()),
	NO_SWIM((p) -> !p.isSwimming()),
	NO_ELYTRA((p) -> !p.hasElytra()),
	NO_FLY((p) -> !p.isFlying());

	private Predicate<Player> function;
	
	private CheckConditions(Predicate<Player> function) {
		this.function = function;
	}
	
	public boolean check(Player p) {
		return function.test(p);
	}
}
