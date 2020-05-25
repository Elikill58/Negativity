package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
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
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class StepProtocol extends Cheat {

	public StepProtocol() {
		super(CheatKeys.STEP, false, ItemTypes.BRICK_STAIRS, CheatCategory.MOVEMENT, true);
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
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
		if (!np.hasPotionEffect(PotionEffectTypes.JUMP_BOOST)) {
			double dif = to.getY() - from.getY();
			if (!np.isUsingSlimeBlock) {
				if (dif < 0)
					return;
				int ping = Utils.getPing(p);
				int relia = UniversalUtils.parseInPorcent(dif * 50);

				if (dif > 1.499 && ping < 200) {
					boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, relia, "Warn for Step: "
							+ np.getWarn(this) + ". Move " + dif + "blocks up. ping: " + ping, new CheatHover("main", "%block%", String.format("%.2f", dif)));
					if (isSetBack() && mayCancel) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
