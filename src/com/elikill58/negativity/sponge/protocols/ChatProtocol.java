package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ChatProtocol extends Cheat {

	public ChatProtocol() {
		super(CheatKeys.CHAT, false, ItemTypes.WRITABLE_BOOK, false, true);
	}

	@Listener
	public void onChat(MessageChannelEvent.Chat e, @First Player p) {
		if (!p.gameMode().get().equals(GameModes.SURVIVAL) && !p.gameMode().get().equals(GameModes.ADVENTURE)) {
			return;
		}
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		String msg = e.getMessage().toPlain();
		String withoutEvading = msg.replaceAll(" ", "").toLowerCase();
		String foundedInsults = "";
		for(String insults : Adapter.getAdapter().getStringListInConfig("cheats.chat.insults")) {
			if(withoutEvading.contains(insults.toLowerCase()))
				foundedInsults = (foundedInsults.equalsIgnoreCase("") ? "" : foundedInsults + ", ") + insults;
		}
		if(!foundedInsults.equalsIgnoreCase("")) {
			boolean mayCancel = SpongeNegativity.alertMod(foundedInsults.contains(", ") ? ReportType.VIOLATION : ReportType.WARNING, p, this,
					Utils.parseInPorcent(80 + (foundedInsults.split(", ").length - 1) * 10), "Insults: " + foundedInsults, "Insults: " + foundedInsults);
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
