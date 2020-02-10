package com.elikill58.negativity.spigot.protocols;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ChatProtocol extends Cheat implements Listener {

	public ChatProtocol() {
		super("CHAT", false, Utils.getMaterialWith1_15_Compatibility("BOOK_AND_QUILL", "LEGACY_BOOK_AND_QUILL"), false, true);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		String msg = e.getMessage();
		String withoutEvading = msg.replaceAll(" ", "").toLowerCase();
		String foundedInsults = "";
		for(String insults : Adapter.getAdapter().getStringListInConfig("cheats.chat.insults")) {
			if(withoutEvading.contains(insults.toLowerCase()))
				foundedInsults = (foundedInsults.equalsIgnoreCase("") ? "" : foundedInsults + ", ") + insults;
		}
		if(!foundedInsults.equalsIgnoreCase("")) {
			boolean mayCancel = SpigotNegativity.alertMod(foundedInsults.contains(", ") ? ReportType.VIOLATION : ReportType.WARNING, p, this,
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
