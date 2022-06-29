package com.elikill58.negativity.universal.ban;

import java.io.File;
import java.nio.file.Path;
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
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;
import com.elikill58.negativity.universal.ban.processor.CommandBanProcessor;
import com.elikill58.negativity.universal.ban.processor.NegativityBanProcessor;
import com.elikill58.negativity.universal.ban.storage.DatabaseActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.DatabaseBanLogsStorage;
import com.elikill58.negativity.universal.ban.storage.FileActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.FileBanLogsStorage;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.webhooks.WebhookManager;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage.WebhookMessageType;

public class BanManager {

	public static boolean banActive, autoBan = false;
	private static Configuration banConfig;

	private static String processorId;
	private static final Map<String, BanProcessor> processors = new HashMap<>();
	private static final List<AltAccountBan> ALT_BAN_ACCOUNTS = new ArrayList<>();
	private static final List<Sanction> SANCTIONS = new ArrayList<>();

	public static List<Ban> getLoggedBans(UUID playerId) {
		BanProcessor processor = getProcessor();
		if (processor == null)
			return Collections.emptyList();

		return processor.getLoggedBans(playerId);
	}

	public static boolean isBanned(UUID playerId) {
		BanProcessor processor = getProcessor();
		if (processor == null)
			return false;

		return processor.isBanned(playerId);
	}

	@Nullable
	public static Ban getActiveBan(UUID playerId) {
		BanProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find ban processor while trying to get active ban from " + playerId);
			return null;
		}

		return processor.getActiveBan(playerId);
	}

	/**
	 * Executes the given ban. The executed ban may contain different information than the one you provided.
	 * Therefore, it is advised to use the returned {@link BanResult} data instead of what was passed to this method's parameters.
	 * <p>
	 * The ban may not be executed if bans are disabled, or for any {@link BanProcessor}-specific reason, like if the player bypassed the ban.
	 *
	 * @param ban the ban to execute
	 * @return the result of the ban with success informations
	 */
	public static BanResult executeBan(Ban ban) {
		BanProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find ban processor while trying to execute ban from " + ban.getPlayerId());
			return new BanResult(BanResultType.UNKNOW_PROCESSOR, null);
		}

		BanResult br = processor.executeBan(ban);
		if(br.isSuccess()) {
			WebhookManager.send(new WebhookMessage(WebhookMessageType.BAN, Adapter.getAdapter().getPlayer(ban.getPlayerId()), ban.getBannedBy(), ban.getExecutionTime(), "%reason%", ban.getReason()));
		}
		return br;
	}

	/**
	 * Revokes the active ban of the player identified by the given UUID.
	 * <p>
	 * The revocation may fail if the player is not banned or bans are disabled.
	 * <p>
	 * If ban logging is disabled, a LoggedBan will still be returned even though it will not be saved.
	 *
	 * @param playerId the UUID of the player to unban
	 *
	 * @return the logged revoked ban or {@code null} if the revocation failed.
	 */
	public static BanResult revokeBan(UUID playerId) {
		BanProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find ban processor while trying to revoke ban from " + playerId);
			return null;
		}

		return processor.revokeBan(playerId);
	}
	
	/**
	 * Get all active ban on the given IP
	 * <p>
	 * Return an empty array if ban feature is disabled
	 * 
	 * @param ip the IP where we are looking for ban
	 * @return the list of active ban on this IP
	 */
	public static List<Ban> getActiveBanOnSameIP(String ip){
		BanProcessor processor = getProcessor();
		if (processor == null) {
			Adapter.getAdapter().debug("Cannot find ban processor while trying to get active ban on IP " + ip);
			return null;
		}

		return processor.getActiveBanOnSameIP(ip);
	}

	public static AltAccountBan getAltBanFor(int nb) {
		AltAccountBan ban = null;
		for(AltAccountBan alt : ALT_BAN_ACCOUNTS) {
			if(alt.getAltNb() == nb) // check for exact NB of alt
				return alt;
			else if(alt.getAltNb() < nb) // check for nearest nb of alt, but lower than the given number
				ban = alt;
		}
		return ban;
	}
	
	/**
	 * Indicates whether Negativity should kick banned players
	 * 
	 * @return true if there is a processor and if it enable negativity's kick
	 */
	public static boolean shouldNegativityHandleBans() {
		BanProcessor processor = getProcessor();
		return processor != null && processor.isHandledByNegativity();
	}
	
	public static List<Sanction> getSanctions() {
		return SANCTIONS;
	}
	
	public static String getProcessorId() {
		return processorId;
	}
	
	public static void setProcessorId(String processorId) {
		BanManager.processorId = processorId;
		banConfig.set("processor", processorId);
		banConfig.save();
	}

	@Nullable
	public static BanProcessor getProcessor() {
		if (!banActive)
			return null;

		return processors.get(processorId);
	}
	
	public static String getProcessorName() {
		BanProcessor proc = getProcessor();
		return proc == null ? Messages.getMessage("none") : proc.getName();
	}
	
	public static List<String> getProcessorDescription() {
		BanProcessor proc = getProcessor();
		return proc == null ? new ArrayList<>() : proc.getDescription();
	}
	
	public static Map<String, BanProcessor> getProcessors() {
		return processors;
	}

	public static void registerProcessor(String id, BanProcessor processor) {
		processors.put(id, processor);
	}

	public static void init() {
		processors.clear();
		ALT_BAN_ACCOUNTS.clear();
		SANCTIONS.clear();
		
		Adapter adapter = Adapter.getAdapter();
		
		banConfig = UniversalUtils.loadConfig(new File(adapter.getDataFolder(), "bans.yml"), "bans.yml");
		
		banActive = banConfig.getBoolean("active");

		processorId = banConfig.getString("processor");
		
		autoBan = banConfig.getBoolean("auto", false);

		Path dataDir = adapter.getDataFolder().toPath();
		Path banDir = dataDir.resolve("bans");
		Path banLogsDir = banDir.resolve("logs");
		boolean fileLogBans = banConfig.getBoolean("file.log_bans");
		registerProcessor("file", new NegativityBanProcessor(new FileActiveBanStorage(banDir), fileLogBans ? new FileBanLogsStorage(banLogsDir) : null, "file"));

		if (Database.hasCustom) {
			boolean dbLogBans = banConfig.getBoolean("database.log_bans");
			registerProcessor("database", new NegativityBanProcessor(new DatabaseActiveBanStorage(), dbLogBans ? new DatabaseBanLogsStorage() : null, "database"));
		}

		List<String> banCommands = banConfig.getStringList("command.ban");
		List<String> unbanCommands = banConfig.getStringList("command.unban");
		registerProcessor("command", new CommandBanProcessor(banCommands, unbanCommands));

		Configuration altConfig = banConfig.getSection("alt");
		if(altConfig.getBoolean("active", false)) {
			altConfig.getKeys().stream().filter(UniversalUtils::isInteger).forEach((key) -> {
				ALT_BAN_ACCOUNTS.add(new AltAccountBan(Integer.parseInt(key), altConfig.getSection(key)));
			});
		}
		
		Configuration sanctionConfig = banConfig.getSection("sanctions");
		sanctionConfig.getKeys().forEach((key) -> SANCTIONS.add(new Sanction(key, sanctionConfig.getSection(key))));
		
		if(processorId != null && !processorId.equalsIgnoreCase("proxy") && banActive) // don't have ban to migrate because it's managed on proxy
			BansMigration.migrateBans(banDir, banLogsDir);
		
		Negativity.loadExtensions(BanProcessorProvider.class, provider -> {
			BanProcessor processor = provider.create(adapter);
			if (processor != null) {
				registerProcessor(provider.getId(), processor);
				return true;
			}
			return false;
		});
	}
	
	/**
	 * Change the state of ban<br>
	 * This doesn't save the config
	 * 
	 * @param b true if the ban feature should be enabled
	 */
	public static void setBanActive(boolean b) {
		BanManager.banActive = b;
		banConfig.set("active", b);
	}

	
	/**
	 * Change the state of auto ban<br>
	 * This doesn't save the config
	 * 
	 * @param b true if the auto ban feature should be enabled
	 */
	public static void setAutoBan(boolean b) {
		BanManager.autoBan = b;
		banConfig.set("auto", b);
	}
	
	public static Configuration getBanConfig() {
		return banConfig;
	}
	
	public static int getInt(Cheat where, String key) {
		return where.getConfig().getInt("ban." + key, getBanConfig().getInt(key));
	}
	
	public static String getString(Cheat where, String key) {
		return where.getConfig().getString("ban." + key, getBanConfig().getString(key));
	}
}
