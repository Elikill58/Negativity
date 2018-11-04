package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;

public class StepProtocol implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(Cheat.STEP))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double dif = from.getY() - to.getY();
		if (!p.hasPotionEffect(PotionEffectType.JUMP)) {
			if (np.slime_block) {
				if (dif >= 0)
					np.slime_block = false;
			} else {
				Location baseLoc = p.getLocation();
				boolean hasSlimeBlock = false;
				for (int u = 0; u < 360; u += 3) {
					Location flameloc = baseLoc.clone().subtract(0, 1, 0);
					flameloc.setZ(flameloc.getZ() + Math.cos(u) * 3);
					flameloc.setX(flameloc.getX() + Math.sin(u) * 3);
					if (flameloc.getBlock().getType().name().equalsIgnoreCase("SLIME_BLOCK"))
						hasSlimeBlock = true;
				}
				if (hasSlimeBlock)
					np.slime_block = true;
				else {
					int ping = Utils.getPing(p), relia = Utils.parseInPorcent(dif * -500);
					if ((from.getY() - to.getY()) > 0)
						return;
					if (dif < -1.499 && ping < 200) {
						np.addWarn(Cheat.STEP);
						boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.STEP, relia, "Warn for Step: "
								+ np.getWarn(Cheat.STEP) + ". Move " + dif + "blocks up. ping: " + ping);
						if (Cheat.STEP.isSetBack() && mayCancel)
							e.setCancelled(true);
					}
				}
			}
		}
	}

}
