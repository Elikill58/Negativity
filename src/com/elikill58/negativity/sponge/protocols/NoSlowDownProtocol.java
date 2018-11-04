package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.sponge.NeedListener;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Cheat;
import com.elikill58.negativity.sponge.utils.ReportType;
import com.elikill58.negativity.sponge.utils.Utils;

public class NoSlowDownProtocol implements NeedListener {

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(Cheat.NOSLOWDOWN))
			return;
		Location<?> loc = p.getLocation();
		if (!loc.getBlock().getType().equals(BlockTypes.SOUL_SAND))
			return;
		for (PotionEffect pe : p.getOrCreate(PotionEffectData.class).get().asList())
			if (pe.getType().equals(PotionEffectTypes.SPEED) && pe.getAmplifier() > 1)
				return;
		Transform<World> from = e.getFromTransform(), to = e.getToTransform();
		double distance = to.getPosition().distance(from.getPosition());
		if (distance > 0.2) {
			Location<World> fl = from.getLocation().copy().sub(to.getLocation().getX(), to.getLocation().getY(), to.getLocation().getZ());
			int ping = Utils.getPing(p), relia = Utils.parseInPorcent(distance * 400);
			if((from.getYaw() - to.getYaw()) < -0.001)
				return;
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.NOSLOWDOWN, relia,
					"Soul sand under player. Distance from/to : " + distance + ". Ping: " + ping);
			if (Cheat.NOSLOWDOWN.isSetBack() && mayCancel)
				e.setToTransform(new Transform<>(new Location<World>(from.getExtent(), fl.getX() / 2, fl.getY() / 2, fl.getZ()).add(0, 0.5, 0)));
		}
	}
}
