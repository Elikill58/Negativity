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
	
	SPRINT("Sprint", Player::isSprinting),
	SNEAK("Sneak", Player::isSneaking),
	SLEEP("Sleep", Player::isSleeping),
	SWIM("Swim", Player::isSwimming),
	ELYTRA("Elytra", Player::hasElytra),
	FLY("Fly", Player::isFlying),
	ALLOW_FLY("Allow to fly", Player::getAllowFlight),
	GROUND("On ground", Player::isOnGround),

	NO_BLOCK_MID_AROUND("No block MID (fence, slab...) around", (p) -> !LocationUtils.hasMaterialsAround(p.getLocation(), "SLAB", "STEP", "FENCE", "STAIRS", "ICE", "TRAPDOOR", "CARPET", "LILY", "CAKE", "SNOW", "SCAFFOLD"), true),
	NO_BLOCK_MID_AROUND_BELOW("No block MID (fence, slab...) below", (p) -> !LocationUtils.hasMaterialsAround(p.getLocation().clone().sub(0, 1, 0), "SLAB", "STEP", "FENCE", "STAIRS", "ICE", "TRAPDOOR", "CARPET", "LILY", "CAKE", "SNOW", "SCAFFOLD"), true),
	NO_LIQUID_AROUND("No liquid around", (p) -> !LocationUtils.hasMaterialsAround(p.getLocation(), "WATER", "LAVA"), true),
	NO_STAIRS_AROUND("No stairs around", (p) -> !LocationUtils.hasMaterialsAround(p.getLocation(), "STAIRS", "SCAFFOLD"), true),
	NO_FALL_LESS_BLOCK("No block that reduce fall fear", (p) -> !LocationUtils.hasMaterialsAround(p.getLocation(), "STAIRS", "SCAFFOLD", "STEP", "SLAB", "HONEY_BLOCK"), true),
	NO_FALL_LESS_BLOCK_BELOW("No block that reduce fall fear below", (p) -> !LocationUtils.hasMaterialsAround(p.getLocation().clone().sub(0, 1, 0), "STAIRS", "SCAFFOLD", "STEP", "SLAB", "HONEY_BLOCK"), true),
	NO_CLIMB_BLOCK("No climb block around", (p) -> !LocationUtils.hasMaterialsAround(p.getLocation(), "STAIRS", "SCAFFOLD", "LADDER", "VINE"), true),
	
	NO_TELEPORT("No teleport", p -> !NegativityPlayer.getNegativityPlayer(p).isTeleporting),
	NO_BOAT_AROUND("No boat around", (p) -> !LocationUtils.hasBoatAroundHim(p.getLocation()), true),
	NO_ON_BEDROCK("Not on bedrock", (p) -> !NegativityPlayer.getNegativityPlayer(p).isBedrockPlayer()),
	NO_USE_TRIDENT("Using trident", (p) -> !p.getItemInHand().getType().getId().contains("TRIDENT")),
	NO_USE_SLIME("Using slime", (p) -> !NegativityPlayer.getNegativityPlayer(p).isUsingSlimeBlock),
	NO_USE_ELEVATOR("Using elevator", (p) -> !LocationUtils.isUsingElevator(p), true),
	NO_IRON_TARGET("Target by iron golem", (p) -> !NegativityPlayer.getNegativityPlayer(p).isTargetByIronGolem(), true),
	NO_THORNS("Thorns", p -> !Utils.hasThorns(p), true),
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
	private final boolean shouldBeCached;
	
	private CheckConditions(String displayName, Predicate<Player> function) {
		this(displayName, function, false);
	}
	
	private CheckConditions(String displayName, Predicate<Player> function, boolean shouldBeCached) {
		this.displayName = displayName;
		this.function = function;
		this.shouldBeCached = shouldBeCached;
	}
	
	public boolean check(Player p) {
		return function.test(p);
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public boolean shouldBeCached() {
		return shouldBeCached;
	}
}
