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
		double distance = e.getFromTransform().getPosition().distance(e.getToTransform().getPosition());
		if(distance > 0.6 && (distance < (np.lastDistanceFastStairs * 2))) {
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 145), "Player without fall damage. Block: " + blockName + ", distance: " + distance);
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
		np.lastDistanceFastStairs = distance;
	}
}
