package com.elikill58.negativity.spigot.protocols;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ChatProtocol extends Cheat implements Listener {

	public ChatProtocol() {
		super(CheatKeys.CHAT, false, Utils.getMaterialWith1_15_Compatibility("BOOK_AND_QUILL", "LEGACY_BOOK_AND_QUILL"), CheatCategory.PLAYER, true);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		String msg = e.getMessage();
		if(msg.equalsIgnoreCase(np.LAST_CHAT_MESSAGE)) {
			np.LAST_CHAT_MESSAGE_NB++;
			Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
				boolean mayCancel = SpigotNegativity.alertMod(np.LAST_CHAT_MESSAGE_NB > 2 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent(95 + np.LAST_CHAT_MESSAGE_NB), "Spam " + np.LAST_CHAT_MESSAGE + " " + np.LAST_CHAT_MESSAGE_NB + " times",
						"Spam " + np.LAST_CHAT_MESSAGE + " " + np.LAST_CHAT_MESSAGE_NB + " times");
				if(mayCancel && isSetBack())
					e.setCancelled(true);
			});
		} else
			np.LAST_CHAT_MESSAGE_NB = 0;
		np.LAST_CHAT_MESSAGE = msg;
		
		final List<String> insults = new ArrayList<>();
		SpigotNegativity.getInstance().getConfig().getStringList("cheats.chat.insults").forEach((s) -> {
			insults.add(s.toLowerCase());
		});
		final StringJoiner foundedInsults = new StringJoiner(", ");
		for(String s : msg.toLowerCase().split(" ")) {
			if(insults.contains(s))
				foundedInsults.add(s);
		}
		if(foundedInsults.length() == 0) {
			Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
				boolean mayCancel = SpigotNegativity.alertMod(foundedInsults.length() > 1 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent(90 + (foundedInsults.length() - 1) * 5), "Insults: " + foundedInsults.toString(),
						getHover("main", "%msg%", foundedInsults.toString()));
				if(mayCancel && isSetBack())
					e.setCancelled(true);
			});
		}
	}
}
