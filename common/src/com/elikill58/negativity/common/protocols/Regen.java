package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent;
import com.elikill58.negativity.api.events.player.PlayerRegainHealthEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Regen extends Cheat implements Listeners {
	
	public Regen() {
		super(CheatKeys.REGEN, CheatCategory.PLAYER, Materials.GOLDEN_APPLE, CheatDescription.HEALTH);
	}

	@EventListener
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();
		if(item == null)
			return;
		Material m = item.getType();
		if (m.equals(Materials.GOLDEN_APPLE) || m.equals(Materials.GOLDEN_CARROT))
			NegativityPlayer.getNegativityPlayer(p).flyingReason = FlyingReason.REGEN;
	}

	@EventListener
	public void onRegen(PlayerRegainHealthEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		boolean hasPotion = false;
		for (PotionEffect pe : p.getActivePotionEffect())
			if (pe.getType().equals(PotionEffectType.POISON) || pe.getType().equals(PotionEffectType.BLINDNESS)
					|| pe.getType().equals(PotionEffectType.WITHER)
					|| pe.getType().equals(PotionEffectType.SLOW_MINING)
					|| pe.getType().equals(PotionEffectType.WEAKNESS) || pe.getType().equals(PotionEffectType.NAUSEA)
					|| pe.getType().equals(PotionEffectType.HUNGER))
				hasPotion = true;
		if (hasPotion)
			np.flyingReason = FlyingReason.POTION;
		else
			np.flyingReason = FlyingReason.REGEN;
	}
	
	@Check(name = "time", description = "Time between 2 regen")
	public void onRegenTime(PlayerRegainHealthEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		long lastRegen = np.longs.get(getKey(), "time", 0l);
		long actual = System.currentTimeMillis(), dif = actual - lastRegen;
		if (lastRegen != 0 && !p.hasPotionEffect(PotionEffectType.REGENERATION) && !p.hasPotionEffect(PotionEffectType.INSTANT_HEAL)
				&& (lastRegen != System.currentTimeMillis() && Version.getVersion().isNewerOrEquals(Version.V1_14))
				&& !p.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
			int ping = p.getPing();
			if (dif + ping < (Version.getVersion().getTimeBetweenTwoRegenFromVersion())) {
				boolean mayCancel = Negativity.alertMod(dif < (50 + ping) ? ReportType.VIOLATION : ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent(200 - dif - ping), "time", "Player regen, last regen: "
						+ lastRegen + " Actual time: " + actual + " Difference: " + dif + "ms",
						hoverMsg("main", "%time%", dif));
				if(isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}
		np.longs.set(getKey(), "time", actual);
	}
}
