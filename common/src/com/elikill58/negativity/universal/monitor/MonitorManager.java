package com.elikill58.negativity.universal.monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.monitor.cpu.CpuMonitorManager;

public abstract class MonitorManager {

	private static List<MonitorManager> monitors = new ArrayList<>();
	public static List<MonitorManager> getMonitors() {
		return monitors;
	}
	
	public static void load() {
		monitors.forEach(MonitorManager::disable);
		monitors = new ArrayList<>(Arrays.asList(new CpuMonitorManager()));
		monitors.forEach(MonitorManager::enable);
	}
	
	/**
	 * Enable actual monitor
	 */
	public abstract void enable();
	
	/**
	 * Get the name of what the monitor is checking
	 * 
	 * @return the name
	 */
	public abstract @NonNull String getName();

	/**
	 * Get description that will be showed at TOP of results
	 * 
	 * @return the description
	 */
	public @NonNull String getDescription() {
		return ChatColor.YELLOW + "Result of monitor for " + getName() + ":";
	}
	
	/**
	 * Show all result lines.<br>
	 * It's not recommend to use this method, because it can spam the chat.
	 * 
	 * @param sender who should see result
	 */
	public void showRawResult(CommandSender sender) {
		sender.sendMessage(getDescription());
		getHeaderResult().forEach(sender::sendMessage);
		getRawResult().forEach(sender::sendMessage);
		getFooterResult().forEach(sender::sendMessage);
	}

	/**
	 * Show cleaned result lines.
	 * 
	 * @param sender who should see result
	 */
	public void showCleanedResult(CommandSender sender) {
		sender.sendMessage(getDescription());
		getHeaderResult().forEach(sender::sendMessage);
		getCleanedResult().forEach(sender::sendMessage);
		getFooterResult().forEach(sender::sendMessage);
	}

	/**
	 * See parsed result with color
	 * 
	 * @param sender who should see result
	 */
	public void showPerCheatResult(CommandSender sender) {
		sender.sendMessage(getDescription());
		getHeaderResult().forEach(sender::sendMessage);
		getResultPerCheat().forEach((cheat, lines) -> {
			if(lines.isEmpty())
				return; // ignore cheat without any informations
			String cheatName = Cheat.forKey(cheat).getName();
			if(lines.size() == 1) {
				sender.sendMessage(ChatColor.GREEN + " " + cheatName + ChatColor.GRAY + ": " + ChatColor.YELLOW + lines.get(0));
			} else {
				sender.sendMessage(ChatColor.GREEN + " " + cheatName + ChatColor.GRAY + ": ");
				lines.forEach(line -> sender.sendMessage(ChatColor.YELLOW  + "  " + lines.get(0)));
			}
		});
		getFooterResult().forEach(sender::sendMessage);
	}
	
	/**
	 * Get header of result. Used for raw & cleaned result.<br>
	 * Should include description of the next result
	 * 
	 * @return the footer, or empty list
	 */
	public @NonNull List<String> getHeaderResult() {
		return new ArrayList<>();
	}
	
	/**
	 * Get all result lines, even if it doesn't concerned Negativity plugin.
	 * 
	 * @return all results lines
	 */
	public abstract @NonNull List<String> getRawResult();
	
	/**
	 * Get cleaned lines. It will return only which one is concerned by Negativity plugin
	 * 
	 * @return cleaned results lines
	 */
	public abstract @NonNull List<String> getCleanedResult();

	/**
	 * Tell if the {@link #getResultPerCheat()} method is supported.
	 * 
	 * @return true if can parse result
	 */
	public abstract boolean canParsePerCheat();
	
	/**
	 * Get result per cheat.<br>
	 * Use {@link #canParsePerCheat()} to be sure the map can be filled.
	 * 
	 * @return all information per key
	 */
	public abstract HashMap<CheatKeys, List<String>> getResultPerCheat();
	
	/**
	 * Get footer of result. Used for raw & cleaned result.<br>
	 * Should include conclusion and things like that.
	 * 
	 * @return the footer, or empty list
	 */
	public @NonNull List<String> getFooterResult() {
		return new ArrayList<>();
	}

	/**
	 * Disable actual monitor.
	 */
	public abstract void disable();

}
