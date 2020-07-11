package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
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

public class NoSlowDownProtocol extends Cheat {

	public NoSlowDownProtocol() {
		super(CheatKeys.NO_SLOW_DOWN, false, ItemTypes.SOUL_SAND, CheatCategory.MOVEMENT, true, "slowdown");
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

		Location<?> loc = p.getLocation();
		Location<World> from = e.getFromTransform().getLocation();
		Location<World> to = e.getToTransform().getLocation();
		double xSpeed = Math.abs(from.getX() - to.getX());
	    double zSpeed = Math.abs(from.getZ() - to.getZ());
	    double xzSpeed = Math.sqrt(xSpeed * xSpeed + zSpeed * zSpeed);
	    np.contentDouble.put("slowdown-eating-distance", xSpeed >= zSpeed ? xSpeed : zSpeed);
	    if (np.contentDouble.get("slowdown-eating-distance") < xzSpeed)
	    	np.contentDouble.put("slowdown-eating-distance", xzSpeed);
		if (!loc.getBlockType().equals(BlockTypes.SOUL_SAND)) {
			return;
		}

		for (PotionEffect pe : np.getActiveEffects()) {
			if (pe.getType().equals(PotionEffectTypes.SPEED) && pe.getAmplifier() > 1) {
				return;
			}
		}

		double distance = to.getPosition().distance(from.getPosition());
		if (distance > 0.2) {
			int ping = Utils.getPing(p);
			int relia = UniversalUtils.parseInPorcent(distance * 400);
			if ((e.getFromTransform().getYaw() - e.getToTransform().getYaw()) < -0.001) {
				return;
			}

			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, relia,
					"Soul sand under player. Distance from/to : " + distance + ". Ping: " + ping);
			if (isSetBack() && mayCancel) {
				e.setCancelled(true);
			}
		}
	}
	
	@Listener
	public void onItemConsume(UseItemStackEvent.Finish e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if(p.getVehicle().isPresent())
			return;
		double dis = np.contentDouble.getOrDefault("slowdown-eating-distance", 0.0);
		if (dis > np.getWalkSpeed() || p.get(Keys.IS_SPRINTING).orElse(false)) {
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(dis * 200),
					"Distance while eating: " + dis + ", WalkSpeed: " + np.getWalkSpeed(), hoverMsg("main", "%distance%", String.format("%.2f", dis)));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
}
