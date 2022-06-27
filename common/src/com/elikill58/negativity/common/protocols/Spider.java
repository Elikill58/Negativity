package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.SPIDER;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Spider extends Cheat {

	public Spider() {
		super(SPIDER, CheatCategory.MOVEMENT, Materials.SPIDER_EYE);
	}

	@Check(name = "nothing-around", description = "Walking with nothing around", conditions = {
			CheckConditions.SURVIVAL, CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_ELYTRA, CheckConditions.NO_LIQUID_AROUND,
			CheckConditions.NO_FLY, CheckConditions.NO_FALL_DISTANCE, CheckConditions.NO_SPRINT, CheckConditions.NO_USE_TRIDENT,
			CheckConditions.NO_USE_SLIME, CheckConditions.NO_STAIRS_AROUND, CheckConditions.NO_CLIMB_BLOCK })
	public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		if (p.hasPotionEffect(PotionEffectType.JUMP) || np.booleans.get(CheatKeys.ALL, "jump-boost-use", false))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double y = to.getY() - from.getY();
		if (!LocationUtils.hasOtherThan(loc, Materials.AIR) || (from.getX() == to.getX() && from.getZ() == to.getZ()))
			return;
		Material underPlayer = loc.clone().sub(0, 1, 0).getBlock().getType(),
				underUnder = loc.clone().sub(0, 2, 0).getBlock().getType();
		if (!underPlayer.equals(Materials.AIR) || !underUnder.equals(Materials.AIR)
				|| !loc.getBlock().getType().equals(Materials.AIR))
			return;
		if (hasBypassBlockAround(loc))
			return;
		boolean isAris = ((float) y) == p.getWalkSpeed();
		if (((y > 0.499 && y < 0.7) || isAris) && p.getVelocity().length() < 1.5) {
			int relia = UniversalUtils.parseInPorcent(y * 160 + (isAris ? 39 : 0));
			boolean mayCancel = Negativity.alertMod((np.getWarn(this) > 6 ? ReportType.WARNING : ReportType.VIOLATION),
					p, this, relia, "nothing-around",
					"Nothing around him. To > From: " + y + " isAris: " + isAris + ", has not stab slairs")
					&& isSetBack();
			if (mayCancel)
				LocationUtils.teleportPlayerOnGround(p);
		}
	}

	@Check(name = "same-y", description = "Player move with same Y", conditions = { CheckConditions.SURVIVAL,
			CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_ELYTRA, CheckConditions.NO_FLY,
			CheckConditions.NO_FALL_DISTANCE, CheckConditions.NO_CLIMB_BLOCK })
	public void onPlayerMoveSameY(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		if (p.hasPotionEffect(PotionEffectType.JUMP) || np.booleans.get(CheatKeys.ALL, "jump-boost-use", false))
			return;
		int amount = 0;
		Location from = e.getFrom(), to = e.getTo();
		double y = to.getY() - from.getY();
		if (y <= 0.0 || y == 0.25 || y == 0.5 || y == 0.11837500000000034 /* TODO check if it's a good value */ || LocationUtils.isInWater(to) || hasBypassBlockAround(to)) {
			np.lastY.clear();
			return;
		} else {
			int i = np.lastY.size() - 1;
			while (i > 0) {
				double value = np.lastY.get(i);
				if (value == y) {
					++amount;
					--i;
				} else {
					if (i == np.lastY.size() - 1) {
						np.lastY.clear();
						break;
					}
					for (int x = 0; x < i; x++) {
						np.lastY.remove(0);
					}
					break;
				}
			}
		}
		if (amount > 1) {
			if (Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(80 + amount * 3),
					"same-y", "Y: " + y + ", fall: " + p.getFallDistance() + ", aount: " + amount + ", last: " + np.lastY) && isSetBack())
				LocationUtils.teleportPlayerOnGround(p);
		}
		np.lastY.add(y);
	}

	@Check(name = "distance", description = "Distance when going up", conditions = { CheckConditions.SURVIVAL,
			CheckConditions.NO_BLOCK_MID_AROUND_BELOW, CheckConditions.NO_INSIDE_VEHICLE,
			CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_LIQUID_AROUND,
			CheckConditions.NO_STAIRS_AROUND, CheckConditions.NO_USE_TRIDENT,
			CheckConditions.NO_BLOCK_MID_AROUND, CheckConditions.NO_FLY, CheckConditions.NO_CLIMB_BLOCK })
	public void onPlayerContinueMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		Location loc = p.getLocation().clone();
		double y = e.getTo().getY() - e.getFrom().getY();
		if (np.lastSpiderLoc != null && np.lastSpiderLoc.getWorld().equals(loc.getWorld()) && y > 0) {
			double tempDis = loc.getY() - np.lastSpiderLoc.getY(),
					lastSpiderDistance = np.doubles.get(SPIDER, "last-distance", 0.0);
			if (lastSpiderDistance == tempDis && tempDis != 0) {
				np.spiderSameDist++;
				if (np.spiderSameDist > 2) {
					if (Negativity.alertMod(ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent(tempDis * 400 + np.spiderSameDist), "distance",
							"Nothing strange around him. To > From: " + y + ", distance: " + lastSpiderDistance
									+ ". Walk with same y " + np.spiderSameDist + " times")
							&& isSetBack()) {
						LocationUtils.teleportPlayerOnGround(p);
					}
				}
			} else
				np.spiderSameDist = 0;
			np.doubles.set(SPIDER, "last-distance", tempDis);
		} else
			np.spiderSameDist = 0;
		np.lastSpiderLoc = loc;
	}

	private boolean hasBypassBlockAround(Location loc) {
		return has(loc, "SLAB", "CAKE", "SNOW");
	}

	public boolean has(Location loc, String... m) {
		String b = loc.getBlock().getType().getId(), b1 = loc.clone().add(0, 0, 1).getBlock().getType().getId(),
				b2 = loc.clone().add(1, 0, -1).getBlock().getType().getId(),
				b3 = loc.clone().add(-1, 0, -1).getBlock().getType().getId(),
				b4 = loc.clone().add(-1, 0, 1).getBlock().getType().getId();
		for (String temp : m) {
			if (b.contains(temp))
				return true;
			if (b1.contains(temp))
				return true;
			if (b2.contains(temp))
				return true;
			if (b3.contains(temp))
				return true;
			if (b4.contains(temp))
				return true;
		}
		return false;
	}
}
