package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.PlayerCheatBypassEvent;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;
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
		if(msg.equals(np.LAST_CHAT_MESSAGE)) {
			if (SpigotNegativity.hasBypass && Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p),
					"bypass.chat")) {
				PlayerCheatBypassEvent bypassEvent = new PlayerCheatBypassEvent(p, this, 100);
				Bukkit.getPluginManager().callEvent(bypassEvent);
				if (bypassEvent.isCancelled()) {
					Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
						boolean mayCancel = SpigotNegativity.alertMod(np.LAST_CHAT_MESSAGE_NB > 2 ? ReportType.VIOLATION : ReportType.WARNING, p, this,
								UniversalUtils.parseInPorcent(100), "Spam " + np.LAST_CHAT_MESSAGE + " " + np.LAST_CHAT_MESSAGE_NB + " times",
								"Spam " + np.LAST_CHAT_MESSAGE + " " + np.LAST_CHAT_MESSAGE_NB + " times");
						if(mayCancel && isSetBack())
							e.setCancelled(true);
					});
				}
			}
			np.LAST_CHAT_MESSAGE_NB++;
		} else
			np.LAST_CHAT_MESSAGE_NB = 0;
		np.LAST_CHAT_MESSAGE = msg;
		String withoutEvading = msg.replaceAll(" ", "").toLowerCase();
		String foundedInsults = "";
		for(String insults : Adapter.getAdapter().getStringListInConfig("cheats.chat.insults")) {
			if(withoutEvading.contains(insults.toLowerCase()))
				foundedInsults = (foundedInsults.equalsIgnoreCase("") ? "" : foundedInsults + ", ") + insults;
		}
		if(!foundedInsults.equalsIgnoreCase("")) {
			final String finalString = foundedInsults;
			Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
				boolean mayCancel = SpigotNegativity.alertMod(finalString.contains(", ") ? ReportType.VIOLATION : ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent(80 + (finalString.split(", ").length - 1) * 10), "Insults: " + finalString, "Insults: " + finalString);
				if(mayCancel && isSetBack())
					e.setCancelled(true);
			});
		}
	}
}
