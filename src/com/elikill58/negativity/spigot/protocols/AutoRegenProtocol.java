package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer.FlyingReason;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Version;

@SuppressWarnings("deprecation")
public class AutoRegenProtocol implements Listener {
	
	@EventHandler (ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Material m = p.getItemInHand().getType();
		if (m.equals(Material.GOLDEN_APPLE) || m.equals(Material.GOLDEN_CARROT))
			SpigotNegativityPlayer.getNegativityPlayer(p).flyingReason = FlyingReason.REGEN;
	}

	@EventHandler (ignoreCancelled = true)
	public void onRegen(EntityRegainHealthEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		Player p = (Player) e.getEntity();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		boolean hasPotion = false;
		for (PotionEffect pe : p.getActivePotionEffects())
			if (pe.getType().equals(PotionEffectType.POISON) || pe.getType().equals(PotionEffectType.BLINDNESS)
					|| pe.getType().equals(PotionEffectType.WITHER)
					|| pe.getType().equals(PotionEffectType.SLOW_DIGGING)
					|| pe.getType().equals(PotionEffectType.WEAKNESS) || pe.getType().equals(PotionEffectType.CONFUSION)
					|| pe.getType().equals(PotionEffectType.HUNGER))
				hasPotion = true;
		if (hasPotion)
			np.flyingReason = FlyingReason.POTION;
		else
			np.flyingReason = FlyingReason.REGEN;
		long actual = System.currentTimeMillis(), dif = actual - np.LAST_REGEN;
		if (np.LAST_REGEN != 0 && !p.hasPotionEffect(PotionEffectType.REGENERATION) && np.ACTIVE_CHEAT.contains(Cheat.AUTOREGEN)) {
			int ping = Utils.getPing(p);
			if (dif < (Version.getVersion().getTimeBetweenTwoRegenFromVersion() + ping)) {
				np.addWarn(Cheat.AUTOREGEN);
				boolean mayCancel = false;
				if (dif < (50 + ping))
					mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, Cheat.AUTOREGEN,
							Utils.parseInPorcent(200 - dif - ping), "Player regen, last regen: " + np.LAST_REGEN
									+ " Actual time: " + actual + " Difference: " + dif + "ms",
							"Time between two regen: " + dif + " (in milliseconds)");
				else
					mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.AUTOREGEN,
							Utils.parseInPorcent(100 - dif - ping), "Player regen, last regen: " + np.LAST_REGEN
									+ " Actual time: " + actual + " Difference: " + dif + "ms",
							"Time between two regen: " + dif + " (in milliseconds)");
				if(Cheat.AUTOREGEN.isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
		np.LAST_REGEN = actual;
	}
}
