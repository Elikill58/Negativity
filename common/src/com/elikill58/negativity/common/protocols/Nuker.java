package com.elikill58.negativity.common.protocols;

import java.util.List;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.ItemUtils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Nuker extends Cheat {

	public Nuker() {
		super(CheatKeys.NUKER, CheatCategory.WORLD, Materials.BEDROCK, CheatDescription.BLOCKS);
	}

	@Check(name = "distance", description = "Distance between target and breaked block", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_FLY })
	public void onBlockBreak(BlockBreakEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		Material type = b.getType();
		if(p.hasPotionEffect(PotionEffectType.HASTE) || b == null || !type.isSolid() || isInstantBlock(type.getId()))
			return;
		int ping = p.getPing();
		Adapter.getAdapter().runSync(() -> {
			List<Block> target = p.getTargetBlock(5);
			if(!target.isEmpty()) {
				Location blockLoc = b.getLocation();
				Block bestBlock = null;
				double bestDistance = Double.MAX_VALUE;
				for(Block targetBlock : target) {
					if(!targetBlock.getLocation().getWorld().getName().equals(blockLoc.getWorld().getName())) {
						Adapter.getAdapter().debug("[Nuker] Wrong world: player/block/targetBlock > " + p.getWorld().getName() + "/" + blockLoc.getWorld().getName() + "/" + targetBlock.getLocation().getWorld().getName());
						break;
					}
					double distance = targetBlock.getLocation().distance(blockLoc);
					if(distance < bestDistance) {
						bestBlock = targetBlock;
						bestDistance = distance;
					}
				}
				if (bestDistance != Double.MAX_VALUE && (bestBlock.getType() != type) && bestDistance > (p.getGameMode().equals(GameMode.CREATIVE) ? 5 : 4) && bestBlock.getType() != Materials.AIR) {
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(bestDistance * 15 - ping), "distance",
							"BlockDig " + b.toString() + ", player see " + bestBlock.toString() + ". Distance between blocks " + bestDistance + " block.");
					if(isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
		});
	}
	
	@Check(name = "time", description = "Time between 2 block break", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_FLY })
	public void onBlockBreakTime(BlockBreakEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if(p.hasPotionEffect(PotionEffectType.HASTE) || b == null || !b.getType().isSolid() || isInstantBlock(b.getType().getId()))
			return;
		long lastBreak = np.longs.get(getKey(), "time", 0l);
		long temp = System.currentTimeMillis(), dis = temp - lastBreak;
		if(dis < 50 && !ItemUtils.hasDigSpeedEnchant(p.getItemInHand()) && !p.hasPotionEffect(PotionEffectType.HASTE)) {
			boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, (int) (100 - dis), "time",
					"Type: " + e.getBlock().getType().getId() + ". Last: " + lastBreak + ", Now: " + temp + ", diff: " + dis, hoverMsg("breaked_in", "%time%", dis));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
		np.longs.set(getKey(), "time", temp);
	}
	
	private boolean isInstantBlock(String m) {
		return m.contains("SLIME") || m.contains("TNT") || m.contains("LEAVE") || m.contains("NETHERRACK") || m.contains("BAMBOO") || m.contains("SNOW");
	}

	
	@Check(name = "packet", description = "Amount of block break packet")
	public void onPacketClear(PlayerPacketsClearEvent e) {
		Player p = e.getPlayer();
		int ping = p.getPing();
		int blockDig = e.getPackets().getOrDefault(PacketType.Client.BLOCK_DIG, 0);
		if(ping < getMaxAlertPing() && (blockDig - (ping / 10)) > 20 && !ItemUtils.hasDigSpeedEnchant(p.getItemInHand()))
			Negativity.alertMod(blockDig > 200 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(20 + blockDig),
					"packet", "BlockDig packet: " + blockDig);
	}
}
