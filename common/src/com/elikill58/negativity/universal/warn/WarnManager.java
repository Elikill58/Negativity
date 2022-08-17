package com.elikill58.negativity.universal.warn;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Sanction;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.warn.WarnResult.WarnResultType;
import com.elikill58.negativity.universal.warn.processor.WarnProcessor;
import com.elikill58.negativity.universal.warn.processor.WarnProcessorProvider;
import com.elikill58.negativity.universal.warn.processor.hook.CommandWarnProcessor;
import com.elikill58.negativity.universal.warn.processor.hook.NegativityDatabaseWarnProcessor;
import com.elikill58.negativity.universal.warn.processor.hook.NegativityFileWarnProcessor;
import com.elikill58.negativity.universal.webhooks.WebhookManager;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage.WebhookMessageType;

public class WarnManager {

	public static boolean warnActive;
	private static Configuration warnConfig;

	private static String processorId;
	private static final Map<String, WarnProcessor> processors = new HashMap<>();
	private static final List<Sanction> SANCTIONS = new ArrayList<>();

	public static boolean isWarned(UUID playerId) {
		WarnProcessor processor = getProcessor();
		if (processor == null)
			return false;

		return processor.isWarned(playerId);
	}

	public static List<Warn> getActiveWarn(UUID playerId) {
		WarnProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find warn processor while trying to get active warn from " + playerId);
			return null;
		}

		return processor.getActiveWarn(playerId);
	}

	/**
	 * Executes the given warn. The executed warn may contain different information than the one you provided.
	 * Therefore, it is advised to use the returned {@link WarnResult} data instead of what was passed to this method's parameters.
	 * <p>
	 * The warn may not be executed if warns are disabled, or for any {@link WarnProcessor}-specific reason, like if the player bypassed the warn.
	 *
	 * @param warn the warn to execute
	 * @return the result of the warn with success informations
	 */
	public static WarnResult executeWarn(Warn warn) {
		WarnProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find processor while trying to execute warn from " + warn.getPlayerId());
			return new WarnResult(WarnResultType.UNKNOW_PROCESSOR);
		}

		WarnResult br = processor.executeWarn(warn);
		if(br.isSuccess()) {
			WebhookManager.send(new WebhookMessage(WebhookMessageType.WARN, Adapter.getAdapter().getPlayer(warn.getPlayerId()), warn.getBannedBy(), warn.getExecutionTime(), "%reason%", warn.getReason()));
		}
		return br;
	}

	/**
	 * Revokes the active warns of the player identified by the given UUID.
	 * <p>
	 * The revocation may fail if the player is not warned or warns are disabled.
	 *
	 * @param playerId the UUID of the player to unwarn
	 *
	 * @return the logged revoked warn or {@code null} if the revocation failed.
	 */
	public static WarnResult revokeWarn(UUID playerId) {
		WarnProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find processor while trying to revoke warn from " + playerId);
			return new WarnResult(WarnResultType.UNKNOW_PROCESSOR);
		}

		return processor.revokeWarn(playerId);
	}
	
	/**
	 * Get all active warn on the given IP
	 * <p>
	 * Return an empty array if warn feature is disabled
	 * 
	 * @param ip the IP where we are looking for warn
	 * @return the list of active warn on this IP
	 */
	public static List<Warn> getActiveWarnOnSameIP(String ip){
		WarnProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find processor while trying to get active warn on IP " + ip);
			return Collections.emptyList();
		}

		return processor.getActiveWarnOnSameIP(ip);
	}
	
	public static List<Sanction> getSanctions() {
		return SANCTIONS;
	}
	
	public static String getProcessorId() {
		return processorId;
	}
	
	public static void setProcessorId(String processorId) {
		WarnProcessor oldProcessor = getProcessor();
		if(oldProcessor != null)
			oldProcessor.disable();
		
		WarnManager.processorId = processorId;
		warnConfig.set("processor", processorId);
		warnConfig.save();
		
		WarnProcessor processor = getProcessor();
		if(processor != null)
			processor.enable();
	}

	@Nullable
	public static WarnProcessor getProcessor() {
		if (!warnActive)
			return null;

		return processors.get(processorId);
	}
	
	public static String getProcessorName() {
		WarnProcessor proc = getProcessor();
		return proc == null ? Messages.getMessage("none") : proc.getName();
	}
	
	public static List<String> getProcessorDescription() {
		WarnProcessor proc = getProcessor();
		return proc == null ? new ArrayList<>() : proc.getDescription();
	}
	
	public static Map<String, WarnProcessor> getProcessors() {
		return processors;
	}

	public static void registerProcessor(String id, WarnProcessor processor) {
		processors.put(id, processor);
	}

	public static void init() {
		processors.clear();
		SANCTIONS.clear();
		
		Adapter adapter = Adapter.getAdapter();
		
		warnConfig = UniversalUtils.loadConfig(new File(adapter.getDataFolder(), "warns.yml"), "warns.yml");
		
		warnActive = warnConfig.getBoolean("active");

		processorId = warnConfig.getString("processor");
		
		registerProcessor("file", new NegativityFileWarnProcessor());

		if (Database.hasCustom) {
			registerProcessor("database", new NegativityDatabaseWarnProcessor());
		}

		List<String> warnCommands = warnConfig.getStringList("command.warn");
		List<String> unwarnCommands = warnConfig.getStringList("command.unwarn");
		registerProcessor("command", new CommandWarnProcessor(warnCommands, unwarnCommands));
		
		Configuration sanctionConfig = warnConfig.getSection("sanctions");
		sanctionConfig.getKeys().forEach((key) -> SANCTIONS.add(new Sanction(key, sanctionConfig.getSection(key))));
		
		Negativity.loadExtensions(WarnProcessorProvider.class, provider -> {
			WarnProcessor processor = provider.create(adapter);
			if (processor != null) {
				registerProcessor(provider.getId(), processor);
				return true;
			}
			return false;
		});
		
		WarnProcessor processor = getProcessor();
		if(processor != null)
			processor.enable();
	}
	
	/**
	 * Change the state of warn<br>
	 * This doesn't save the config
	 * 
	 * @param b true if the warn feature should be enabled
	 */
	public static void setWarnActive(boolean b) {
		WarnManager.warnActive = b;
		warnConfig.set("active", b);
	}
	
	public static Configuration getWarnConfig() {
		return warnConfig;
	}
	
	public static int getInt(Cheat where, String key) {
		return where.getConfig().getInt("warn." + key, getWarnConfig().getInt(key));
	}
	
	public static String getString(Cheat where, String key) {
		return where.getConfig().getString("warn." + key, getWarnConfig().getString(key));
	}
}
