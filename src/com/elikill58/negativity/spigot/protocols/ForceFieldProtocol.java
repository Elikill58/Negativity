package com.elikill58.negativity.spigot.protocols;

import java.text.NumberFormat;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.adapter.Adapter;

@SuppressWarnings("deprecation")
public class ForceFieldProtocol implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			if (!np.ACTIVE_CHEAT.contains(Cheat.FORCEFIELD))
				return;
			double dis = e.getEntity().getLocation().distance(p.getLocation());
			if (dis > Adapter.getAdapter().getDoubleInConfig("cheats.forcefield.reach") && !p.getItemInHand().getType().equals(Material.BOW)) {
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumIntegerDigits(2);
				np.addWarn(Cheat.FORCEFIELD);
				boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.FORCEFIELD,
						Utils.parseInPorcent(dis * 2 * 10),
						"Big distance with: " + e.getEntity().getType().name().toLowerCase() + ". Exact distance: "
								+ dis + ". Ping: " + Utils.getPing(p),
						"Distance with " + e.getEntity().getType().getName() + ": " + nf.format(dis));
				if (Cheat.FORCEFIELD.isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
	}
}
