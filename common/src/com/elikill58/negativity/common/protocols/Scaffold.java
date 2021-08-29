package com.elikill58.negativity.common.protocols;

import java.util.Locale;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.ray.BlockRay.BlockRayBuilder;
import com.elikill58.negativity.api.ray.BlockRayResult;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.Version;
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
	public void onBlockBreakDistance(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block place = e.getBlock();
		if(Version.getVersion().isNewerOrEquals(Version.V1_14) && place.getType().equals(Materials.SCAFFOLD))
			return;
		BlockRayResult result = new BlockRayBuilder(p.getLocation().clone(), p).ignoreAir(true)
				.neededPositions(place.getLocation().toVector()).build().compile();
		Block searched = result.getBlock() == null ? place : result.getBlock();
		double distance = place.getLocation().distance(searched.getLocation());
		if(distance > 3 && searched.getY() < p.getLocation().getY()) {
			Negativity.alertMod(distance > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 30), "distance", "Place: " + place + ", targetVisual: " + searched + ". Distance: " + distance);
		}
	}
	
	@Check(name = "packet", description = "Distance of move with packet", conditions = CheckConditions.SURVIVAL)
	public void onPacket(PacketReceiveEvent e) {
		AbstractPacket pa = e.getPacket();
		Player p = e.getPlayer();
		if(pa.getPacketType().equals(PacketType.Client.BLOCK_PLACE)) {
			pa.getContent().getSpecificModifier(float.class).getContent().forEach((field, value) -> {
				if(value > 1.5) {
					Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(value * 10), "packet",
							"Wrong value " + field.getName() + ": " + value + " for packet BlockPlace");
				}
			});
		}
	}
}
