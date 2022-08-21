package com.elikill58.negativity.minestom;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.minestom.listeners.BlockListeners;
import com.elikill58.negativity.minestom.listeners.EntityListeners;
import com.elikill58.negativity.minestom.listeners.InventoryListeners;
import com.elikill58.negativity.minestom.listeners.PlayersListeners;
import com.elikill58.negativity.minestom.packets.NegativityPacketManager;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;
import com.elikill58.negativity.universal.warn.WarnManager;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.extensions.Extension;

public class MinestomNegativity extends Extension {

	public static final List<String> ALL_COMMANDS = new ArrayList<>();
	public static MinestomNegativity INSTANCE;

	private NegativityPacketManager packetManager;

	@Override
	public void initialize() {
		INSTANCE = this;

		new File(getDataDirectory().toFile().getAbsolutePath() + File.separator + "user" + File.separator + "proof").mkdirs();

		Adapter.setAdapter(new MinestomAdapter(this, getLogger()));

		Negativity.loadNegativity();

		NegativityAccountStorage.setDefaultStorage("file");

		new BlockListeners(getEventNode());
		new PlayersListeners(getEventNode());
		new EntityListeners(getEventNode());
		new InventoryListeners(getEventNode());
		new NegativityPacketManager(getEventNode());
		
		loadCommands();
		getLogger().info("Negativity v" + getOrigin().getVersion() + " loaded.");
	}
	
	@Override
	public void terminate() {
		Negativity.closeNegativity();
	}
	
	private void loadCommands() {
		registerCommand(null, "negativity", "neg", "n");
		registerCommand("nmod", "nmod", "mod");
		registerCommand("kick", "nkick", "kick");
		registerCommand("lang", "nlang", "lang");
		registerCommand("report", "nreport", "report", "repot");
		registerCommand("ban", "nban", "negban", "ban");
		registerCommand("unban", "nunban", "negunban", "unban");
		registerCommand("chat.clear", "nclearchat", "clearchat");
		registerCommand("chat.lock", "nlockchat", "lockchat");
		registerCommand("warn", "nwarn", "warn");
	}

	private void registerCommand(String configKey, String cmd, String... alias) {
		Configuration conf = Adapter.getAdapter().getConfig();
		if(configKey != null) {
			if(configKey.endsWith("ban"))
				conf = BanManager.getBanConfig();
			if(configKey.endsWith("warn"))
				conf = WarnManager.getWarnConfig();
		}
		if (configKey == null || conf.getBoolean("commands." + configKey)) {
			ALL_COMMANDS.add(cmd);
			ALL_COMMANDS.addAll(Arrays.asList(alias));
			MinecraftServer.getCommandManager().register(new MinestomCommand(cmd, alias));
		}
	}

	public static MinestomNegativity getInstance() {
		return INSTANCE;
	}

	public NegativityPacketManager getPacketManager() {
		return packetManager;
	}

	public static List<Player> getOnlinePlayers() {
		return new ArrayList<>(MinecraftServer.getConnectionManager().getOnlinePlayers());
	}
}
