package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.SPIDER;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Spider extends Cheat implements Listeners {

	public Spider() {
		super(SPIDER, CheatCategory.MOVEMENT, Materials.SPIDER_EYE, false, false, "wallhack",
				"wall");
	}

	@Check(name = "nothing-around", description = "Walking with nothing around", conditions = { CheckConditions.SURVIVAL, CheckConditions.NOT_USE_ELEVATOR, CheckConditions.NO_ELYTRA, CheckConditions.NO_FLY, CheckConditions.NO_FALL_DISTANCE, CheckConditions.NO_SPRINT, CheckConditions.NOT_USE_SLIME })
	public void onPlayerMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();
		if(Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		if(p.hasPotionEffect(PotionEffectType.JUMP) || np.booleans.get(CheatKeys.ALL, "jump-boost-use", false))
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
		if (p.getItemInHand() != null && p.getItemInHand().getType().getId().contains("TRIDENT"))
			return;
		if(hasBypassBlockAround(loc))
			return;
		boolean isAris = ((float) y) == p.getWalkSpeed();
		if (((y > 0.499 && y < 0.7) || isAris) && p.getVelocity().length() < 1.5) {
			int relia = UniversalUtils.parseInPorcent(y * 160 + (isAris ? 39 : 0));
			boolean mayCancel = Negativity.alertMod((np.getWarn(this) > 6 ? ReportType.WARNING : ReportType.VIOLATION), p, this, relia,
					"nothing-around", "Nothing around him. To > From: " + y + " isAris: " + isAris + ", has not stab slairs")
					&& isSetBack();
			if(mayCancel)
				LocationUtils.teleportPlayerOnGround(p);
		}
	}

	@Check(name = "same-y", description = "Player move with same Y", conditions = { CheckConditions.SURVIVAL, CheckConditions.NOT_USE_ELEVATOR, CheckConditions.NO_ELYTRA, CheckConditions.NO_FLY, CheckConditions.NO_FALL_DISTANCE })
	public void onPlayerMoveSameY(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if(Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		if(p.hasPotionEffect(PotionEffectType.JUMP) || np.booleans.get(CheatKeys.ALL, "jump-boost-use", false))
			return;
		int amount = 0;
		Location from = e.getFrom(), to = e.getTo();
		double y = to.getY() - from.getY();
		if (y <= 0.0 || y == 0.25 || y == 0.5 || LocationUtils.isInWater(to)
				|| hasBypassBlockAround(to)) {//(to, "LADDER", "CLIMB", "SCAFFOLD", "WATER", "LAVA", "VINE")) {
			np.lastY.clear();
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
			boolean mayCancel = (Negativity.alertMod((np.getWarn(this) > 6 ? ReportType.WARNING : ReportType.VIOLATION), p, this, 80 + amount * 3,
					"nothing-around", "Y: " + y + ", fall: " + p.getFallDistance() + ", aount: " + amount)
					&& isSetBack());
			if(mayCancel)
				LocationUtils.teleportPlayerOnGround(p);
		}
		np.lastY.add(y);
	}
	
	@Check(name = "distance", description = "Distance when going up", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_FLY, CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NOT_USE_ELEVATOR })
	public void onPlayerContinueMove(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if(Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		Location loc = p.getLocation().clone();
		if(hasBypassBlockAround(loc) || (p.getItemInHand() != null && p.getItemInHand().getType().getId().contains("TRIDENT")))
			return;
		if(LocationUtils.hasExtended(loc, "STAIRS") || p.getLocation().getBlock().getType().getId().contains("LAVA"))
			return;
		String blockName = p.getLocation().getBlock().getType().getId();
		if(blockName.contains("LADDER") || blockName.contains("VINE"))
			return;
		
		double y = e.getTo().getY() - e.getFrom().getY();
		if (np.lastSpiderLoc != null && np.lastSpiderLoc.getWorld().equals(loc.getWorld()) && y > 0) {
			double tempDis = loc.getY() - np.lastSpiderLoc.getY(), lastSpiderDistance = np.doubles.get(SPIDER, "last-distance", 0.0);
			if (lastSpiderDistance == tempDis && tempDis != 0) {
				np.SPIDER_SAME_DIST++;
				if(np.SPIDER_SAME_DIST > 2) {
					if (Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(tempDis * 400 + np.SPIDER_SAME_DIST), "distance",
							"Nothing strange around him. To > From: " + y + ", distance: " + lastSpiderDistance + ". Walk with same y " + np.SPIDER_SAME_DIST + " times") && isSetBack()) {
						LocationUtils.teleportPlayerOnGround(p);
					}
				}
			} else
				np.SPIDER_SAME_DIST = 0;
			np.doubles.set(SPIDER, "last-distance", tempDis);
		}
		np.lastSpiderLoc = loc;
	}
		
	private boolean hasBypassBlockAround(Location loc) {
		if(has(loc, "SLAB", "STAIRS", "VINE", "LADDER", "WATER", "SCAFFOLD", "CAKE"))
			return true;
		loc = loc.clone().sub(0, 1, 0);
		return has(loc, "SLAB", "STAIRS", "VINE", "LADDER", "WATER", "SCAFFOLD", "CAKE");
	}

	public boolean has(Location loc, String... m) {
		String b = loc.getBlock().getType().getId(),
				b1 = loc.clone().add(0, 0, 1).getBlock().getType().getId(),
				b2 = loc.clone().add(1, 0, -1).getBlock().getType().getId(),
				b3 = loc.clone().add(-1, 0, -1).getBlock().getType().getId(),
				b4 = loc.clone().add(-1, 0, 1).getBlock().getType().getId();
		for(String temp : m) {
			if(b.contains(temp))
				return true;
			if(b1.contains(temp))
				return true;
			if(b2.contains(temp))
				return true;
			if(b3.contains(temp))
				return true;
			if(b4.contains(temp))
				return true;
		}
		return false;
	}
}
