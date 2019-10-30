package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.scheduler.Task;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class ScaffoldProtocol extends Cheat {

	private ScaffoldProtocol instance;
	
	public ScaffoldProtocol() {
		super("SCAFFOLD", false, ItemTypes.GRASS, false, true);
		instance = this;
	}
	
	@Listener
	public void onBlockBreak(ChangeBlockEvent.Place e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		int ping = Utils.getPing(p), slot = -1;
		if (ping > 120)
			return;
		Task.builder().delayTicks(0).execute(() -> {
			ItemType m = np.getItemTypeInHand();
			BlockType placed = e.getTransactions().get(0).getOriginal().getLocation().get().getBlock().getType();
			if(!placed.getItem().isPresent())
				return;
			if ((m == null || (!np.isBlock(m) && !m.equals(placed.getItem().get()))) && slot != 0 && !placed.equals(BlockTypes.AIR)) {
				int localPing = ping;
				if (localPing == 0)
					localPing = 1;
				boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, instance, Utils.parseInPorcent(120 / localPing),
						"Item in hand: " + m.getName() + " Block placed: " + placed.getName() + " Ping: " + ping,
						"Item in hand: " + m.getName().toLowerCase() + " \nBlock placed: "
								+ placed.getName().toLowerCase());
				if (isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		}).submit(SpongeNegativity.getInstance());
	}
	
	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
