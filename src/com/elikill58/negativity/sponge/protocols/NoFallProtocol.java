package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
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
import com.elikill58.negativity.universal.adapter.Adapter;

public class NoFallProtocol extends Cheat {

	public NoFallProtocol() {
		super(CheatKeys.NO_FALL, false, ItemTypes.WOOL, CheatCategory.MOVEMENT, true, "fall");
	}

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;

		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;

		if (p.get(Keys.CAN_FLY).orElse(false) || p.get(Keys.IS_ELYTRA_FLYING).orElse(false)
				|| np.hasPotionEffect(PotionEffectTypes.SPEED) || p.getVehicle().isPresent())
			return;

		Location<World> from = e.getFromTransform().getLocation();
		Location<World> to = e.getToTransform().getLocation();
		double distance = to.getPosition().distance(from.getPosition());
		float fallDistance = np.getFallDistance();
		if (!(distance == 0.0D || from.getY() < to.getY())) {
			if (fallDistance == 0.0F && p.getLocation().sub(0, 1, 0).getBlockType().equals(BlockTypes.AIR)) {
				int reliability = Utils.parseInPorcent(distance * 100);
				if (np.justDismounted) {
					// If the player dismounted a vehicule a few ticks ago, we may be wrong
					// For example, flying with pixelmons and dismounting may cause false positives
					reliability *= 0.75;
				}
				if (p.isOnGround()) {
					if (distance > 0.79D) {
						if (SpongeNegativity.alertMod(ReportType.VIOLATION, p, this, reliability,
								"Player in ground. FallDamage: " + fallDistance + ", DistanceBetweenFromAndTo: "
										+ distance + " (ping: " + Utils.getPing(p) + "). Warn: " + np.getWarn(this)))
							np.NO_FALL_DAMAGE += 1;
					} else if (np.NO_FALL_DAMAGE != 0) {
						if (isSetBack())
							p.damage(np.NO_FALL_DAMAGE, DamageSources.FALLING);
						np.NO_FALL_DAMAGE = 0;
					}
				} else {
					if (distance > 2D) {
						boolean mayCancel = SpongeNegativity.alertMod(ReportType.VIOLATION, p, this, reliability,
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
			} else if(!p.isOnGround()) {
				BlockType justUnder = p.getLocation().copy().sub(0, 0.1, 0).getBlock().getType();
				if(justUnder.getProperty(SolidCubeProperty.class).get().getValue() && fallDistance > 3.0) {
					int ping = Utils.getPing(p), relia = Utils.parseInPorcent(100 - (ping / 5) + fallDistance);
					boolean mayCancel = SpongeNegativity.alertMod(ReportType.VIOLATION, p, this, relia,
							"Player not ground with fall damage (FallDistance: " + fallDistance + "). Block 0.1 below: " + justUnder.getId()
									+ ", DistanceBetweenFromAndTo: " + distance + " (ping: " + ping
									+ "). Warn: " + np.getWarn(this), "");
					if(mayCancel && isSetBack())
						manageDamage(p, (int) fallDistance, relia);
				}
			}
		}
	}
	
	private void manageDamage(Player p, int damage, int relia) {
		Adapter ada = Adapter.getAdapter();
		p.damage(damage >= p.health().get() ? (ada.getBooleanInConfig("cheats.nofall.kill") && ada.getDoubleInConfig("cheats.nofall.kill-reliability") >= relia ? damage : p.health().get() - 0.5) : p.health().get(), DamageSource.builder().type(DamageTypes.FALL).build());
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
