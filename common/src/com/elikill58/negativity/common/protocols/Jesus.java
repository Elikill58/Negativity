package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.api.item.Materials.STATIONARY_WATER;
import static com.elikill58.negativity.api.utils.LocationUtils.hasMaterialAround;
import static com.elikill58.negativity.api.utils.LocationUtils.hasMaterialsAround;
import static com.elikill58.negativity.api.utils.LocationUtils.hasOtherThan;
import static com.elikill58.negativity.api.utils.LocationUtils.hasOtherThanExtended;
import static com.elikill58.negativity.universal.detections.keys.CheatKeys.JESUS;
import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Jesus extends Cheat implements Listeners {

	public Jesus() {
		super(JESUS, CheatCategory.MOVEMENT, Materials.WATER_BUCKET, CheatDescription.NO_FIGHT);
	}

	@EventListener
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(!e.isMovePosition())
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || LocationUtils.hasBoatAroundHim(e.getTo()))
			return;
		if (p.hasElytra() || p.isInsideVehicle() || p.isSwimming())
			return;
		ItemStack item = p.getItemInHand();
		if(item != null && item.getType().getId().contains("TRIDENT"))
			return;
		Location loc = p.getLocation(), to = e.getTo(), from = e.getFrom(), under = loc.clone().sub(0, 1, 0);
		if (hasMaterialsAround(loc, "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET", "LILY")
				|| hasMaterialsAround(under, "ICE", "TRAPDOOR", "SLAB", "STAIRS", "CARPET", "LILY"))
			return;
		Material type = loc.getBlock().getType(), underType = under.getBlock().getType();
		boolean isInWater = type.getId().contains("WATER"), isOnWater = underType.getId().contains("WATER");
		boolean mayCancel = false;
		double dif = e.getFrom().getY() - e.getTo().getY();
		if(checkActive("water-around")) {
			if (type.equals(Materials.AIR) && !isInWater && isOnWater && !LocationUtils.hasBoatAroundHim(loc) && !p.isFlying()) {
				if (!hasOtherThanExtended(under, STATIONARY_WATER)) {
					double reliability = 0;
					if (dif < 0.0005 && dif > 0.00000005)
						reliability = dif * 10000000 - 1;
					else if (dif < 0.1 && dif > 0.08 && p.getFallDistance() <= 0.0)
						reliability = dif * 1000;
					else if (dif == 0.5)
						reliability = 75;
					else if (dif < 0.30001 && dif > 0.3000)
						reliability = dif * 100 * 2.5;
					else if (dif < 0.002 && dif > -0.002 && dif != 0.0)
						reliability = Math.abs(dif * 5000);
					else if (dif == 0.0 && loc.clone().sub(0, 0.2, 0).getBlock().getType().getId().contains("WATER"))
						reliability = 90;
					mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(reliability), "water-around",
							"Stationary_water aroud him. Diff: " + dif + ", fallDistance: " + p.getFallDistance());
				}
			}
		}
		if (checkActive("dif") && dif == -0.5 && ((!isInWater && p.getFallDistance() > 0) || isOnWater) && !type.getId().contains("FENCE")) {
			mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(98), "dif", "dif: -0.5, isIn: " + isInWater + ", isOn: " + isOnWater + ", type: " + type.getId() + ", type Under: " + underType.getId() + ", fallDistance: " + p.getFallDistance());
		}
		
		if(checkActive("dif-y-2-move")) {
			boolean jesusState = np.booleans.get(JESUS, "state", false);
			if (dif == np.doubles.get(JESUS, "last-y-" + jesusState, 0.0) && isInWater && !np.isInFight) {
				if (!hasOtherThan(under, STATIONARY_WATER) && !p.isSwimming()) {
					mayCancel = Negativity.alertMod(np.getWarn(this) > 10 ? ReportType.VIOLATION : ReportType.WARNING,
							p, this, parseInPorcent((dif + 5) * 10), "dif-y-2-move",
							"Stationary_water aroud him. Difference between 2 y: " + dif
							+ " (other: " + np.doubles.get(JESUS, "last-y-" + (!jesusState), 0.0) + ")");
				}
			}
			np.doubles.set(JESUS, "last-y-" + jesusState, dif);
			np.booleans.set(JESUS, "state", !jesusState);
		}
		
		if(checkActive("distance-in") && !p.hasPotionEffect(PotionEffectType.SPEED) && !np.isInFight) {
			int depthStriderLevel = p.getInventory().getBoots().orElse(ItemBuilder.Builder(Materials.AIR).build()).getEnchantLevel(Enchantment.DEPTH_STRIDER);
			double distanceAbs = to.distance(from) - Math.abs(from.getY() - to.getY());
			Location upper = loc.clone().add(0, 1, 0);
			float distanceFall = p.getFallDistance(), ws = p.getWalkSpeed();
			if(depthStriderLevel > 0)
				ws = ws * depthStriderLevel * (4/3);
			if (isInWater && isOnWater && distanceFall < 1 && distanceAbs > ws && !upper.getBlock().isLiquid() && !p.isFlying()
					&& !p.getInventory().getBoots().orElse(ItemBuilder.Builder(Materials.AIR).build()).hasEnchant(Enchantment.DIG_SPEED)
					&& !hasMaterialsAround(loc, "WATER_LILY") && !hasMaterialsAround(upper, "WATER_LILY")
						&& !hasOtherThan(under, "WATER")) {
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, 98, "distance-in",
						"In water, distance: " + distanceAbs + ", ws: " + ws + ", depth strider: " + depthStriderLevel,
						hoverMsg("main", "%distance%", String.format("%.2f", distanceAbs)));
			}
		}

		if (isSetBack() && mayCancel)
			p.teleport(p.getLocation().sub(0, 1, 0));
	}
	
	@Check(name = "ground-water", description = "Ground and on water", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_SNEAK, CheckConditions.NO_BOAT_AROUND })
	public void onGroundWater(PlayerMoveEvent e, NegativityPlayer np) {
		if(!e.isMovePosition())
			return;
		Player p = e.getPlayer();
		if(hasMaterialAround(p.getLocation(), Materials.WATER_LILY))
			return;
		Block actual = p.getLocation().getBlock(), sub = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getBlockY() - 1, p.getLocation().getZ()).getBlock();
		if(sub.getType().equals(Materials.WATER_LILY))
			return;
		List<String> tested = new ArrayList<>();
		int i = 0;
		for(BlockFace bf : Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)) {
			String id = actual.getRelative(bf).getType().getId();
			String idSub = sub.getRelative(bf).getType().getId();
			for(String idTmp : Arrays.asList(id, idSub)) {
				tested.add(idTmp);
				if(idTmp.contains("WATER"))
					i++;
				else if(idTmp.contains("LILY") || idTmp.contains("PAD") || idTmp.contains("SLAB") || idTmp.contains("STEP") || idTmp.contains("STAIRS"))
					return;
			}
		}
		boolean wasOnGround = np.booleans.get(JESUS, "bw-was-ground", false);
		boolean isOnGround = p.isOnGround();
		if(wasOnGround && isOnGround && p.getLocation().getBlock().getType().equals(Materials.AIR) && sub.getType().getId().contains("WATER") && i > 3) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(i * 25), "ground-water", "I: " + i + ", sneak: " +
						p.isSneaking() + ", swim: " + p.isSwimming() + ", types: " + p.getLocation().getBlock().getType().getId() + ", " + sub.getType().getId() + " > " + tested);
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
		np.booleans.set(JESUS, "bw-was-ground", isOnGround);
	}
}
