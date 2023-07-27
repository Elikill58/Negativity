package com.elikill58.negativity.spigot;

import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.spigot.listeners.*;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.database.Database;
import com.elikill58.negativity.universal.logger.Debug;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;
import com.elikill58.negativity.universal.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class SpigotNegativity extends JavaPlugin {

    private static final SubPlatform platform = SubPlatform.getSubPlatform();
    public static String CHANNEL_NAME_FML = "";
    private static SpigotNegativity INSTANCE;

    public static SubPlatform getSubPlatform() {
        return platform;
    }

    public static SpigotNegativity getInstance() {
        return INSTANCE;
    }

    public static void sendPluginMessage(byte[] rawMessage) {
        Player player = Utils.getFirstOnlinePlayer();
        if (player != null) {
            player.sendPluginMessage(getInstance(), NegativityMessagesManager.CHANNEL_ID, rawMessage);
        } else {
            getInstance().getLogger().severe("Could not send plugin message to proxy because there are no player online.");
        }
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        new File(getDataFolder().getAbsolutePath() + File.separator + "user" + File.separator + "proof").mkdirs();
        if (!new File(getDataFolder().getAbsolutePath(), "config.yml").exists()) {
            // show message before setting adapter (which create config file)
            getLogger().info("------ Negativity Information ------");
            getLogger().info("");
            getLogger().info(" > Thanks for downloading Negativity :)");
            getLogger().info("I'm trying to make the better anti-cheat has possible.");
            getLogger().info("If you get error/false positive, or just have suggestion, you can contact me via:");
            getLogger().info("@Elikill58 on discord and twitter or in all other web site like SpigotMC ...");
            getLogger().info("");
            getLogger().info("------ Negativity Information ------");
        }
        Adapter.setAdapter(new SpigotAdapter(this));

        SpigotVersionAdapter versionAdapter = SpigotVersionAdapter.getVersionAdapter();
        Version v = versionAdapter.getVersion();
        if (v.equals(Version.HIGHER) || v.equals(Version.LOWER))
            getLogger().warning("Unknow server version " + Utils.VERSION + " ! Some problems can appears.");
        else {
            getLogger().info("Detected server version: " + v.getName().toLowerCase(Locale.ROOT) + " (" + Utils.VERSION + ") using " + getSubPlatform().getName() + " server.");
        }

        Negativity.loadNegativity();

        new Metrics(this, 1743)
                .addCustomChart(new Metrics.SimplePie("custom_permission", () -> String.valueOf(Database.hasCustom)));

        loadEvents(Bukkit.getPluginManager(), v);

        Messenger messenger = getServer().getMessenger();
        ChannelListeners channelListeners = new ChannelListeners();
        //loadChannelInOut(messenger, NegativityMessagesManager.CHANNEL_ID, channelListeners);
        loadChannelInOut(messenger, CHANNEL_NAME_FML = v.isNewerOrEquals(Version.V1_13) ? "fml:hs" : "FML|HS", channelListeners);
        messenger.registerOutgoingPluginChannel(this, "BungeeCord");

        loadCommand();

        Stats.sendStartupStats(Bukkit.getServer().getPort());

        NegativityAccountStorage.setDefaultStorage("file");

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                double i = SpigotVersionAdapter.getVersionAdapter().getAverageTps() * 1.0E-6D;
                if (Negativity.tpsDrop && i < 50) { // if disabled and need to be enabled
                    Negativity.tpsDrop = false;
                } else if (!Negativity.tpsDrop && i > 50) { // if not disabled but need to be
                    Negativity.tpsDrop = true;
                    Adapter.getAdapter().debug(Debug.GENERAL, "Disabling detection because of TPS lagspike: " + i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1, 1);
    }

    private void loadEvents(PluginManager pm, Version v) {
        pm.registerEvents(new PlayersListeners(), this);
        pm.registerEvents(new InventoryListeners(), this);
        pm.registerEvents(new BlockListeners(), this);
        pm.registerEvents(new EntityListeners(), this);
        pm.registerEvents(new CommandsListeners(), this);
        pm.registerEvents(new PacketListeners(), this);
        if (v.isNewerOrEquals(Version.V1_9))
            pm.registerEvents(new ElytraListeners(), this);
    }

    private void loadChannelInOut(Messenger messenger, String channel, ChannelListeners event) {
        if (!messenger.getOutgoingChannels().contains(channel))
            messenger.registerOutgoingPluginChannel(this, channel);
        if (!messenger.getIncomingChannels().contains(channel))
            messenger.registerIncomingPluginChannel(this, channel, event);
    }

    private void loadCommand() {
        CommandsListeners command = new CommandsListeners();
        Configuration commandSection = Adapter.getAdapter().getConfig().getSection("commands");
        PluginCommand negativity = getCommand("negativity");
        negativity.setExecutor(command);
        negativity.setTabCompleter(command);

        PluginCommand reportCmd = getCommand("nreport");
        if (!commandSection.getBoolean("report", true))
            unRegisterBukkitCommand(reportCmd);
        else {
            reportCmd.setExecutor(command);
            reportCmd.setTabCompleter(command);
        }

        PluginCommand kickCmd = getCommand("nkick");
        if (!commandSection.getBoolean("kick", true))
            unRegisterBukkitCommand(kickCmd);
        else {
            kickCmd.setAliases(Collections.singletonList("kigk"));
            kickCmd.setExecutor(command);
            kickCmd.setTabCompleter(command);
        }

        PluginCommand langCmd = getCommand("nlang");
        if (!commandSection.getBoolean("lang", true))
            unRegisterBukkitCommand(langCmd);
        else {
            langCmd.setExecutor(command);
            langCmd.setTabCompleter(command);
        }

        PluginCommand modCmd = getCommand("nmod");
        if (!commandSection.getBoolean("mod", true))
            unRegisterBukkitCommand(modCmd);
        else
            modCmd.setExecutor(command);


        Configuration banConfig = BanManager.getBanConfig().getSection("commands");
        PluginCommand banCmd = getCommand("nban");
        if (!banConfig.getBoolean("ban", true))
            unRegisterBukkitCommand(banCmd);
        else {
            banCmd.setAliases(Collections.singletonList("ban"));
            banCmd.setExecutor(command);
            banCmd.setTabCompleter(command);
        }

        PluginCommand unbanCmd = getCommand("nunban");
        if (!banConfig.getBoolean("unban", true))
            unRegisterBukkitCommand(unbanCmd);
        else {
            unbanCmd.setAliases(Collections.singletonList("unban"));
            unbanCmd.setExecutor(command);
            unbanCmd.setTabCompleter(command);
        }


        Configuration warnConfig = BanManager.getBanConfig().getSection("commands");
        PluginCommand warnCmd = getCommand("nwarn");
        if (!warnConfig.getBoolean("warn", true))
            unRegisterBukkitCommand(banCmd);
        else {
            warnCmd.setAliases(Collections.singletonList("warn"));
            warnCmd.setExecutor(command);
            warnCmd.setTabCompleter(command);
        }


        PluginCommand clearCheatCmd = getCommand("nclearchat");
        if (!commandSection.getBoolean("chat.clear", true))
            unRegisterBukkitCommand(clearCheatCmd);
        else {
            clearCheatCmd.setAliases(Collections.singletonList("clearchat"));
            clearCheatCmd.setExecutor(command);
        }

        PluginCommand lockChatCmd = getCommand("nlockchat");
        if (!commandSection.getBoolean("chat.lock", true))
            unRegisterBukkitCommand(lockChatCmd);
        else {
            lockChatCmd.setAliases(Collections.singletonList("lockchat"));
            lockChatCmd.setExecutor(command);
        }
    }

    @Override
    public void onDisable() {
        Negativity.closeNegativity();
    }

    private Object getKnownCommands(Object object) {
        try {
            Field objectField = object.getClass().getDeclaredField("knownCommands");
            objectField.setAccessible(true);
            return objectField.get(object);
        } catch (NoSuchFieldException e) {
            Class<?> clazz = object.getClass();
            try {
                return clazz.getMethod("getKnownCommands").invoke(object);
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void unRegisterBukkitCommand(PluginCommand cmd) {
        try {
            Object result = ReflectionUtils.getPrivateField(this.getServer().getPluginManager(), "commandMap");
            HashMap<?, ?> knownCommands = (HashMap<?, ?>) getKnownCommands(result);
            knownCommands.remove(cmd.getName());
            for (String alias : cmd.getAliases())
                if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName()))
                    knownCommands.remove(alias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
