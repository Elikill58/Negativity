package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Difficulty;
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
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.UniversalUtils;

@SuppressWarnings("deprecation")
public class RegenProtocol extends Cheat implements Listener {
	
	public RegenProtocol() {
		super(CheatKeys.REGEN, true, Material.GOLDEN_APPLE, CheatCategory.PLAYER, true, "regen", "autoregen");
	}

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
		if (np.LAST_REGEN != 0 && !p.hasPotionEffect(PotionEffectType.REGENERATION) && np.ACTIVE_CHEAT.contains(this)
				&& (np.LAST_REGEN != System.currentTimeMillis() && Version.getVersion().isNewerOrEquals(Version.V1_14))
				&& !p.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
			int ping = Utils.getPing(p);
			if (dif < (Version.getVersion().getTimeBetweenTwoRegenFromVersion() + ping)) {
				boolean mayCancel = SpigotNegativity.alertMod(dif < (50 + ping) ? ReportType.VIOLATION : ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent(200 - dif - ping), "Player regen, last regen: " + np.LAST_REGEN
									+ " Actual time: " + actual + " Difference: " + dif + "ms",
									hoverMsg("main", "%time%", dif));
				if(isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
		np.LAST_REGEN = actual;
	}
}
