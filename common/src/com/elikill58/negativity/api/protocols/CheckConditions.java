package com.elikill58.negativity.api.protocols;

import java.util.function.Predicate;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.api.utils.Utils;

public enum CheckConditions {

	SURVIVAL("Survival", (p) -> p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)),
	
	INSIDE_VEHICLE("Inside vehicle", Player::isInsideVehicle),
	IS_NO_BEDROCK("Is on bedrock", (p) -> !NegativityPlayer.getNegativityPlayer(p).isBedrockPlayer()),
	NOT_USE_TRIDENT("Using trident", (p) -> !p.getItemInHand().getType().getId().contains("TRIDENT")),
	NOT_USE_SLIME("Using slime", (p) -> !NegativityPlayer.getNegativityPlayer(p).isUsingSlimeBlock),
	NOT_USE_ELEVATOR("Using elevator", (p) -> !LocationUtils.isUsingElevator(p)),
	NOT_IRON_TARGET("Target by iron golem", (p) -> !NegativityPlayer.getNegativityPlayer(p).isTargetByIronGolem()),
	NOT_THORNS("Thorns", Utils::hasThorns),
	
	SPRINT("Sprint", Player::isSprinting),
	SNEAK("Sneak", Player::isSneaking),
	SLEEP("Sleep", Player::isSleeping),
	SWIM("Swim", Player::isSwimming),
	ELYTRA("Elytra", Player::hasElytra),
	FLY("Fly", Player::isFlying),
	ALLOW_FLY("Allow to fly", Player::getAllowFlight),
	GROUND("On ground", Player::isOnGround),

	NO_INSIDE_VEHICLE("Not inside vehicle", (p) -> !p.isInsideVehicle()),
	NO_SPRINT("Not sprinting", (p) -> !p.isSprinting()),
	NO_SNEAK("Not sneaking", (p) -> !p.isSneaking()),
	NO_SLEEP("Not sleeping", (p) -> !p.isSleeping()),
	NO_SWIM("Not swimming", (p) -> !p.isSwimming()),
	NO_ELYTRA("Not using elytra", (p) -> !p.hasElytra()),
	NO_FLY("Not flying", (p) -> !p.isFlying()),
	NO_ALLOW_FLY("Not allowed to fly", (p) -> !p.getAllowFlight()),
	NO_GROUND("Not on ground", (p) -> !p.isOnGround()),
	NO_FALL_DISTANCE("No fall distance", (p) -> p.getFallDistance() == 0);

	private final String displayName;
	private final Predicate<Player> function;
	
	private CheckConditions(String displayName, Predicate<Player> function) {
		this.displayName = displayName;
		this.function = function;
	}
	
	public boolean check(Player p) {
		return function.test(p);
	}
	
	public String getDisplayName() {
		return displayName;
	}
}
