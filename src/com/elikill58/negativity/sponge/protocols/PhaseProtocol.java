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
import com.elikill58.negativity.sponge.utils.ReportType;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

public class PhaseProtocol extends Cheat {

	public PhaseProtocol() {
		super("PHASE", false, ItemTypes.STAINED_GLASS, false, true);
	}
	
	@Listener
	public void onPlayerMove(MoveEntityEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		Location<World> loc = p.getLocation();
		Transform<World> from = e.getFromTransform(), to = e.getToTransform();
		double y = to.getYaw() - from.getYaw();
		if (y > 0.1 && (!loc.copy().sub(0, 1, 0).getBlock().getType().equals(BlockTypes.AIR)
				|| !np.hasOtherThan(loc.copy().sub(0, 1, 0), BlockTypes.AIR)))
			np.isJumpingWithBlock = true;
		if (y < -0.1)
			np.isJumpingWithBlock = false;
		if (!loc.copy().sub(0, 1, 0).getBlock().getType().equals(BlockTypes.AIR)
				|| !loc.copy().sub(0, 2, 0).getBlock().getType().equals(BlockTypes.AIR)
				|| !loc.copy().sub(0, 3, 0).getBlock().getType().equals(BlockTypes.AIR)
				|| !loc.copy().sub(0, 4, 0).getBlock().getType().equals(BlockTypes.AIR))
			return;
		if (y < 0)
			return;
		if (np.hasOtherThan(loc.copy(), BlockTypes.AIR) || np.hasOtherThan(loc.copy().sub(0, 1, 0), BlockTypes.AIR))
			return;
		if (!np.isJumpingWithBlock) {
			SpongeNegativity.alertMod(ReportType.VIOLATION, p, this, Utils.parseInPorcent((y * 200) + 20),
					"Player on air. No jumping. DistanceBetweenFromAndTo: " + y + " (ping: " + Utils.getPing(p)
							+ "). Warn: " + np.getWarn(this));
		}
	}
}
