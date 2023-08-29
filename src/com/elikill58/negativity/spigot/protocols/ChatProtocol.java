package com.elikill58.negativity.spigot.protocols;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ChatProtocol extends Cheat implements Listener {

	public ChatProtocol() {
		super(CheatKeys.CHAT, false, ItemUtils.BOOK_AND_QUILL, CheatCategory.PLAYER, true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		Stats.updateMessage(np, e.getMessage());
		if (!np.hasDetectionActive(this))
			return;
		String msg = e.getMessage();
		long diff = System.currentTimeMillis() - np.TIME_LAST_MESSAGE;
		if(msg.equalsIgnoreCase(np.LAST_CHAT_MESSAGE)) {
			np.TIME_LAST_MESSAGE = System.currentTimeMillis();
			np.LAST_CHAT_MESSAGE_NB++;
			if(np.LAST_CHAT_MESSAGE_NB >= 2 && diff <= 3000) {
				Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
					boolean mayCancel = SpigotNegativity.alertMod(np.LAST_CHAT_MESSAGE_NB > 2 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent(95 + np.LAST_CHAT_MESSAGE_NB), "Spam " + np.LAST_CHAT_MESSAGE + " " + np.LAST_CHAT_MESSAGE_NB + " times",
							hoverMsg("spam", "%msg%", np.LAST_CHAT_MESSAGE, "%nb%", np.LAST_CHAT_MESSAGE_NB));
					if(mayCancel && isSetBack())
						e.setCancelled(true);
				});
			}
		} else
			np.LAST_CHAT_MESSAGE_NB = 0;
		np.LAST_CHAT_MESSAGE = msg;
		
		final List<String> insults = new ArrayList<>();
		SpigotNegativity.getInstance().getConfig().getStringList("cheats.chat.insults").forEach((s) -> {
			insults.add(s.toLowerCase());
		});
		final StringJoiner foundInsults = new StringJoiner(", ");
		for(String s : msg.toLowerCase().split(" ")) {
			if(insults.contains(s))
				foundInsults.add(s);
		}
		Adapter.getAdapter().debug("Insult founded: " + foundInsults.toString() + ", msg: " + msg.toLowerCase());
		if(foundInsults.length() > 0) {
			boolean mayCancel = SpigotNegativity.alertMod(foundInsults.length() > 1 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(90 + (foundInsults.length() - 1) * 5), "Insults: " + foundInsults.toString(),
					hoverMsg("main", "%msg%", foundInsults.toString()));
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
	}
}
