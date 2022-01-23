package com.elikill58.negativity.common.protocols;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Chat extends Cheat implements Listeners {

	public Chat() {
		super(CheatKeys.CHAT, CheatCategory.PLAYER, Materials.BOOK_AND_QUILL, false, false);
	}

	@Check(name = "spam", description = "Spam of a message")
	public void onChatSpam(PlayerChatEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		PlayerChatEvent lastChat = np.LAST_CHAT_EVENT;
		if(lastChat != null && msg.equalsIgnoreCase(lastChat.getMessage()) && (System.currentTimeMillis() - np.TIME_LAST_MESSAGE < 5000)) {
			np.LAST_CHAT_MESSAGE_NB++;
			boolean mayCancel = Negativity.alertMod(np.LAST_CHAT_MESSAGE_NB > 2 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(95 + np.LAST_CHAT_MESSAGE_NB), "spam", "Spam " + lastChat.getMessage() + " " + np.LAST_CHAT_MESSAGE_NB + " times",
					hoverMsg("spam", "%msg%", lastChat.getMessage(), "%nb%", np.LAST_CHAT_MESSAGE_NB));
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		} else
			np.LAST_CHAT_MESSAGE_NB = 0;
		np.LAST_CHAT_EVENT = e;
		np.TIME_LAST_MESSAGE = System.currentTimeMillis();
	}

	@Check(name = "insult", description = "Manage insult")
	public void onChatInsult(PlayerChatEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		final List<String> insults = new ArrayList<>();
		getConfig().getStringList("insults").forEach((s) -> {
			insults.add(s.toLowerCase(Locale.ENGLISH));
		});
		final StringJoiner foundInsults = new StringJoiner(", ");
		for(String s : msg.toLowerCase(Locale.ENGLISH).split(" ")) {
			if(insults.contains(s))
				foundInsults.add(s);
		}
		if(foundInsults.length() > 0) {
			boolean mayCancel = Negativity.alertMod(foundInsults.length() > 1 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(90 + (foundInsults.length() - 1) * 5), "insult", "Insults: " + foundInsults,
					hoverMsg("main", "%msg%", foundInsults.toString()));
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
	}

}
