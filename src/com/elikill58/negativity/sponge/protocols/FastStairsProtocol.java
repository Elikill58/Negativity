package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.flowpowered.math.vector.Vector3d;

public class FastStairsProtocol extends Cheat {

	public FastStairsProtocol() {
		super(CheatKeys.FAST_STAIRS, false, ItemTypes.BIRCH_STAIRS,	CheatCategory.MOVEMENT, true, "stairs");
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
		
		if(np.getFallDistance() != 0)
			return;
		String blockName = e.getToTransform().getLocation().copy().sub(0, 0.0001, 0).getBlock().getType().getId();
		if(!blockName.contains("STAIRS"))
			return;
		Vector3d to = e.getToTransform().getPosition();
		Vector3d from = new Vector3d(e.getFromTransform().getPosition().getX(), to.getY(), e.getFromTransform().getPosition().getZ());
		double distance = from.distance(to);
		if(distance > 0.4 && np.lastDistanceFastStairs > distance) {
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 145), "Player without fall damage. Block: " + blockName + ", distance: " + distance);
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
		np.lastDistanceFastStairs = distance;
	}
}
