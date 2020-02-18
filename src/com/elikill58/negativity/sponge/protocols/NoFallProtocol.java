package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
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

public class NoFallProtocol extends Cheat {

	public NoFallProtocol() {
		super(CheatKeys.NO_FALL, false, ItemTypes.WOOL, false, true, "fall");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;

		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		
		if(p.get(Keys.CAN_FLY).orElse(false) || p.get(Keys.IS_ELYTRA_FLYING).orElse(false))
			return;
		
		Location<World> from = e.getFromTransform().getLocation();
		Location<World> to = e.getToTransform().getLocation();
		double distance = to.getPosition().distance(from.getPosition());
		float fallDistance = np.getFallDistance();
		if (!(p.getVehicle().isPresent() || distance == 0.0D || from.getY() < to.getY()) && fallDistance == 0.0F
				&& !np.hasPotionEffect(PotionEffectTypes.SPEED)
				&& p.getLocation().sub(0, 1, 0).getBlockType().equals(BlockTypes.AIR)) {
			if (p.isOnGround()) {
				if (distance > 0.79D) {
					if (SpongeNegativity.alertMod(ReportType.VIOLATION, p, this, Utils.parseInPorcent(distance * 100),
							"Player in ground. FallDamage: " + fallDistance + ", DistanceBetweenFromAndTo: " + distance
									+ " (ping: " + Utils.getPing(p) + "). Warn: " + np.getWarn(this)))
						np.NO_FALL_DAMAGE += 1;
				} else if (np.NO_FALL_DAMAGE != 0) {
					if (isSetBack())
						p.damage(np.NO_FALL_DAMAGE, DamageSources.FALLING);
					np.NO_FALL_DAMAGE = 0;
				}
			} else {
				if (distance > 2D) {
					boolean mayCancel = SpongeNegativity.alertMod(ReportType.VIOLATION, p, this,
							Utils.parseInPorcent(distance * 100),
							"Player not in ground no fall Damage. FallDistance: " + fallDistance
									+ ", DistanceBetweenFromAndTo: " + distance + " (ping: " + Utils.getPing(p)
									+ "). Warn: " + np.getWarn(this));
					if (mayCancel)
						np.NO_FALL_DAMAGE += 1;
				} else if (np.NO_FALL_DAMAGE != 0) {
					if (isSetBack())
						p.damage(np.NO_FALL_DAMAGE, DamageSources.FALLING);
					np.NO_FALL_DAMAGE = 0;
				}
			}
		}
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
