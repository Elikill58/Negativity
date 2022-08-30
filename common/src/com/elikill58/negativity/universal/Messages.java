package com.elikill58.negativity.universal;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.account.NegativityAccount;

public class Messages {

	public static String getStateName(CommandSender sender, boolean b) {
		return getMessage(sender, "inventory.manager." + (b ? "enabled" : "disabled"));
	}
	
	/**
	 * Get a message with the default lang
	 * 
	 * @param dir the message key
	 * @param placeholders all message placeholders
	 * @return the translated message
	 */
	public static String getMessage(String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders);
		return ChatColor.color(message);
	}

	/**
	 * Get a message with the default lang
	 * 
	 * @param uuid the UUID of the player which will receive the message
	 * @param dir the message key
	 * @param placeholders all message placeholders
	 * @return the translated message
	 */
	public static String getMessage(UUID uuid, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(NegativityAccount.get(uuid).getLang(), dir, placeholders);
		return ChatColor.color(message);
	}

	/**
	 * Get a message with the default lang
	 * 
	 * @param sender the sender which will receive the message
	 * @param dir the message key
	 * @param placeholders all message placeholders
	 * @return the translated message
	 */
	public static String getMessage(CommandSender sender, String dir, Object... placeholders) {
		String lang = (sender instanceof Player ? TranslatedMessages.getLang(((Player) sender).getUniqueId()) : TranslatedMessages.DEFAULT_LANG);
		String message = TranslatedMessages.getStringFromLang(lang, dir, placeholders);
		return ChatColor.color(message);
	}

	/**
	 * Get a message with the default lang
	 * 
	 * @param account the account which will receive the message
	 * @param dir the message key
	 * @param placeholders all message placeholders
	 * @return the translated message
	 */
	public static String getMessage(NegativityAccount account, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(account.getLang(), dir, placeholders);
		return ChatColor.color(message);
	}

	/**
	 * Send a message to the given command sender
	 * 
	 * @param p the sender which will receive the message
	 * @param dir the message keye
	 * @param placeholders all message placeholders
	 */
	public static void sendMessage(CommandSender p, String dir, Object... placeholders) {
		p.sendMessage(getMessage(p, dir, placeholders));
	}

	/**
	 * Send a list of message to the given command sender
	 * If the key is not found, is will show the key of the message
	 * 
	 * @param sender the sender which will receive the message
	 * @param dir the message key
	 * @param placeholders all messages placeholders
	 * @return list of all messages (list with only "dir" if nothing found)
	 */
	public static List<String> getMessageList(CommandSender sender, String dir, Object... placeholders) {
		String lang = (sender instanceof Player ? TranslatedMessages.getLang(((Player) sender).getUniqueId()) : TranslatedMessages.DEFAULT_LANG);
		List<String> lines = TranslatedMessages.getStringListFromLang(lang, dir, placeholders);
		if(lines.isEmpty()) {
			lines.add(dir);
		}
		return lines.stream().map(ChatColor::color).collect(Collectors.toList());
	}

	/**
	 * Send a list of message to the given command sender
	 * If the key is not found, is will show the key of the message
	 * 
	 * @param sender the sender which will receive the message
	 * @param dir the message key
	 * @param placeholders all messages placeholders
	 */
	public static void sendMessageList(CommandSender sender, String dir, Object... placeholders) {
		getMessageList(sender, dir, placeholders).forEach(sender::sendMessage);
	}
	
	public static void broadcastMessage(String dir, Object... placeholders) {
		Adapter.getAdapter().getOnlinePlayers().forEach((p) -> sendMessage(p, dir, placeholders));
	}
}
