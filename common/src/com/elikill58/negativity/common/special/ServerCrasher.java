package com.elikill58.negativity.common.special;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Special;
import com.elikill58.negativity.universal.SpecialKeys;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanType;

public class ServerCrasher extends Special implements Listeners {
	
	private List<UUID> inDisconnection = new ArrayList<>();
	
	public ServerCrasher() {
		super(SpecialKeys.SERVER_CRASHER, true);
	}
	
	@EventListener
	public void onLeave(PlayerLeaveEvent e) {
		inDisconnection.remove(e.getPlayer().getUniqueId());
	}
	
	@EventListener
	public void onPacketClear(PacketReceiveEvent e) {
		if(!isActive())
			return;
		Player p = e.getPlayer();
		/*try {
			Adapter.getAdapter().getLogger().info("> " + e.getPacket().getPacketType() + " : " + e.getPacket().getPacketName());
			//if(!e.getPacket().getPacketType().name().contains("CHAT") && !e.getPacket().getPacketType().isFlyingPacket()) // prevent infinite loop
				//p.sendMessage("> " + e.getPacket().getPacketType() + " ! " + e.getPacket().getPacketName());
		} catch (Exception exc) {
			exc.printStackTrace();
		}*/
		if(e.getPacket().getPacketType() == PacketType.Client.POSITION && !inDisconnection.contains(p.getUniqueId())) {
			if(NegativityPlayer.getNegativityPlayer(p).PACKETS.getOrDefault(PacketType.Client.POSITION, 0) > 1000) {
				e.getPacket().setCancelled(tryingToCrash(e.getPlayer()));
			}
		}
	}
	
	private boolean tryingToCrash(Player p) {
		if(getConfig().getBoolean("ban.active", false)) {
			if(!BanManager.banActive) {
				Adapter.getAdapter().getLogger().warn("Cannot ban player " + p.getName() + " for " + getName() + " because ban is NOT config.");
				Adapter.getAdapter().getLogger().warn("Please, enable ban in config and restart your server");
				if(getConfig().getBoolean("kick", true)) {
					p.kick(Messages.getMessage(p, "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
					inDisconnection.add(p.getUniqueId());
				}
			} else {
				BanManager.executeBan(Ban.active(p.getUniqueId(), getName(), "Negativity", BanType.PLUGIN,
						System.currentTimeMillis() + getConfig().getLong("ban.time", 2629800000l), "server_crash", p.getIP()));
				inDisconnection.add(p.getUniqueId());
			}
		} else if(getConfig().getBoolean("kick", true)) {
			p.kick(Messages.getMessage(p, "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
			inDisconnection.add(p.getUniqueId());
		}
		return inDisconnection.contains(p.getUniqueId());
	}
}
