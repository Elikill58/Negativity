package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class NoFallProtocol implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(Cheat.NOFALL))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double distance = to.toVector().distance(from.toVector());
		if (!(p.getVehicle() != null || distance == 0.0D || from.getY() < to.getY()))
			if (p.getFallDistance() == 0.0F && !p.hasPotionEffect(PotionEffectType.SPEED)
					&& p.getLocation().clone().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
				if (p.isOnGround()) {
					if (distance > 0.79D) {
						np.addWarn(Cheat.NOFALL);
						boolean mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, Cheat.NOFALL,
								Utils.parseInPorcent(distance * 100),
								"Player in ground. FallDamage: " + p.getFallDistance() + ", DistanceBetweenFromAndTo: "
										+ distance + " (ping: " + Utils.getPing(p) + "). Warn: "
										+ np.getWarn(Cheat.NOFALL));
						if(mayCancel)
							np.NO_FALL_DAMAGE += 1;
					} else if (np.NO_FALL_DAMAGE != 0) {
						if (Cheat.NOFALL.isSetBack())
							p.damage(np.NO_FALL_DAMAGE);
						np.NO_FALL_DAMAGE = 0;
					}
				} else {
					if (distance > 2D) {
						np.addWarn(Cheat.NOFALL);
						boolean mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, Cheat.NOFALL,
								Utils.parseInPorcent(distance * 100),
								"Player not in ground no fall Damage. FallDistance: " + p.getFallDistance()
										+ ", DistanceBetweenFromAndTo: " + distance + " (ping: " + Utils.getPing(p)
										+ "). Warn: " + np.getWarn(Cheat.NOFALL));
						if(mayCancel)
							np.NO_FALL_DAMAGE += 1;
					} else if (np.NO_FALL_DAMAGE != 0) {
						if (Cheat.NOFALL.isSetBack())
							p.damage(np.NO_FALL_DAMAGE);
						np.NO_FALL_DAMAGE = 0;
					}
				}
			}
	}
}
