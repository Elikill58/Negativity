package com.elikill58.negativity.protocols;

import java.util.List;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.block.Block;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.block.BlockBreakEvent;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.potion.PotionEffectType;
import com.elikill58.negativity.common.utils.ItemUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Nuker extends Cheat implements Listeners {

	public Nuker() {
		super(CheatKeys.NUKER, true, Materials.BEDROCK, CheatCategory.WORLD, true, "breaker", "bed breaker", "bedbreaker");
	}
	
	@EventListener
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(p.hasPotionEffect(PotionEffectType.FAST_DIGGING) || e.getBlock() == null)
			return;
		int ping = p.getPing();
		List<Block> target = p.getTargetBlock(5);
		if(!target.isEmpty()) {
			for(Block b : target) {
				double distance = b.getLocation().distance(e.getBlock().getLocation());
				if ((b.getType() != e.getBlock().getType()) && distance > 3.5 && b.getType() != Materials.AIR) {
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 15 - ping),
							"BlockDig " + e.getBlock().getType().getId() + ", player see " + b.getType().getId() + ". Distance between blocks " + distance + " block. Ping: " + ping + ". Warn: " + np.getWarn(this));
					if(isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
		}
		long temp = System.currentTimeMillis(), dis = temp - np.LAST_BLOCK_BREAK;
		Material m = e.getBlock().getType();
		if(dis < 50 && m.isSolid() && !isInstantBlock(m.getId()) && !ItemUtils.hasDigSpeedEnchant(p.getItemInHand()) && !p.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
			boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, (int) (100 - dis),
					"Type: " + e.getBlock().getType().getId() + ". Last: " + np.LAST_BLOCK_BREAK + ", Now: " + temp + ", diff: " + dis + " (ping: " + ping + "). Warn: " + np.getWarn(this), hoverMsg("breaked_in", "%time%", dis));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
		np.LAST_BLOCK_BREAK = temp;
	}
	
	private boolean isInstantBlock(String m) {
		if(m.contains("SLIME") || m.contains("TNT"))
			return true;
		return false;
	}
}
