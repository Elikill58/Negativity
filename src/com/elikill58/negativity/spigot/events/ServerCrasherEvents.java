package com.elikill58.negativity.spigot.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.event.PacketReceiveEvent;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanType;

public class ServerCrasherEvents implements Listener {

	private FileConfiguration config;
	private ConfigurationSection section;
	private List<UUID> inDisconnection = new ArrayList<>();
	
	public ServerCrasherEvents(SpigotNegativity pl) {
		config = pl.getConfig();
		section = config.getConfigurationSection("cheats.special.server_crash");
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		inDisconnection.remove(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onPacketClear(PacketReceiveEvent e) {
		AbstractPacket packet = e.getPacket();
		if(packet.getPacketType() != PacketType.Client.POSITION)
			return;
		Player p = e.getPlayer();
		if(!inDisconnection.contains(p.getUniqueId())) {
			if(SpigotNegativityPlayer.getNegativityPlayer(p).PACKETS.getOrDefault(PacketType.Client.POSITION, 0) > 1000) {
				packet.setCancelled(tryingToCrash(p));
			}
		}
	}
	
	public String getName() {
		return section == null ? "Server Crasher" : section.getString("name", "Server Crasher");
	}
	
	private boolean tryingToCrash(Player p) {
		if(section.getBoolean("ban", false)) {
			if(!BanManager.banActive) {
				SpigotNegativity.getInstance().getLogger().warning("Cannot ban player " + p.getName() + " for " + getName() + " because ban is NOT config.");
				SpigotNegativity.getInstance().getLogger().warning("Please, enable ban in config and restart your server");
				if(section.getBoolean("kick", true)) {
					p.kickPlayer(Messages.getMessage(p, "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
					inDisconnection.add(p.getUniqueId());
				}
			} else {
				BanManager.executeBan(Ban.active(p.getUniqueId(), getName(), "Negativity", BanType.PLUGIN,
						System.currentTimeMillis() + section.getLong("ban_time", 2629800000l), "server_crash"));
				inDisconnection.add(p.getUniqueId());
			}
		} else if(section.getBoolean("kick", true)) {
			p.kickPlayer(Messages.getMessage(p, "kick.kicked", "%name%", "Negativity", "%reason%", section.getString("name", "Server Crasher")));
			inDisconnection.add(p.getUniqueId());
		}
		return inDisconnection.contains(p.getUniqueId());
	}
}
