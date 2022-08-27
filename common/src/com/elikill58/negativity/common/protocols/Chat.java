package com.elikill58.negativity.common.protocols;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Chat extends Cheat {

	private final List<String> insults = new ArrayList<>();
	
	public Chat() {
		super(CheatKeys.CHAT, CheatCategory.PLAYER, Materials.BOOK_AND_QUILL);
		if(!isActive())
			return;
		CompletableFuture.runAsync(() -> {
			try {
				String lang = getConfig().getString("checks.insult.lang", "en");
				File insultFile = new File(new File(Adapter.getAdapter().getDataFolder(), "modules"), "chat-insults-" + lang + ".txt");
				if(!insultFile.exists()) {
					List<String> lines = UniversalUtils.getListFromURL("https://raw.githubusercontent.com/LDNOOBW/List-of-Dirty-Naughty-Obscene-and-Otherwise-Bad-Words/master/" + lang);
					if(!lines.isEmpty()) {
						insultFile.createNewFile();
						StringJoiner contentToWrite = new StringJoiner("\n");
						for(String line : lines) {
							if(line.isEmpty())
								continue;
							insults.add(line.toLowerCase(Locale.ENGLISH));
							contentToWrite.add(line);
						}
			            Files.write(insultFile.toPath(), contentToWrite.toString().getBytes(), StandardOpenOption.APPEND);
						Adapter.getAdapter().getLogger().warn("Downloaded bad words for lang " + lang + ". Amount used: " + insults.size());
					} else {
						Adapter.getAdapter().getLogger().warn("Failed to get insults from lang " + lang + " in repository: https://github.com/LDNOOBW/List-of-Dirty-Naughty-Obscene-and-Otherwise-Bad-Words .");
						return;
					}
				}
				insults.addAll(Files.readAllLines(insultFile.toPath()));
				getConfig().getStringList("insults").forEach((s) -> {
					insults.add(s.toLowerCase(Locale.ENGLISH));
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Check(name = "spam", description = "Spam of a message")
	public void onChatSpam(PlayerChatEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		PlayerChatEvent lastChat = np.lastChatEvent;
		if (lastChat != null && msg.equalsIgnoreCase(lastChat.getMessage())
				&& (System.currentTimeMillis() - np.timeLastMessage < 5000)) {
			np.LAST_CHAT_MESSAGE_NB++;
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(95 + np.LAST_CHAT_MESSAGE_NB), "spam",
					"Spam " + lastChat.getMessage() + " " + np.LAST_CHAT_MESSAGE_NB + " times",
					hoverMsg("spam", "%msg%", lastChat.getMessage(), "%nb%", np.LAST_CHAT_MESSAGE_NB));
			if (mayCancel && isSetBack())
				e.setCancelled(true);
		} else
			np.LAST_CHAT_MESSAGE_NB = 0;
		np.lastChatEvent = e;
		np.timeLastMessage = System.currentTimeMillis();
	}

	@Check(name = "insult", description = "Manage insult")
	public void onChatInsult(PlayerChatEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		final StringJoiner foundInsults = new StringJoiner(", ");
		for (String s : msg.toLowerCase(Locale.ENGLISH).split(" ")) {
			if (insults.contains(s))
				foundInsults.add(s);
		}
		if (foundInsults.length() > 0) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(90 + (foundInsults.length() - 1) * 5), "insult",
					"Insults: " + foundInsults, hoverMsg("main", "%msg%", foundInsults.toString()));
			if (mayCancel && isSetBack())
				e.setCancelled(true);
		}
	}

	@Check(name = "caps", description = "Too many caps on same message")
	public void onChatCaps(PlayerChatEvent e, NegativityPlayer np) {
		double upperCase = 0;
		String msg = e.getMessage();
		if(msg.length() < 5)
			return; // too low message
		for (int i = 0; i < msg.length(); i++)
			if (Character.isUpperCase(msg.charAt(i)))
				upperCase++;
		double percent = upperCase / msg.length() * 100.0D;
		Adapter.getAdapter().debug("Message: " + msg + ", upper: " + upperCase + ", percent: " + percent);
		if(percent >= getConfig().getDouble("checks.caps.percent", 70)) {
			if(Negativity.alertMod(ReportType.WARNING, e.getPlayer(), this, (int) percent, "caps", "Message: " + msg + ", percent: " + percent, new CheatHover.Literal("Caps message: " + msg + " (" + String.format("%.2f", percent) + "% caps)"), (long) (upperCase - 5)) && isSetBack()) {
				e.setCancelled(true);
			}
		}
	}
}
