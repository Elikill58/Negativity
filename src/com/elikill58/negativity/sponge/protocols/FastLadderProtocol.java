package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
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
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.flowpowered.math.vector.Vector3d;

public class FastLadderProtocol extends Cheat {

	public FastLadderProtocol() {
		super(CheatKeys.FAST_LADDER, false, ItemTypes.LADDER, CheatCategory.WORLD, true, "ladder", "ladders");
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
		if (loc.getBlockType() != BlockTypes.LADDER) {
			np.isOnLadders = false;
			return;
		}

		if (!np.isOnLadders) {
			np.isOnLadders = true;
			return;
		}

		if (np.isFlying() ||np.hasPotionEffect(PotionEffectTypes.JUMP_BOOST)) {
			return;
		}

		for (PotionEffect pe : p.getOrCreate(PotionEffectData.class).get().asList()) {
			if (pe.getType().equals(PotionEffectTypes.SPEED) && pe.getAmplifier() > 2) {
				return;
			}
		}

		Location<World> from = e.getFromTransform().getLocation();
		Location<World> to = e.getToTransform().getLocation();
		double distance = to.getPosition().distance(from.getPosition());
		int nbLadder = 0;
		Location<?> nextLadderLoc = loc;
		while (nextLadderLoc.getBlockType() == BlockTypes.LADDER) {
			nbLadder++;
			nextLadderLoc = nextLadderLoc.add(0, -1, 0);
		}

		if (distance > 0.23 && distance < 3.8 && nbLadder > 2 && loc.add(0, 1, 0).getBlock().getType().getId().contains("LADDER")) {
			int ping = Utils.getPing(p);
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 350),
					"On ladders. Distance from/to : " + distance + ". Ping: " + ping + "ms. Number of Ladder: " + nbLadder, getHover("main", "%nb%", nbLadder));
			if (isSetBack() && mayCancel) {
				Vector3d movementDelta = from.getPosition().sub(to.getPosition());
				Vector3d setBackPosition = new Vector3d(movementDelta.getX() / 2, (movementDelta.getY() / 2) + 0.5, movementDelta.getZ());
				e.setToTransform(e.getFromTransform().setPosition(setBackPosition));
			}
		}
	}
}
