package com.elikill58.negativity.common.protocols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.ray.block.BlockRay;
import com.elikill58.negativity.api.ray.block.BlockRayBuilder;
import com.elikill58.negativity.api.ray.block.BlockRayResult;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Scaffold extends Cheat {

	private static final List<Material> BYPASS_TYPES = Arrays.asList(Materials.AIR, Materials.SCAFFOLD);
	
	public Scaffold() {
		super(CheatKeys.SCAFFOLD, CheatCategory.WORLD, Materials.GRASS, CheatDescription.BLOCKS);
	}

	@Check(name = "below", description = "Block placed below", conditions = CheckConditions.SURVIVAL)
	public void onBlockBreak(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		int ping = p.getPing(), slot = p.getInventory().getHeldItemSlot();
		if (ping > 120)
			return;
		Scheduler.getInstance().runDelayed(() -> {
			Material m = p.getItemInHand().getType(), placed = e.getBlock().getType();
			if(BYPASS_TYPES.contains(placed))
				return;
			
			if ((m == null || (!m.isSolid() && !m.equals(placed))) && slot != p.getInventory().getHeldItemSlot()) {
				int localPing = ping;
				if (localPing == 0)
					localPing = 1;
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, Scaffold.this,
						UniversalUtils.parseInPorcent(120 / localPing), "below",
						"Item in hand: " + m.getId() + " Block placed: " + placed.getId(),
						hoverMsg("main", "%item%", m.getId().toLowerCase(Locale.ROOT), "%block%",
								placed.getId().toLowerCase(Locale.ROOT)));
				if (mayCancel && isSetBack()) {
					p.getInventory().addItem(ItemBuilder.Builder(placed).build());
					e.getBlock().setType(Materials.AIR);
				}
			}
		}, 1);
	}

	@Check(name = "distance", description = "Distance between placed and target one", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NO_ON_BEDROCK })
	public void onBlockPlaceDistance(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block place = e.getBlock();
		if(BYPASS_TYPES.contains(place.getType()) || place.getType().getId().contains("FENCE"))
			return;
		Location loc = place.getLocation();
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		List<Vector> allLocs = new ArrayList<>();
		allLocs.add(loc.toVector());
		double more = 1;
		// just right near
		allLocs.add(new Vector(x + more, y, z));
		allLocs.add(new Vector(x, y + more, z));
		allLocs.add(new Vector(x, y, z + more));
		allLocs.add(new Vector(x - more, y, z));
		allLocs.add(new Vector(x, y - more, z));
		allLocs.add(new Vector(x, y, z - more));

		// angle, to prevent item just at border
		/*allLocs.add(new Vector(x + littleMore, y, z + littleMore));
		allLocs.add(new Vector(x + littleMore, y, z - littleMore));
		allLocs.add(new Vector(x - littleMore, y, z - littleMore));
		allLocs.add(new Vector(x - littleMore, y, z + littleMore));

		allLocs.add(new Vector(x, y + littleMore, z + littleMore));
		allLocs.add(new Vector(x, y + littleMore, z - littleMore));
		allLocs.add(new Vector(x + littleMore, y - littleMore, z));
		allLocs.add(new Vector(x - littleMore, y - littleMore, z));*/
		
		BlockRay blockRay = new BlockRayBuilder(p).neededPositions(allLocs.stream().map(Vector::toBlockVector).distinct().collect(Collectors.toList()))
				.maxDistance(6).build();
		if(blockRay.getBasePosition().getYaw() > 1000) {
			Adapter.getAdapter().debug("Cancel yaw: " + blockRay.getBasePosition().getYaw());
			return; // invalid yaw
		}
		Adapter.getAdapter().runSync(() -> {
			BlockRayResult result = blockRay.compile();
			Block searched = result.getBlock() == null ? place : result.getBlock();
			double distance = result.getLastDistance();
			double maxDistance = (p.getGameMode().equals(GameMode.CREATIVE) ? 5 : 4) + 0.2;
			//Adapter.getAdapter().debug((searched.getX() == place.getX() && searched.getZ() == place.getZ() ? "Result SAME: " : "Result: ") + result.getRayResult() + ", distance: " + distance + ", block: " + searched + ", place: " + place + ", start: " + blockRay.getBasePosition());
			if (!result.getRayResult().isFounded()) {
				Negativity.alertMod(distance > maxDistance + 2 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent(distance * 25), "distance",
						"Place: " + place + ", targetVisual: " + searched + ", vec: " + result.getVector() + ", begin: " + blockRay.getBasePosition()
								+ " Distance: " + distance + ". Result: " + result.getRayResult()
								+ ", Tested: " + result.getAllTestedLoc() + ", needed: "
								+ blockRay.getNeededPositions());
			}
		});
	}

	@Check(name = "packet", description = "Distance of move with packet", conditions = CheckConditions.SURVIVAL)
	public void onPacket(PacketReceiveEvent e) {
		AbstractPacket pa = e.getPacket();
		if (pa.getPacketType().equals(PacketType.Client.BLOCK_PLACE)) {
			Player p = e.getPlayer();
			pa.getContent().getSpecificModifier(float.class).getContent().forEach((field, value) -> {
				if (value > 1.5) {
					Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(value * 10),
							"packet", "Wrong value " + field.getName() + ": " + value + " for packet BlockPlace");
				}
			});
		}
	}

	@Check(name = "place-below", description = "When placing block below", conditions = CheckConditions.NO_INSIDE_VEHICLE)
	public void onPlaceBlock(BlockPlaceEvent e, NegativityPlayer np) { // should works for bad cheats
		Player p = e.getPlayer();
		Block placed = e.getBlock();
		Location loc = p.getLocation();
		if(placed.getX() == loc.getBlockX() && placed.getZ() == loc.getBlockZ()) { // if block on same X/Z
			if(placed.getY() == (loc.getBlockY() - 1) && loc.getPitch() < 50) { // block directly below and not looking below
				if(Negativity.alertMod(ReportType.WARNING, p, this, getReliabilityAlert(), "place-below", placed + ", " + loc, new CheatHover.Literal("Place block just below")) && isSetBack())
					e.setCancelled(true);
			}
		}
 	}
	
	@Check(name = "rise-slot", description = "Detect move of head & slot.\n§cWARN: This check can be created even without hack.\n§cEnable it carefully.", conditions = CheckConditions.NO_ON_BEDROCK)
	public void onPacket(PacketReceiveEvent e, NegativityPlayer np) {
		// fully manage in ScaffoldRiseCheckProcessor
	}
}
