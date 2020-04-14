package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SneakProtocol extends Cheat implements Listener {

	public SneakProtocol() {
		super(CheatKeys.SNEAK, true, Material.BLAZE_POWDER, CheatCategory.MOVEMENT, true, "sneack", "sneac");
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.isSneaking() && p.isSprinting() && !p.isFlying() && np.wasSneaking) {
			if(np.getPlayerVersion().isNewerOrEquals(Version.V1_14)) {
				/*Location from = e.getFrom().clone(), to = e.getTo().clone();
				from.setY(to.getY());
				if(from.distance(to) > 0.15) {
					boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(105 - (Utils.getPing(p) / 10)), "Sneaking, sprinting and not flying 1.15");
					if(mayCancel && isSetBack()) {
						e.setCancelled(true);
						p.setSprinting(false);
					}
				}*/
			} else {
				boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(105 - (Utils.getPing(p) / 10)), "Sneaking, sprinting and not flying");
				if(mayCancel && isSetBack()) {
					e.setCancelled(true);
					p.setSprinting(false);
				}
			}
		}
		np.wasSneaking = p.isSneaking();
	}
}
