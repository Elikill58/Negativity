package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.LocationUtils;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class StepProtocol extends Cheat {

	public static final DataType<Double> BLOCKS_UP = new DataType<Double>("blocks_up", "Blocks UP", () -> new DoubleDataCounter());
	
	public StepProtocol() {
		super(CheatKeys.STEP, false, ItemTypes.BRICK_STAIRS, CheatCategory.MOVEMENT, true);
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || LocationUtils.isUsingElevator(p) || np.hasPotionEffect(PotionEffectTypes.LEVITATION)) {
			return;
		}

		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}

		if (np.justDismounted) {
			// Dismounting horses triggers a false positive
			return;
		}
		
		if(p.get(Keys.IS_ELYTRA_FLYING).orElse(false) || p.getItemInHand(HandTypes.MAIN_HAND).get().getType().getId().contains("TRIDENT"))
			return;

		Location<World> from = e.getFromTransform().getLocation();
		Location<World> to = e.getToTransform().getLocation();
		String belowSubType = to.copy().sub(0, 1, 0).getBlock().getType().getId();
		if(belowSubType.contains("SHULKER") || belowSubType.contains("SNOW"))
			return;
		double dif = to.getY() - from.getY();
		if (!np.hasPotionEffect(PotionEffectTypes.JUMP_BOOST)) {
			if (!np.isUsingSlimeBlock) {
				if (dif < 0)
					return;
				int ping = Utils.getPing(p);
				int relia = UniversalUtils.parseInPorcent(dif * 50);

				if (dif > 1.499 && ping < 200) {
					boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, relia, "Warn for Step: "
							+ np.getWarn(this) + ". Move " + dif + "blocks up. ping: " + ping, hoverMsg("main", "%block%", String.format("%.2f", dif)));
					if (isSetBack() && mayCancel) {
						e.setCancelled(true);
					}
				}
			}
		}
		double amplifier = 0;
		for(PotionEffect pe : np.getActiveEffects())
			if(pe.getType().equals(PotionEffectTypes.JUMP_BOOST))
				amplifier = pe.getAmplifier();
		double diffBoost = dif - (amplifier / 10);
		if(diffBoost > 0.2) {
			recordData(p.getUniqueId(), BLOCKS_UP, diffBoost);
			if(diffBoost > 0.6 && p.getNearbyEntities(3).stream().filter((et) -> et.getType().equals(EntityTypes.BOAT)).count() == 0)
				SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(diffBoost * 125),
					"Basic Y diff: " + dif + ", with boost: " + diffBoost + " (because of boost amplifier " + amplifier + ")",
					hoverMsg("main", "%block%", String.format("%.2f", dif)), (int) ((diffBoost - 0.6) / 0.2));
		}
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		return "Average of block up : &a" + String.format("%.3f", data.getData(BLOCKS_UP).getAverage());
	}
}
