package com.elikill58.negativity.velocity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.ProxyAdapter;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.account.SimpleAccountManager;
import com.elikill58.negativity.universal.logger.LoggerAdapter;
import com.elikill58.negativity.universal.translation.NegativityTranslationProviderFactory;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.velocity.impl.entity.VelocityPlayer;
import com.elikill58.negativity.velocity.impl.plugin.VelocityExternalPlugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class VelocityAdapter extends ProxyAdapter {

	private final NegativityAccountManager accountManager = new SimpleAccountManager.Proxy();
	private final TranslationProviderFactory translationProviderFactory;
	private final LoggerAdapter logger;
	private final VelocityNegativity pl;
	private final VelocityScheduler scheduler;
	private Configuration config;

	public VelocityAdapter(VelocityNegativity pl) {
		this.pl = pl;
		this.config = UniversalUtils.loadConfig(new File(pl.getDataFolder(), "config.yml"), "config_bungee.yml");
		this.translationProviderFactory = new NegativityTranslationProviderFactory(pl.getDataFolder().toPath().resolve("lang"), "NegativityProxy", "CheatHover");
		this.logger = new Slf4jLoggerAdapter(pl.getLogger());
		this.scheduler = new VelocityScheduler(pl);
	}

	@Override
	public Platform getPlatformID() {
		return Platform.VELOCITY;
	}

	@Override
	public Configuration getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder();
	}

	@Override
	public LoggerAdapter getLogger() {
		return logger;
	}

	@Override
	public TranslationProviderFactory getPlatformTranslationProviderFactory() {
		return this.translationProviderFactory;
	}

	@Override
	public void reload() {
		reloadConfig();
		Negativity.loadNegativity();
	}

	@Override
	public String getVersion() {
		return pl.getServer().getVersion().getVersion();
	}

	@Override
	public String getPluginVersion() {
		return pl.getContainer().getDescription().getVersion().orElse("unknown");
	}

	@Override
	public void reloadConfig() {
		config = UniversalUtils.loadConfig(new File(pl.getDataFolder(), "config.yml"), "config_bungee.yml");
	}

	@Override
	public NegativityAccountManager getAccountManager() {
		return accountManager;
	}

	@Override
	public void runConsoleCommand(String cmd) {
		pl.getServer().getCommandManager().executeAsync(pl.getServer().getConsoleCommandSource(), cmd).join();
	}

	@Override
	public List<UUID> getOnlinePlayersUUID() {
		List<UUID> list = new ArrayList<>();
		pl.getServer().getAllPlayers().forEach((p) -> list.add(p.getUniqueId()));
		return list;
	}

	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		pl.getServer().getAllPlayers().forEach((p) -> list.add(NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new VelocityPlayer(p)).getPlayer()));
		return list;
	}

	@Override
	public @Nullable Player getPlayer(String name) {
		return pl.getServer().getPlayer(name).map(player -> NegativityPlayer.getNegativityPlayer(player.getUniqueId(), () -> new VelocityPlayer(player)).getPlayer()).orElse(null);
	}

	@Override
	public @Nullable Player getPlayer(UUID uuid) {
		return pl.getServer().getPlayer(uuid).map(player -> NegativityPlayer.getNegativityPlayer(uuid, () -> new VelocityPlayer(player)).getPlayer()).orElse(null);
	}

	@Override
	public boolean hasPlugin(String name) {
		return pl.getServer().getPluginManager().isLoaded(name.toLowerCase(Locale.ROOT));
	}

	@Override
	public ExternalPlugin getPlugin(String name) {
		return new VelocityExternalPlugin(pl.getServer().getPluginManager().getPlugin(name.toLowerCase(Locale.ROOT)).orElse(null));
	}

	@Override
	public List<ExternalPlugin> getDependentPlugins() {
		return pl.getServer().getPluginManager().getPlugins().stream().filter(plugin -> plugin.getDescription().getDependency("negativity").isPresent()).map(VelocityExternalPlugin::new)
				.collect(Collectors.toList());
	}

	@Override
	public void runSync(Runnable call) {
		pl.getServer().getScheduler().buildTask(pl, call).schedule();
	}

	@Override
	public void registerNewIncomingChannel(String channel, BiConsumer<Player, byte[]> event) {
		VelocityListeners.channelListeners.put(channel, event);
	}

	@Override
	public void broadcastMessage(String message) {
		TextComponent text = Component.text(message);
		pl.getServer().getAllPlayers().forEach((p) -> p.sendMessage(text));
	}

	@Override
	public List<String> getAllPlugins() {
		return VelocityNegativity.getInstance().getServer().getPluginManager().getPlugins().stream().map(PluginContainer::getDescription).map(PluginDescription::getId)
				.collect(Collectors.toList());
	}

	@Override
	public void sendMessageRunnableHover(Player p, String message, String hover, String command) {
		com.velocitypowered.api.proxy.Player vp = (com.velocitypowered.api.proxy.Player) p.getDefault();
		vp.sendMessage(Component.text(message).clickEvent(ClickEvent.runCommand(command)).hoverEvent(HoverEvent.showText(Component.text(hover))));
	}

	@Override
	public Scheduler getScheduler() {
		return scheduler;
	}
}
