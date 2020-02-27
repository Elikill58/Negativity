package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class CriticalProtocol extends Cheat implements Listener {
	
	public CriticalProtocol() {
		super(CheatKeys.CRITICAL, false, Utils.getMaterialWith1_15_Compatibility("FIREBALL", "LEGACY_FIREBALL"), CheatCategory.COMBAT, true, "crit", "critic");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(final EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player) || e.isCancelled())
			return;
		Player p = (Player) e.getDamager();

		if (p.isInsideVehicle())
			return;

		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (!p.isOnGround() && !p.isFlying()) {
			if (p.getLocation().getY() % 1.0D == 0.0D) {
				if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
					boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, np.getAllWarn(this) > 5 ? 100 : 95, "");
					if(mayCancel && isSetBack())
						e.setCancelled(true);
				}
			}
		}
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
