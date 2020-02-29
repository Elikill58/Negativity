package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NukerProtocol extends Cheat implements Listener {

	public NukerProtocol() {
		super(CheatKeys.NUKER, true, Material.BEDROCK, CheatCategory.WORLD, true, "breaker", "bed breaker", "bedbreaker");
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		Block target = p.getTargetBlock(null, 5);
		double distance = target.getLocation().distance(e.getBlock().getLocation());
		if ((target.getType() != e.getBlock().getType()) && distance > 3.5) {
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 15 - Utils.getPing(p)),
					"BlockDig " + e.getBlock().getType().name() + ", player see " + target.getType().name() + ". Distance between blocks " + distance + " block. Ping: " + Utils.getPing(p) + ". Warn: " + np.getWarn(this));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
		long temp = System.currentTimeMillis(), dis = temp - np.LAST_BLOCK_BREAK;
		if(dis < 50 && e.getBlock().getType().isSolid()) {
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, this, (int) (100 - dis),
					"Type: " + e.getBlock().getType().name() + ". Last: " + np.LAST_BLOCK_BREAK + ", Now: " + temp + ", diff: " + dis + " (ping: " + Utils.getPing(p) + "). Warn: " + np.getWarn(this), "2 blocks breaked in " + dis + " ms");
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
		np.LAST_BLOCK_BREAK = temp;
	}
}
