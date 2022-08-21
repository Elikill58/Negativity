package com.elikill58.negativity.universal.monitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.universal.detections.keys.IDetectionKey;

public abstract class MonitorManager {
	
	public static void load() {
		MonitorType.getMonitors().forEach(m -> {
			m.getMonitor().disable();
			m.getMonitor().enable();
		});
	}
	
	private final String name;
	protected boolean enabled = false;
	
	public MonitorManager(String name) {
		this.name = name;
	}
	
	/**
	 * Enable actual monitor
	 */
	public void enable() {
		enabled = true;
	}

	/**
	 * Disable actual monitor.
	 */
	public void disable() {
		enabled = false;
	}

	/**
	 * Check if the actual monitor is enabled
	 * 
	 * @return true if enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Get the name of what the monitor is checking
	 * 
	 * @return the name
	 */
	public @NonNull String getName() {
		return name;
	}

	/**
	 * Get description that will be showed at TOP of results
	 * 
	 * @return the description
	 */
	public @NonNull String getDescription() {
		return ChatColor.YELLOW + "Result of monitor for " + getName() + ":";
	}
	
	/**
	 * Show result from current monitor
	 * 
	 * @param sender who should see result
	 */
	public void showResult(CommandSender sender) {
		sender.sendMessage(getDescription());
		getHeaderResult().forEach(sender::sendMessage);
		getResult().forEach(sender::sendMessage);
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
		getResultPerCheat().forEach((key, lines) -> {
			if(lines.isEmpty())
				return; // ignore cheat without any informations
			String cheatName = key.getName();
			if(lines.size() == 1) {
				sender.sendMessage(ChatColor.GREEN + " " + cheatName + ChatColor.GRAY + ": " + ChatColor.YELLOW + lines.get(0));
			} else {
				sender.sendMessage(ChatColor.GREEN + " " + cheatName + ChatColor.GRAY + ": ");
				lines.forEach(line -> sender.sendMessage(ChatColor.YELLOW  + "  " + line));
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
		return Collections.emptyList();
	}
	
	/**
	 * Get parsed and showable result
	 * 
	 * @return all results lines
	 */
	public abstract @NonNull List<String> getResult();

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
	public abstract HashMap<IDetectionKey<?>, List<String>> getResultPerCheat();
	
	/**
	 * Get footer of result. Used for raw & cleaned result.<br>
	 * Should include conclusion and things like that.
	 * 
	 * @return the footer, or empty list
	 */
	public @NonNull List<String> getFooterResult() {
		return Collections.emptyList();
	}
	
	public abstract List<MonitorMeasure> getFullConfig();
}