package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
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

public class NoFallProtocol implements NeedListener {

	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(Cheat.NOFALL))
			return;
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		Location<World> from = e.getFromTransform().getLocation(), to = e.getToTransform().getLocation();
		double distance = to.getPosition().distance(from.getPosition());
		float fallDistance = np.getFallDistance();
		if (!(p.getVehicle().isPresent() || distance == 0.0D || from.getY() < to.getY()) && fallDistance == 0.0F
				&& !np.hasPotionEffect(PotionEffectTypes.SPEED)
				&& p.getLocation().copy().sub(0, 1, 0).getBlock().getType().equals(BlockTypes.AIR)) {
			if (p.isOnGround() || distance > 0.79D) {
				if (SpongeNegativity.alertMod(ReportType.VIOLATION, p, Cheat.NOFALL,
						Utils.parseInPorcent(distance * 100),
						"Player in ground. FallDamage: " + fallDistance + ", DistanceBetweenFromAndTo: " + distance
								+ " (ping: " + Utils.getPing(p) + "). Warn: " + np.getWarn(Cheat.NOFALL)))
					np.NO_FALL_DAMAGE += 1;
			}
		} else if (np.NO_FALL_DAMAGE != 0) {
			if (Cheat.NOFALL.isSetBack())
				p.damage(np.NO_FALL_DAMAGE, DamageSources.FALLING);
			np.NO_FALL_DAMAGE = 0;
		}
	}
}
