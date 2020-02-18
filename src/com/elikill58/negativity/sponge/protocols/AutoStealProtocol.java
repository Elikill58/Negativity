package com.elikill58.negativity.sponge.protocols;

import java.util.Optional;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class AutoStealProtocol extends Cheat {

	public AutoStealProtocol() {
		super(CheatKeys.AUTO_STEAL, false, ItemTypes.CHEST, false, true, "steal");
	}

	public static final int TIME_CLICK = 50;

	@Listener
	@Exclude(ClickInventoryEvent.Double.class)
	public void onInvClick(ClickInventoryEvent e, @First Player p) {
		if (!(p.gameMode().get().equals(GameModes.SURVIVAL) || p.gameMode().get().equals(GameModes.ADVENTURE))) {
			return;
		}

		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}

		np.haveClick = true;
		Optional<ItemStack> itemInHand = p.getItemInHand(HandTypes.MAIN_HAND);
		if (itemInHand.isPresent()) {
			ItemUseBypass bypass = ItemUseBypass.ITEM_BYPASS.get(itemInHand.get().getType());
			if (bypass != null && bypass.getWhen().isClick() && bypass.isForThisCheat(this)) {
				return;
			}
		}

		long actual = System.currentTimeMillis();
		long diff = actual - np.LAST_CLICK_INV;
		int ping = Utils.getPing(p);
		if ((ping + TIME_CLICK) >= diff) {
			if (np.lastClickInv && isSetBack() && SpongeNegativity.alertMod(ReportType.WARNING, p, this, Utils.parseInPorcent((100 + TIME_CLICK) - diff - ping), "Time between 2 click: " + diff + ". Ping: " + ping, "Time between 2 clicks: " + diff)) {
				e.setCancelled(true);
			}
			np.lastClickInv = true;
		} else {
			np.lastClickInv = false;
		}
		np.LAST_CLICK_INV = actual;
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
