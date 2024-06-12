package com.elikill58.negativity.minestom;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.minestom.listeners.BlockListeners;
import com.elikill58.negativity.minestom.listeners.EntityListeners;
import com.elikill58.negativity.minestom.listeners.InventoryListeners;
import com.elikill58.negativity.minestom.listeners.PacketListeners;
import com.elikill58.negativity.minestom.listeners.PlayersListeners;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;
import com.elikill58.negativity.universal.warn.WarnManager;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.extensions.Extension;
import net.minestom.server.extensions.ExtensionClassLoader;

public class MinestomNegativity extends Extension {

	public static final List<String> ALL_COMMANDS = new ArrayList<>();
	public static MinestomNegativity INSTANCE;

	@Override
	public void initialize() {
		INSTANCE = this;

		loadNetty();
		new File(getDataDirectory().toFile(), "user" + File.separator + "proof").mkdirs();

		Adapter.setAdapter(new MinestomAdapter(this, getLogger()));

		Negativity.loadNegativity();

		NegativityAccountStorage.setDefaultStorage("file");

		new BlockListeners(getEventNode());
		new PlayersListeners(getEventNode());
		new EntityListeners(getEventNode());
		new InventoryListeners(getEventNode());
		new PacketListeners(getEventNode());

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
		if (configKey != null) {
			if (configKey.endsWith("ban"))
				conf = BanManager.getBanConfig();
			if (configKey.endsWith("warn"))
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

	public static List<Player> getOnlinePlayers() {
		return new ArrayList<>(MinecraftServer.getConnectionManager().getOnlinePlayers());
	}

	private void loadNetty() {
		CompletableFuture.runAsync(() -> {
			File netty = new File(getDataDirectory().toFile(), "netty");
			if (netty.exists() && netty.isDirectory()) {
				getLogger().info("[Negativity] Loading netty dependancy ...");
				int loaded = 0, failed = 0;
				for (File jarFile : netty.listFiles()) {
					if (jarFile.isFile() && jarFile.getName().endsWith(".jar")) {
						if (loadJar(jarFile))
							loaded++;
						else
							failed++;
					}
				}
				getLogger().info("[Negativity] " + loaded + " jars loaded and " + failed + " failed.");
				try {
					for (String names : Arrays.asList("io.netty.buffer.Unpooled", "io.netty.buffer.ByteBuf",
							"io.netty.handler.codec.DecoderException"))
						Class.forName(names);
					getLogger().info("[Negativity] Netty well loaded.");
				} catch (ClassNotFoundException e) {
					getLogger().error("[Negativity] Failed to load netty. " + e.getMessage());
				}
			} else {
				netty.mkdirs();
				getLogger().info("[Negativity] Netty not found. Downloading required files ...");
				int loaded = 0, failed = 0;
				String nettyVersion = "4.1.85.Final";
				for (String requiredJarNames : Arrays.asList("netty-all", "netty-buffer", "netty-codec", "netty-common",
						"netty-handler")) {
					String fileName = requiredJarNames + "-" + nettyVersion + ".jar";
					String url = "https://repo1.maven.org/maven2/io/netty/" + requiredJarNames + "/" + nettyVersion
							+ "/" + fileName;
					try {
						try (InputStream in = new URI(url).toURL().openStream()) {
							File jarFile = new File(netty, fileName);
							Files.copy(in, jarFile.toPath());
							getLogger().info("[Negativity] Jar " + fileName + " downloaded.");
							if (loadJar(jarFile))
								loaded++;
							else
								failed++;
						}
					} catch (Exception e) {
						getLogger().error("[Negativity] Can't download file for jar " + requiredJarNames + ". Reason: "
								+ e.getMessage());
						e.printStackTrace();
					}
				}
				getLogger().info("[Negativity] " + loaded + " jars loaded and " + failed + " failed.");
			}
		});
	}

	private boolean loadJar(File file) {
		try {
			URL url = file.toURI().toURL();

			ClassLoader classLoader = getClass().getClassLoader();
			if (classLoader instanceof ExtensionClassLoader ecl)
				ecl.addURL(url);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
