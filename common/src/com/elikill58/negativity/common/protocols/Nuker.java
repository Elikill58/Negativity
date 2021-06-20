package com.elikill58.negativity.common.protocols;

import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.ItemUtils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Nuker extends Cheat implements Listeners {

	public Nuker() {
		super(CheatKeys.NUKER, CheatCategory.WORLD, Materials.BEDROCK, true, false, "breaker", "bed breaker", "bedbreaker");
	}

	@Check(name = "distance", conditions = CheckConditions.SURVIVAL)
	public void onBlockBreak(BlockBreakEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if(p.hasPotionEffect(PotionEffectType.FAST_DIGGING) || b == null || !b.getType().isSolid() || isInstantBlock(b.getType().getId()))
			return;
		int ping = p.getPing();
		List<Block> target = p.getTargetBlock(5);
		if(!target.isEmpty()) {
			Location blockLoc = b.getLocation();
			for(Block targetBlock : target) {
				if(!targetBlock.getLocation().getWorld().getName().equals(blockLoc.getWorld().getName())) {
					Adapter.getAdapter().debug("[Nuker] Wrong world: player/block/targetBlock > " + p.getWorld().getName() + "/" + blockLoc.getWorld().getName() + "/" + targetBlock.getLocation().getWorld().getName());
					break;
				}
				double distance = targetBlock.getLocation().distance(blockLoc);
				if ((targetBlock.getType() != e.getBlock().getType()) && distance > 3.5 && targetBlock.getType() != Materials.AIR) {
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 15 - ping), "distance",
							"BlockDig " + b.getType().getId() + ", player see " + targetBlock.getType().getId() + ". Distance between blocks " + distance + " block.");
					if(isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
		}
	}
	
	@Check(name = "time", conditions = CheckConditions.SURVIVAL)
	public void onBlockBreakTime(BlockBreakEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if(p.hasPotionEffect(PotionEffectType.FAST_DIGGING) || b == null || !b.getType().isSolid() || isInstantBlock(b.getType().getId()))
			return;
		long temp = System.currentTimeMillis(), dis = temp - np.LAST_BLOCK_BREAK;
		if(dis < 50 && !ItemUtils.hasDigSpeedEnchant(p.getItemInHand()) && !p.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
			boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, (int) (100 - dis), "time",
					"Type: " + e.getBlock().getType().getId() + ". Last: " + np.LAST_BLOCK_BREAK + ", Now: " + temp + ", diff: " + dis, hoverMsg("breaked_in", "%time%", dis));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
		np.LAST_BLOCK_BREAK = temp;
	}
	
	private boolean isInstantBlock(String m) {
		return m.contains("SLIME") || m.contains("TNT") || m.contains("LEAVE") || m.contains("NETHERRACK") || m.contains("BAMBOO") || m.contains("SNOW");
	}

	
	@Check(name = "packet")
	public void onPacketClear(PlayerPacketsClearEvent e) {
		Player p = e.getPlayer();
		int ping = p.getPing();
		int blockDig = e.getPackets().getOrDefault(PacketType.Client.BLOCK_DIG, 0);
		if(ping < getMaxAlertPing() && (blockDig - (ping / 10)) > 20 && !ItemUtils.hasDigSpeedEnchant(p.getItemInHand()))
			Negativity.alertMod(blockDig > 200 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(20 + blockDig),
					"packet", "BlockDig packet: " + blockDig);
	}
}
