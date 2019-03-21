package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;

public class FastPlaceProtocol implements Listener {

	@EventHandler (ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(Cheat.FASTPLACE))
			return;
		long last = System.currentTimeMillis() - np.LAST_BLOCK_PLACE, lastPing = last - (Utils.getPing(p) / 9);
		np.LAST_BLOCK_PLACE = System.currentTimeMillis();
		if (lastPing < 50) {
			np.addWarn(Cheat.FASTPLACE);
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.FASTPLACE,
					Utils.parseInPorcent(last * 1.5), "Blockplaced too quickly. Last time: " + last + ", Last with ping: "
							+ lastPing + ". Ping: " + Utils.getPing(p),
					"2 blocks placed in: " + last + " ms\nReal player do it in 150/200ms");
			if(Cheat.FASTPLACE.isSetBack() && mayCancel)
				e.setCancelled(true);
		}

	}

}
