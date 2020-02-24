package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockTypes;
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
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class PhaseProtocol extends Cheat {

	public PhaseProtocol() {
		super(CheatKeys.PHASE, false, ItemTypes.STAINED_GLASS, CheatCategory.MOVEMENT, true);
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

		Location<World> loc = p.getLocation();
		Transform<World> from = e.getFromTransform();
		Transform<World> to = e.getToTransform();
		double yDiff = to.getPosition().getY() - from.getPosition().getY();
		if (yDiff > 0.1 && (loc.sub(Vector3i.UNIT_Y).getBlockType() != BlockTypes.AIR
				|| !np.hasOtherThan(loc.sub(Vector3i.UNIT_Y), BlockTypes.AIR))) {
			np.isJumpingWithBlock = true;
		} else if (yDiff < -0.1) {
			np.isJumpingWithBlock = false;
		}

		if (yDiff < 0
				|| loc.sub(0, 1, 0).getBlockType() != BlockTypes.AIR
				|| loc.sub(0, 2, 0).getBlockType() != BlockTypes.AIR
				|| loc.sub(0, 3, 0).getBlockType() != BlockTypes.AIR
				|| loc.sub(0, 4, 0).getBlockType() != BlockTypes.AIR) {
			return;
		}

		if (np.hasOtherThan(loc, BlockTypes.AIR)
				|| np.hasOtherThan(loc.sub(Vector3d.UNIT_Y), BlockTypes.AIR)) {
			return;
		}

		if (!np.isJumpingWithBlock) {
			SpongeNegativity.alertMod(ReportType.VIOLATION, p, this, Utils.parseInPorcent((yDiff * 200) + 20),
					"Player on air. No jumping. DistanceBetweenFromAndTo: " + yDiff + " (ping: " + Utils.getPing(p)
							+ "). Warn: " + np.getWarn(this));
		}
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
