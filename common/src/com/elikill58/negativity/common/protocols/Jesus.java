package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.api.item.Materials.STATIONARY_WATER;
import static com.elikill58.negativity.universal.detections.keys.CheatKeys.JESUS;
import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.JesusData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Jesus extends Cheat implements Listeners {

	public Jesus() {
		super(JESUS, CheatCategory.MOVEMENT, Materials.WATER_BUCKET, JesusData::new, CheatDescription.NO_FIGHT);
	}

	@Check(name = "ground-water", description = "Ground and on water", conditions = { CheckConditions.SURVIVAL,
			CheckConditions.NO_SNEAK, CheckConditions.NO_BOAT_AROUND, CheckConditions.NO_BLOCK_MID_AROUND, CheckConditions.NO_SWIM })
	public void onGroundWater(PlayerMoveEvent e, NegativityPlayer np, JesusData data) {
		if (!e.isMovePosition())
			return;
		Player p = e.getPlayer();
		Block actual = p.getLocation().getBlock(), sub = new Location(p.getWorld(), p.getLocation().getX(),
				p.getLocation().getBlockY() - 1, p.getLocation().getZ()).getBlock();
		if (sub.getType().equals(Materials.WATER_LILY))
			return;
		List<String> tested = new ArrayList<>();
		int i = 0;
		for (BlockFace bf : Arrays.asList(BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.SOUTH, BlockFace.NORTH_WEST,
				BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.WEST, BlockFace.SOUTH_WEST)) {
			Material id = actual.getRelative(bf).getType();
			Material idSub = sub.getRelative(bf).getType(); // don't check too far block
			for (Material types : Arrays.asList(id, idSub)) {
				String idTmp = types.getId();
				tested.add(bf.name() + ": " + idTmp);
				if (idTmp.contains("WATER"))
					i++;
				else {
					if (!types.equals(Materials.AIR) && types.isSolid()) // is solid
						return;
					else if (idTmp.contains("LILY") || idTmp.contains("PAD") || idTmp.contains("SLAB")
							|| idTmp.contains("STEP") || idTmp.contains("STAIRS")) // just strange block
						return;
				}
			}
		}
		boolean isOnGround = p.isOnGround();
		if (data.wasGround && isOnGround && p.getLocation().getBlock().getType().equals(Materials.AIR)
				&& sub.getType().getId().contains("WATER") && i > 3) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(i * 25),
					"ground-water",
					"I: " + i + ", sneak: " + p.isSneaking() + ", swim: " + p.isSwimming() + ", types: "
							+ p.getLocation().getBlock().getType().getId() + ", " + sub.getType().getId() + " > "
							+ tested);
			if (mayCancel && isSetBack())
				e.setCancelled(true);
		}
		data.wasGround = isOnGround;
	}
	
	@Check(name = "dif-y-2-move", description = "Y distance between moves", conditions = { CheckConditions.NO_FIGHT })
	public void onDifYMove(PlayerMoveEvent e, JesusData data) {
		Player p = e.getPlayer();
		double dif = e.getFrom().getY() - e.getTo().getY();
		double val = data.getYDiff();
		boolean isInWater = e.getTo().getBlock().getType().getId().contains("WATER");
		if (dif == val && isInWater) {
			if (!p.isSwimming() && !p.getBoundingBox().move(0, -1, 0).getBlocks(p.getWorld()).hasOther("AIR", "WATER")) {
				Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent((dif + 5) * 10),
						"dif-y-2-move", "Difference between 2 y: " + dif + " (other: " + val + ")");
			}
		}
		data.applyYDiff(dif);
	}
	
	@Check(name = "dif", description = "Little check dif", conditions = CheckConditions.NO_BLOCK_MID_AROUND)
	public void onDif(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location from = e.getFrom(), to = e.getTo();
		double dif = from.getY() - to.getY();
		Block toBlock = to.getBlock();
		Material type = toBlock.getType(), underType = toBlock.getRelative(BlockFace.DOWN).getType();
		boolean isInWater = type.getId().contains("WATER");
		boolean isOnWater = underType.getId().contains("WATER");
		if(dif == -0.5 && ((!isInWater && p.getFallDistance() > 0) || isOnWater)
				&& !type.getId().contains("FENCE")) {
			Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(98), "dif",
					"dif: -0.5, isIn: " + isInWater + ", isOn: " + isOnWater + ", type: " + type.getId()
							+ ", type Under: " + underType.getId() + ", fallDistance: " + p.getFallDistance());
		}
	}
	
	@Check(name = "water-around", description = "Check water around", conditions = { CheckConditions.NO_BOAT_AROUND })
	public void onWaterAround(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location from = e.getFrom(), to = e.getTo();
		double dif = from.getY() - to.getY();
		Block toBlock = to.getBlock();
		Material type = toBlock.getType(), underType = toBlock.getRelative(BlockFace.DOWN).getType();
		boolean isInWater = type.getId().contains("WATER");
		boolean isOnWater = underType.getId().contains("WATER");
		if (type.equals(Materials.AIR) && !isInWater && isOnWater
				&& !p.isFlying()) {
			if (!to.clone().sub(0, 1, 0).getBlockCheckerXZ(1.5).hasOther(STATIONARY_WATER)) {
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
				Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(reliability),
						"water-around",
						"Diff: " + dif + ", fd: " + p.getFallDistance());
			}
		}
	}
	
	@Check(name = "distance-in", description = "Check distance when in water", conditions = { CheckConditions.SURVIVAL })
	public void onDistanceIn(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation(), from = e.getFrom(), to = e.getTo();
		Block toBlock = to.getBlock();
		Material type = toBlock.getType(), underType = toBlock.getRelative(BlockFace.DOWN).getType();
		boolean isInWater = type.getId().contains("WATER");
		boolean isOnWater = underType.getId().contains("WATER");
		int depthStriderLevel = p.getInventory().getBoots().orElse(ItemBuilder.Builder(Materials.AIR).build())
				.getEnchantLevel(Enchantment.DEPTH_STRIDER);
		double distanceAbs = to.distance(from) - Math.abs(from.getY() - to.getY());
		Location upper = loc.clone().add(0, 1, 0);
		float distanceFall = p.getFallDistance(), ws = p.getWalkSpeed();
		if (depthStriderLevel > 0)
			ws *= depthStriderLevel * (4 / 3);
		if (isInWater && isOnWater && distanceFall < 1 && distanceAbs > ws && !upper.getBlock().isLiquid()
				&& !p.isFlying() && !loc.getBlockChecker(1).has("LILY", "WATER")) {
			Negativity.alertMod(ReportType.WARNING, p, this, 98, "distance-in",
					"In water, distance: " + distanceAbs + ", ws: " + ws + ", depth strider: " + depthStriderLevel,
					hoverMsg("main", "%distance%", String.format("%.2f", distanceAbs)));
		}
	}
}
