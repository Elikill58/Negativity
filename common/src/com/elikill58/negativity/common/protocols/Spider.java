package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.SPIDER;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.common.protocols.data.SpiderData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Spider extends Cheat {

	public Spider() {
		super(SPIDER, CheatCategory.MOVEMENT, Materials.SPIDER_EYE, SpiderData::new);
	}

	@Check(name = "nothing-around", description = "Walking with nothing around", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_ELYTRA,
			CheckConditions.NO_LIQUID_AROUND, CheckConditions.NO_FLY, CheckConditions.NO_FALL_DISTANCE, CheckConditions.NO_SPRINT, CheckConditions.NO_USE_TRIDENT,
			CheckConditions.NO_USE_SLIME, CheckConditions.NO_STAIRS_AROUND, CheckConditions.NO_CLIMB_BLOCK, CheckConditions.NO_USE_JUMP_BOOST })
	public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		if (p.hasPotionEffect(PotionEffectType.JUMP))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double y = to.getY() - from.getY();
		if (!loc.getBlockChecker(1).hasOther(Materials.AIR) || (from.getX() == to.getX() && from.getZ() == to.getZ()))
			return;
		if (!loc.clone().sub(0, 2, 0).getBlock().getType().equals(Materials.AIR))
			return;
		if (hasBypassBlockAround(loc))
			return;
		boolean isAris = ((float) y) == p.getWalkSpeed();
		if (((y > 0.499 && y < 0.7) || isAris) && p.getVelocity().length() < 1.5) {
			int relia = UniversalUtils.parseInPorcent(y * 160 + (isAris ? 39 : 0));
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, relia, "nothing-around",
					"Nothing around him. To > From: " + y + " isAris: " + isAris + ", has not stab slairs") && isSetBack();
			if (mayCancel)
				LocationUtils.teleportPlayerOnGround(p);
		}
	}

	@Check(name = "same-y", description = "Player move with same Y", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_ELYTRA,
			CheckConditions.NO_FLY, CheckConditions.NO_FALL_DISTANCE, CheckConditions.NO_CLIMB_BLOCK, CheckConditions.NO_USE_JUMP_BOOST })
	public void onPlayerMoveSameY(PlayerMoveEvent e, NegativityPlayer np, SpiderData data) {
		Player p = e.getPlayer();
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		if (p.hasPotionEffect(PotionEffectType.JUMP))
			return;
		int amount = 0;
		Location from = e.getFrom(), to = e.getTo();
		double y = to.getY() - from.getY();
		if (y <= 0.0 || y == 0.25 || y == 0.5 || y == 0.11837500000000034 /* TODO check if it's a good value */ || LocationUtils.isInWater(to) || hasBypassBlockAround(to)
				|| hasBypassBlockAround(to.clone().sub(0, 1, 0))) {
			data.lastY.clear();
			return;
		}
		int i = data.lastY.size() - 1;
		while (i > 0) {
			double value = data.lastY.get(i);
			if (value == y) {
				++amount;
				--i;
			} else {
				if (i == data.lastY.size() - 1) {
					data.lastY.clear();
					break;
				}
				for (int x = 0; x < i; x++) {
					data.lastY.remove(0);
				}
				break;
			}
		}
		if (amount > 1) {
			if (Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(80 + amount * 3), "same-y",
					"Y: " + y + ", fall: " + p.getFallDistance() + ", amount: " + amount + ", last: " + data.lastY) && isSetBack())
				LocationUtils.teleportPlayerOnGround(p);
		}
		data.lastY.add(y);
	}

	@Check(name = "distance", description = "Distance when going up", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NO_CLIMB_BLOCK,
			CheckConditions.NO_USE_ELEVATOR, CheckConditions.NO_LIQUID_AROUND, CheckConditions.NO_STAIRS_AROUND, CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_BLOCK_MID_AROUND,
			CheckConditions.NO_FLY, CheckConditions.NO_CLIMB_BLOCK })
	public void onPlayerContinueMove(PlayerMoveEvent e, NegativityPlayer np, SpiderData data) {
		Player p = e.getPlayer();
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		Location loc = p.getLocation().clone();
		double y = e.getTo().getY() - e.getFrom().getY();
		if (data.lastSpiderLoc != null && data.lastSpiderLoc.getWorld().equals(loc.getWorld()) && y > 0) {
			double tempDis = loc.getY() - data.lastSpiderLoc.getY();
			if (data.lastDistance == tempDis && tempDis != 0) {
				data.spiderSameDist++;
				if (data.spiderSameDist > 2) {
					if (Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(tempDis * 400 + data.spiderSameDist), "distance",
							"Nothing strange around him. To > From: " + y + ", distance: " + data.lastDistance + ". Walk with same y " + data.spiderSameDist + " times") && isSetBack()) {
						LocationUtils.teleportPlayerOnGround(p);
					}
				}
			} else
				data.spiderSameDist = 0;
			data.lastDistance = tempDis;
		} else
			data.spiderSameDist = 0;
		data.lastSpiderLoc = loc;
	}

	private boolean hasBypassBlockAround(Location loc) {
		return has(loc, "SLAB", "CAKE", "SNOW", "LADDER", "SCAFFOLD");
	}

	public boolean has(Location loc, String... m) {
		String b = loc.getBlock().getType().getId(), b1 = loc.clone().add(0, 0, 1).getBlock().getType().getId(), b2 = loc.clone().add(1, 0, -1).getBlock().getType().getId(),
				b3 = loc.clone().add(-1, 0, -1).getBlock().getType().getId(), b4 = loc.clone().add(-1, 0, 1).getBlock().getType().getId();
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
