package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.HealEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class RegenProtocol extends Cheat {

	public RegenProtocol() {
		super("REGEN", true, ItemTypes.GOLDEN_APPLE, false, true, "regen", "autoregen");
	}

	@Listener
	public void onPlayerInteract(InteractEvent e, @First Player p) {
		ItemType usedItemType = e.getContext().get(EventContextKeys.USED_ITEM)
				.map(ItemStackSnapshot::getType)
				.orElse(ItemTypes.AIR);
		if (usedItemType == ItemTypes.GOLDEN_APPLE || usedItemType == ItemTypes.GOLDEN_CARROT) {
			SpongeNegativityPlayer.getNegativityPlayer(p).flyingReason = FlyingReason.REGEN;
		}
	}

	@Listener
	public void onRegen(HealEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		boolean hasPotion = false;
		boolean hasRegenEffect = false;
		for (PotionEffect pe : p.getOrCreate(PotionEffectData.class).get().effects()) {
			PotionEffectType type = pe.getType();
			if (type.equals(PotionEffectTypes.POISON) || type.equals(PotionEffectTypes.BLINDNESS)
					|| type.equals(PotionEffectTypes.WITHER)
					|| type.equals(PotionEffectTypes.MINING_FATIGUE)
					|| type.equals(PotionEffectTypes.WEAKNESS) || type.equals(PotionEffectTypes.GLOWING)
					|| type.equals(PotionEffectTypes.HUNGER)) {
				hasPotion = true;
			} else if (type == PotionEffectTypes.REGENERATION) {
				hasRegenEffect = true;
			}
		}

		np.flyingReason = hasPotion ? FlyingReason.POTION : FlyingReason.REGEN;

		long actual = System.currentTimeMillis(), dif = actual - np.LAST_REGEN;
		if (np.LAST_REGEN != 0 && !hasRegenEffect && np.hasDetectionActive(this)) {
			int ping = Utils.getPing(p);
			if (dif < (300 + ping)) {
				boolean mayCancel = SpongeNegativity.alertMod(ReportType.VIOLATION, p, this,
						Utils.parseInPorcent((dif < (50 + ping) ? 200 : 100) - dif - ping), "Player regen, last regen: "
								+ np.LAST_REGEN + " Actual time: " + actual + " Difference: " + dif,
						"Time between two regen: " + dif + " (in milliseconds)");
				if (isSetBack() && mayCancel) {
					e.setCancelled(true);
				}
			}
		}
		np.LAST_REGEN = actual;
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
