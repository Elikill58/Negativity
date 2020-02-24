package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.HealEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.Location;

import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.NegativityPlayer;

public class AntiPotionProtocol extends Cheat {

	public AntiPotionProtocol() {
		super(CheatKeys.ANTI_POTION, true, ItemTypes.POTION, CheatCategory.COMBAT, true, "antipopo", "nopotion", "anti-potion");
	}

	@Listener
	public void onRegen(HealEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		boolean hasPotion = false;
		for (PotionEffect pe : p.getOrCreate(PotionEffectData.class).get().effects()) {
			if (pe.getType().equals(PotionEffectTypes.POISON) || pe.getType().equals(PotionEffectTypes.BLINDNESS)
					|| pe.getType().equals(PotionEffectTypes.WITHER)
					|| pe.getType().equals(PotionEffectTypes.MINING_FATIGUE)
					|| pe.getType().equals(PotionEffectTypes.WEAKNESS) || pe.getType().equals(PotionEffectTypes.GLOWING)
					|| pe.getType().equals(PotionEffectTypes.HUNGER)) {
				hasPotion = true;
				np.POTION_EFFECTS.add(pe);
			}
		}

		np.flyingReason = hasPotion ? FlyingReason.POTION : FlyingReason.REGEN;
	}

	@Listener
	public void onProjectileHit(DamageEntityEvent e) {
		if (!e.getTargetEntity().getType().equals(EntityTypes.SPLASH_POTION))
			return;
		Location<?> loc = e.getTargetEntity().getLocation();
		for (Player p : Utils.getOnlinePlayers()) {
			if (loc.getExtent().equals(p.getLocation().getExtent()))
				if (loc.getPosition().distance(p.getLocation().getPosition()) < 8)
					SpongeNegativityPlayer.getNegativityPlayer(p).flyingReason = FlyingReason.POTION;
		}
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
