package com.elikill58.negativity.common.protocols;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
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
import com.elikill58.negativity.api.ray.BlockRay.BlockRayBuilder;
import com.elikill58.negativity.api.ray.BlockRayResult;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Scaffold extends Cheat implements Listeners {

	public Scaffold() {
		super(CheatKeys.SCAFFOLD, CheatCategory.WORLD, Materials.GRASS, false, false);
	}

	@Check(name=  "below", description = "Block placed below", conditions = CheckConditions.SURVIVAL)
	public void onBlockBreak(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		int ping = p.getPing(), slot = p.getInventory().getHeldItemSlot();
		if (ping > 120)
			return;
		Scheduler.getInstance().runDelayed(() -> {
			Material m = p.getItemInHand().getType(), placed = e.getBlock().getType();
			if ((m == null || (!m.isSolid() && !m.equals(placed))) && slot != p.getInventory().getHeldItemSlot()
				&& !placed.equals(Materials.AIR)) {
				int localPing = ping;
				if (localPing == 0)
					localPing = 1;
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, Scaffold.this,
					UniversalUtils.parseInPorcent(120 / localPing), "below",
					"Item in hand: " + m.getId() + " Block placed: " + placed.getId(),
					hoverMsg("main", "%item%", m.getId().toLowerCase(Locale.ROOT), "%block%",
						placed.getId().toLowerCase(Locale.ROOT)));
				if (isSetBack() && mayCancel) {
					p.getInventory().addItem(ItemBuilder.Builder(placed).build());
					e.getBlock().setType(Materials.AIR);
				}
			}
		}, 1);
	}

	@Check(name = "distance", description = "Distance between placed and target one", conditions = CheckConditions.SURVIVAL)
	public void onBlockPlaceDistance(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block place = e.getBlock();
		if(Version.getVersion().isNewerOrEquals(Version.V1_14) && place.getType().equals(Materials.SCAFFOLD))
			return;
		if(place.getType().getId().contains("SLAB")) // TODO fix : temporary fix for ray tracing which pass through slab (more than other block)
			return;
		if(place.getY() >= p.getLocation().getY())
			return;
		Location loc = place.getLocation();
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		List<Vector> allLocs = new ArrayList<>();
		allLocs.add(loc.toVector());
		double more = 0.5;
		allLocs.add(new Vector(x + more, y, z));
		//allLocs.add(new Vector(x, y + more, z));
		allLocs.add(new Vector(x, y, z + more));
		allLocs.add(new Vector(x - more, y, z));
		allLocs.add(new Vector(x, y - more, z));
		allLocs.add(new Vector(x, y, z - more));
		Vector vec = p.getEyeLocation().getDirection();
		BlockRayBuilder builder = new BlockRayBuilder(p.getLocation().clone(), p).maxDistance(6).vector(vec)
				.ignoreAir(true).neededPositions(allLocs);
		Adapter.getAdapter().runSync(() -> {
			BlockRayResult result = builder.build().compile();
			Block searched = result.getBlock() == null ? place : result.getBlock();
			double distance = place.getLocation().distance(searched.getLocation());
			if(distance > 4.6 || !result.getRayResult().isFounded()) {
				Negativity.alertMod(distance > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 30), "distance", "Place: " + place + ", targetVisual: "
							+ searched + ", vector: " + vec.toShowableString() + ". Distance: " + distance + ". Found: " + result.getRayResult().isFounded());
			}
		});
	}
	
	@Check(name = "packet", description = "Distance of move with packet", conditions = CheckConditions.SURVIVAL)
	public void onPacket(PacketReceiveEvent e) {
		AbstractPacket pa = e.getPacket();
		if(pa.getPacketType().equals(PacketType.Client.BLOCK_PLACE)) {
			Player p = e.getPlayer();
			pa.getContent().getSpecificModifier(float.class).getContent().forEach((field, value) -> {
				if(value > 1.5) {
					Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(value * 10), "packet",
							"Wrong value " + field.getName() + ": " + value + " for packet BlockPlace");
				}
			});
		}
	}
}
