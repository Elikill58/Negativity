package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.scheduler.Task;

import com.elikill58.negativity.sponge.NeedListener;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Cheat;
import com.elikill58.negativity.sponge.utils.ReportType;
import com.elikill58.negativity.sponge.utils.Utils;

public class ScaffoldProtocol implements NeedListener {

	@Listener
	public void onBlockBreak(ChangeBlockEvent.Place e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(Cheat.SCAFFOLD))
			return;
		int ping = Utils.getPing(p), slot = -1;
		if (ping > 120)
			return;
		Task.builder().delayTicks(0).execute(() -> {
			ItemType m = np.getItemTypeInHand();
			BlockType placed = e.getTransactions().get(0).getFinal().getLocation().get().getBlock().getType();
			if(!placed.getItem().isPresent())
				return;
			if ((m == null || (!np.isBlock(m) && !m.equals(placed.getItem().get()))) && slot != 0) {
				int localPing = ping;
				if (localPing == 0)
					localPing = 1;
				boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, Cheat.SCAFFOLD, Utils.parseInPorcent(120 / localPing),
						"Item in hand: " + m.getName() + " Block placed: " + placed.getName() + " Ping: " + ping,
						"Item in hand: " + m.getName().toLowerCase() + " \nBlock placed: "
								+ placed.getName().toLowerCase());
				if (Cheat.SCAFFOLD.isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}).submit(SpongeNegativity.getInstance());
	}
}
