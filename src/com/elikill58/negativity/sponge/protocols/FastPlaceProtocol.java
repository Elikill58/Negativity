package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

import com.elikill58.negativity.sponge.NeedListener;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Cheat;
import com.elikill58.negativity.sponge.utils.ReportType;
import com.elikill58.negativity.sponge.utils.Utils;

public class FastPlaceProtocol implements NeedListener {

	@Listener
	public void onBlockPlace(ChangeBlockEvent.Place e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE))
			return;
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(Cheat.FASTPLACE))
			return;
		long last = System.currentTimeMillis() - np.LAST_BLOCK_PLACE, lastPing = last - (Utils.getPing(p) / 9);
		np.LAST_BLOCK_PLACE = System.currentTimeMillis();
		if (lastPing < 50) {
			boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.FASTPLACE,
					Utils.parseInPorcent(last * 2), "Blockplaced too quickly. Last time: " + last + ", Last with ping: "
							+ lastPing + ". Ping: " + Utils.getPing(p),
					"2 blocks placed in: " + last + " ms\nReal player do it in 150/200ms");
			if(Cheat.FASTPLACE.isSetBack() && mayCancel)
				e.setCancelled(true);
		}

	}
}
