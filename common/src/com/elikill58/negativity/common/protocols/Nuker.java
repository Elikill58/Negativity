package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.ItemUtils;
import com.elikill58.negativity.common.protocols.data.NukerData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Nuker extends Cheat implements Listeners {

	public Nuker() {
		super(CheatKeys.NUKER, CheatCategory.WORLD, Materials.BEDROCK, NukerData::new, CheatDescription.BLOCKS);
	}

	@Check(name = "time", description = "Time between 2 block break", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_FLY })
	public void onBlockBreakTime(BlockBreakEvent e, NegativityPlayer np, NukerData data) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if (p.hasPotionEffect(PotionEffectType.HASTE) || b == null || !b.getType().isSolid() || isInstantBlock(b.getType().getId()))
			return;
		int diff = np.getTicks() - data.ticks;
		if (diff <= getConfig().getInt("checks.time.time_ticks", 5) && !ItemUtils.hasDigSpeedEnchant(p.getItemInHand())) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, 100 - diff, "time", "Type: " + e.getBlock().getType().getId() + ". Ticks: " + diff,
					hoverMsg("breaked_in", "%time%", String.format("%.2f", ((double) diff) / 20)));
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
		}
		data.ticks = np.getTicks();
	}

	private boolean isInstantBlock(String m) {
		return m.contains("SLIME") || m.contains("TNT") || m.contains("LEAVE") || m.contains("NETHERRACK") || m.contains("BAMBOO") || m.contains("SNOW") || m.contains("KELP") || m.contains("BERRY") || m.contains("BERRIES");
	}

	@Check(name = "packet", description = "Amount of block break packet")
	public void onPacketClear(PlayerPacketsClearEvent e) {
		Player p = e.getPlayer();
		int ping = p.getPing();
		int blockDig = e.getPackets().getOrDefault(PacketType.Client.BLOCK_DIG, 0);
		if (ping < getMaxAlertPing() && (blockDig - (ping / 10)) > 25 && !ItemUtils.hasDigSpeedEnchant(p.getItemInHand()))
			Negativity.alertMod(blockDig > 200 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(20 + blockDig), "packet", "BlockDig packet: " + blockDig);
	}
}
