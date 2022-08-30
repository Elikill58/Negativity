package com.elikill58.negativity.universal.warn.processor.hook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.api.yaml.YamlConfiguration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.warn.Warn;
import com.elikill58.negativity.universal.warn.WarnResult;
import com.elikill58.negativity.universal.warn.WarnResult.WarnResultType;
import com.elikill58.negativity.universal.warn.processor.WarnProcessor;

public class NegativityFileWarnProcessor implements WarnProcessor {

	private File folder;
	
	@Override
	public void enable() {
		this.folder = new File(Adapter.getAdapter().getDataFolder(), "warns");
	}
	
	private Configuration getYaml(UUID uuid) throws IOException {
		return YamlConfiguration.load(getOrCreateFile(uuid));
	}
	
	private File getOrCreateFile(UUID uuid) throws IOException {
		if(!folder.exists()) {
			folder.mkdirs();
		}
		File userFile = new File(folder, uuid.toString() + ".yml");
		if(!userFile.exists()) {
			userFile.createNewFile();
		}
		return userFile;
	}
	
	@Override
	public WarnResult executeWarn(Warn warn) {
		try {
			Configuration config = getYaml(warn.getPlayerId());
			int id = 0;
			while(config.contains(String.valueOf(id)))
				id++;
			set(config.createSection(String.valueOf(id)), warn);
			config.save();
			return new WarnResult(WarnResultType.DONE);
		} catch (Exception e) {
			e.printStackTrace();
			return new WarnResult(WarnResultType.EXCEPTION);
		}
	}

	@Override
	public WarnResult revokeWarn(UUID playerId, String revoker) {
		try {
			Configuration config = getYaml(playerId);
			config.getKeys().forEach(key -> {
				Configuration warnConfig = config.getSection(key);
				if(warnConfig.getBoolean("active", false)) {
					warnConfig.set("active", false);
					warnConfig.set("revocation_time", System.currentTimeMillis());
					warnConfig.set("revocation_by", revoker);
				}
			});
			config.directSave();
			return new WarnResult(WarnResultType.DONE);
		} catch (Exception e) {
			e.printStackTrace();
			return new WarnResult(WarnResultType.EXCEPTION);
		}
	}

	@Override
	public WarnResult revokeWarn(Warn warn, String revoker) {
		try {
			Configuration config = getYaml(warn.getPlayerId());
			Configuration warnConfig = config.getSection(String.valueOf(warn.getId()));
			if(warnConfig != null) {
				warnConfig.set("active", false);
				warnConfig.set("revocation_time", System.currentTimeMillis());
				warnConfig.set("revocation_by", revoker);
			}
			config.directSave();
			return new WarnResult(WarnResultType.DONE);
		} catch (Exception e) {
			e.printStackTrace();
			return new WarnResult(WarnResultType.EXCEPTION);
		}
	}

	@Override
	public List<Warn> getWarn(UUID playerId) {
		List<Warn> list = new ArrayList<>();
		try {
			Configuration config = getYaml(playerId);
			for(String key : config.getKeys()) {
				list.add(get(playerId, key, config.getSection(key)));
			}
			config.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Warn> getActiveWarnOnSameIP(String ip) {
		return Collections.emptyList();
	}
	
	private Warn get(UUID uuid, String key, Configuration config) {
		int id = Integer.parseInt(key);
		String reason = config.getString("reason");
		String banned_by = config.getString("warned_by");
		SanctionnerType sanctionner = SanctionnerType.valueOf(config.getString("sanctionner_type"));
		String ip = config.getString("ip");
		long executionTime = config.getLong("execution_time", -1);
		boolean active = config.getBoolean("active", true);
		long revocationTime = config.getLong("revocation_time", -1);
		String revocationBy = config.getString("revocation_by");
		return new Warn(id, uuid, reason, banned_by, sanctionner, ip, executionTime, active, revocationTime, revocationBy);
	}

	private void set(Configuration config, Warn warn) {
		config.set("reason", warn.getReason());
		config.set("warned_by", warn.getWarnedBy());
		config.set("sanctionner_type", warn.getSanctionnerType().name());
		config.set("ip", warn.getIp());
		config.set("execution_time", warn.getExecutionTime());
		config.set("active", warn.isActive());
		config.set("revocation_time", warn.getRevocationTime());
		config.set("revocation_by", warn.getRevocationBy());
	}
	
	@Override
	public String getName() {
		return "Negativity With file";
	}
	
	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Processor from Negativity by using files", "", ChatColor.RED + "Not available:", "&6Get all warns.");
	}
}
