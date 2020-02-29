package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Transform;
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
import com.flowpowered.math.vector.Vector3d;

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
		if (!loc.getBlockType().equals(BlockTypes.SOUL_SAND)) {
			return;
		}

		for (PotionEffect pe : np.getActiveEffects()) {
			if (pe.getType().equals(PotionEffectTypes.SPEED) && pe.getAmplifier() > 1) {
				return;
			}
		}

		Transform<World> from = e.getFromTransform();
		Transform<World> to = e.getToTransform();
		double distance = to.getPosition().distance(from.getPosition());
		if (distance > 0.2) {
			int ping = Utils.getPing(p);
			int relia = UniversalUtils.parseInPorcent(distance * 400);
			if ((from.getYaw() - to.getYaw()) < -0.001) {
				return;
			}

			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, relia,
					"Soul sand under player. Distance from/to : " + distance + ". Ping: " + ping);
			if (isSetBack() && mayCancel) {
				Vector3d delta = from.getPosition().sub(to.getPosition());
				Vector3d setBackPosition = new Vector3d(delta.getX() / 2, delta.getY() / 2 + 0.5, delta.getZ());
				e.setToTransform(from.setPosition(setBackPosition));
			}
		}
	}
}
