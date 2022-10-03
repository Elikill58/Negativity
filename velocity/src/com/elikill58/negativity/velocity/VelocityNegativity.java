package com.elikill58.negativity.velocity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;

@Plugin(id = "negativity")
public class VelocityNegativity {
	
	private static VelocityNegativity instance;
	public static VelocityNegativity getInstance() {
		return instance;
	}
	public static final LegacyChannelIdentifier NEGATIVITY_CHANNEL_ID = new LegacyChannelIdentifier(NegativityMessagesManager.CHANNEL_ID);
	
	private static final String MYSQL_DRIVER_URL = "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.25/mysql-connector-java-8.0.25.jar";
	private static final String MYSQL_DRIVER_FILE_NAME = "mysql-connector-java-8.0.25.jar";
	private static final String EXPECTED_MYSQL_DRIVER_HASH_STRING = "F8B9123ACD13058C941AFF25F308C9ED8000BB73";
	private static final byte[] EXPECTED_MYSQL_DRIVER_HASH = UniversalUtils.stringToHex(EXPECTED_MYSQL_DRIVER_HASH_STRING);
	
	private final ProxyServer server;
	private final Logger logger;
	private final PluginContainer container;
	private final PluginManager pluginManager;
	
	@Inject
	public VelocityNegativity(ProxyServer server, Logger logger, PluginContainer container, PluginManager pluginManager) {
		instance = this;
		this.server = server;
		this.logger = logger;
		this.container = container;
		this.pluginManager = pluginManager;
	}
	
	public ProxyServer getServer() {
		return server;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public PluginContainer getContainer() {
		return container;
	}
	
	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		getLogger().info("Loading Negativity");
		downloadAndInstallMysqlDriverIfNeeded();
		server.getEventManager().register(this, new VelocityListeners());
		server.getChannelRegistrar().register(NEGATIVITY_CHANNEL_ID);
		server.getCommandManager().register("vnegativity", new VNegativityCommand());
		
		Adapter.setAdapter(new VelocityAdapter(this));
		Negativity.loadNegativity();
		
		NegativityAccountStorage.setDefaultStorage("database");
		
		Stats.sendStartupStats(getServer().getBoundAddress().getPort());
		getLogger().info("Negativity enabled");
	}
	
	@Subscribe
	public void onProxyDisable(ProxyShutdownEvent e) {
		Negativity.closeNegativity();
	}
	
	public final File getDataFolder() {
		return new File("./plugins/Negativity");
	}
	
	private void downloadAndInstallMysqlDriverIfNeeded() {
		Path mysqlDriverPath = Paths.get("plugins", "Negativity", MYSQL_DRIVER_FILE_NAME).toAbsolutePath();
		
		if (Files.isDirectory(mysqlDriverPath)) {
			getLogger().error("Unexpected directory at {}. Can't download and use MYSQL driver.", mysqlDriverPath);
			return;
		}
		
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			getLogger().error("Could not find SHA-1 digest algorithm");
			return;
		}
		
		if (Files.exists(mysqlDriverPath)) {
			try (InputStream inputStream = Files.newInputStream(mysqlDriverPath);
				 DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)) {
				// Completely read file to compute its digest
				//noinspection StatementWithEmptyBody
				while (digestInputStream.read() != -1) ;
				byte[] digest = messageDigest.digest();
				if (MessageDigest.isEqual(EXPECTED_MYSQL_DRIVER_HASH, digest)) {
					getLogger().warn("Existing MYSQL driver matches expected checksum. Adding it to classpath.");
					this.pluginManager.addToClasspath(this, mysqlDriverPath);
					return;
				}
				
				getLogger().warn("Existing MYSQL driver does not match expected checksum. Downloading it again...");
			} catch (IOException e) {
				getLogger().error("Failed to read existing MYSQL driver at {}", mysqlDriverPath, e);
			}
		} else {
			getLogger().info("Missing MYSQL driver, downloading it...");
		}
		
		try {
			Files.createDirectories(mysqlDriverPath.getParent());
		} catch (IOException e) {
			getLogger().error("Failed to create Negativity configuration directory.", e);
			return;
		}
		
		try {
			URL url = new URL(MYSQL_DRIVER_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(true);
			connection.setRequestProperty("User-Agent", "Negativity Velocity - " + container.getDescription().getVersion());
			try (InputStream inputStream = connection.getInputStream();
				 DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)) {
				Files.copy(digestInputStream, mysqlDriverPath, StandardCopyOption.REPLACE_EXISTING);
				byte[] digest = messageDigest.digest();
				if (MessageDigest.isEqual(EXPECTED_MYSQL_DRIVER_HASH, digest)) {
					getLogger().info("Successfully downloaded MSQL driver. Adding it to the classpath.");
					this.pluginManager.addToClasspath(this, mysqlDriverPath);
				} else {
					String digestString = UniversalUtils.hexToString(digest);
					getLogger().error("MSQL driver SHA-1 checksum mismatch! Expected {} but got {}.", EXPECTED_MYSQL_DRIVER_HASH_STRING, digestString);
					getLogger().error("The file won't be added to the classpath. This means database access might produce errors");
				}
			}
		} catch (IOException e) {
			getLogger().error("Failed to download MYSQL driver", e);
		}
	}
}
