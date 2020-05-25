package com.elikill58.negativity.sponge.protocols;

import java.util.StringJoiner;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ChatProtocol extends Cheat {

	public ChatProtocol() {
		super(CheatKeys.CHAT, false, ItemTypes.WRITABLE_BOOK, CheatCategory.PLAYER, true);
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
		StringJoiner foundedInsults = new StringJoiner(", ");
		for(String insults : Adapter.getAdapter().getConfig().getStringList("cheats.chat.insults")) {
			if(withoutEvading.contains(insults.toLowerCase()))
				foundedInsults.add(insults);
		}
		if(foundedInsults.length() > 0) {
			boolean mayCancel = SpongeNegativity.alertMod(foundedInsults.length() > 1 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(80 + (foundedInsults.length() - 1) * 10), "Insults: " + foundedInsults, new CheatHover("main", "%msg%", foundedInsults));
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
	}
}
