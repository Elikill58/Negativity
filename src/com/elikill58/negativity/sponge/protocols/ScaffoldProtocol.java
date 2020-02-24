package com.elikill58.negativity.sponge.protocols;

import java.util.Optional;

import org.spongepowered.api.block.BlockType;
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
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class ScaffoldProtocol extends Cheat {

	public ScaffoldProtocol() {
		super(CheatKeys.SCAFFOLD, false, ItemTypes.GRASS, CheatCategory.WORLD, true);
	}

	@Listener
	public void onBlockBreak(ChangeBlockEvent.Place e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}

		int ping = Utils.getPing(p);
		if (ping > 120) {
			return;
		}

		// TODO get current the selected slot, and replace the 'slot != 0' appearing
		//  later with a comparison with the hotbar selected slot
		int slot = -1;
		Task.builder().delayTicks(0).execute(() -> {
			ItemType m = np.getItemTypeInHand();
			BlockType placed = e.getTransactions().get(0).getOriginal().getState().getType();
			Optional<ItemType> itemForPlacedBlock = placed.getItem();
			if (itemForPlacedBlock.isPresent() && !m.equals(itemForPlacedBlock.get())) {
				return;
			}

			if (!np.isBlock(m) && slot != 0) {
				int localPing = ping;
				if (localPing == 0) {
					localPing = 1;
				}

				boolean mayCancel = SpongeNegativity.alertMod(ReportType.WARNING, p, this, Utils.parseInPorcent(120 / localPing),
						"Item in hand: " + m.getName() + " Block placed: " + placed.getName() + " Ping: " + ping,
						"Item in hand: " + m.getName().toLowerCase() + " \nBlock placed: "
								+ placed.getName().toLowerCase());
				if (isSetBack() && mayCancel) {
					e.setCancelled(true);
				}
			}
		}).submit(SpongeNegativity.getInstance());
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
