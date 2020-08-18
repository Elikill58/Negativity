package com.elikill58.negativity.common.protocols;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.bukkit.Bukkit;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Chat extends Cheat implements Listeners {

	public Chat() {
		super(CheatKeys.CHAT, false, Materials.BOOK_AND_QUILL, CheatCategory.PLAYER, true);
	}

	@EventListener
	public void onChat(PlayerChatEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		String msg = e.getMessage();
		if(checkActive("spam")) {
			PlayerChatEvent lastChat = np.LAST_CHAT_EVENT;
			if(lastChat != null && msg.equalsIgnoreCase(lastChat.getMessage()) && (System.currentTimeMillis() - np.LAST_CHAT_MESSAGE_NB < 5000)) {
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

		if(checkActive("insult")) {
			final List<String> insults = new ArrayList<>();
			getConfig().getStringList("insults").forEach((s) -> {
				insults.add(s.toLowerCase());
			});
			final StringJoiner foundInsults = new StringJoiner(", ");
			for(String s : msg.toLowerCase().split(" ")) {
				if(insults.contains(s))
					foundInsults.add(s);
			}
			if(foundInsults.length() > 0) {
				Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
					boolean mayCancel = Negativity.alertMod(foundInsults.length() > 1 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent(90 + (foundInsults.length() - 1) * 5), "insult", "Insults: " + foundInsults.toString(),
							hoverMsg("main", "%msg%", foundInsults.toString()));
					if(mayCancel && isSetBack())
						e.setCancelled(true);
				});
			}
		}
	}
}
