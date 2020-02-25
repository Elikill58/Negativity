package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
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
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class NoWebProtocol extends Cheat {

	private static final double MAX = 0.7421028493192875;
	
	public NoWebProtocol() {
		super(CheatKeys.NO_WEB, false, ItemTypes.WEB, CheatCategory.MOVEMENT, true, "no web");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}

		for (PotionEffect pe : np.getActiveEffects()) {
			if (pe.getType().equals(PotionEffectTypes.SPEED) && pe.getAmplifier() > 1) {
				return;
			}
		}

		if(p.get(Keys.IS_FLYING).orElse(false) || np.getFallDistance() > 1)
			return;
		
		Location<?> l = p.getLocation();
		
		double distance = e.getToTransform().getPosition().distance(e.getFromTransform().getPosition());
		if (!(distance > MAX)) {
			BlockState under = new Location<World>(p.getWorld(), l.getX(), l.getY(), l.getZ()).getBlock();
			if (under.getType() == ItemTypes.WEB) {
				if (distance > 0.14) {
					boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, Utils.parseInPorcent(distance * 500), "Distance: " + distance + ", fallDistance: " + np.getFallDistance());
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
