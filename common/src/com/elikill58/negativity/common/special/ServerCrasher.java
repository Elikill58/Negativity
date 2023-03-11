package com.elikill58.negativity.common.special;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventPriority;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketPreReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerCommandPreProcessEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInCustomPayload;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.detections.Special;
import com.elikill58.negativity.universal.detections.keys.SpecialKeys;
import com.elikill58.negativity.universal.logger.Debug;

public class ServerCrasher extends Special implements Listeners {

	public ServerCrasher() {
		super(SpecialKeys.SERVER_CRASHER, Materials.TNT);
	}

	@EventListener(priority = EventPriority.PRE)
	public void onPlayerCommand(PlayerCommandPreProcessEvent e) {
		if (!isActive())
			return;
		String cmd = e.getCommand().toLowerCase();
		Player p = e.getPlayer();
		if (!p.isOp() && (cmd.contains("pex demote") || cmd.contains("pex promote") || cmd.contains("for(") || cmd.contains("while("))) {
			tryingToCrash(NegativityPlayer.getNegativityPlayer(p), p, "Wrong command.");
			e.setCancelled(true);
		}
	}

	@EventListener(priority = EventPriority.PRE)
	public void onPacketReceive(PacketPreReceiveEvent e) {
		if (!isActive() || !e.hasPlayer())
			return;
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		NPacket packet = e.getPacket();
		if (packet.getPacketType().equals(PacketType.Client.POSITION)) {
			if (!np.isDisconnecting() && np.packets.getOrDefault(PacketType.Client.POSITION, 0) > 1000) {
				tryingToCrash(np, p, "Spam TP");
				e.setCancelled(true);
			}
		} else if (packet.getPacketType().equals(PacketType.Client.ARM_ANIMATION)) {
			if (!np.isDisconnecting() && np.packets.getOrDefault(PacketType.Client.ARM_ANIMATION, 0) > 1000) {
				tryingToCrash(np, p, "Spam Attack");
				e.setCancelled(true);
			}
		} else if (packet.getPacketType().equals(PacketType.Client.SET_CREATIVE_SLOT)) {
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				tryingToCrash(np, p, "Set creative slot");
				e.setCancelled(true);
			}
		} else if (packet.getPacketType().equals(PacketType.Client.CUSTOM_PAYLOAD)) {
			NPacketPlayInCustomPayload payload = (NPacketPlayInCustomPayload) packet;
			if (payload.channel.equalsIgnoreCase("MC|BSign")) {
				if (!p.getInventory().contains(Materials.BOOK_AND_QUILL)) {
					tryingToCrash(np, p, "Edit inexistant file");
					e.setCancelled(true);
				} else
					Adapter.getAdapter().debug(Debug.GENERAL, "Has book with BSign");
			} else if (payload.data.length > 32767) { // max as described here: https://wiki.vg/Protocol#Plugin_Message_2
				tryingToCrash(np, p, "Too large data in payload");
				e.setCancelled(true);
			} else {
				Adapter.getAdapter().debug(Debug.GENERAL, "Good length and not managed channel: " + payload.channel);
			}
		}
	}

	private void tryingToCrash(NegativityPlayer np, Player p, String name) {
		String reason = getName() + " - " + name;
		if (getConfig().getBoolean("ban.active", false)) {
			if (!BanManager.banActive) {
				Adapter.getAdapter().getLogger().warn("Cannot ban player " + p.getName() + " for " + reason + " because ban is NOT config.");
				Adapter.getAdapter().getLogger().warn("Please, enable ban in config and restart your server");
				if (getConfig().getBoolean("kick", true)) {
					np.setDisconnecting(true);
					p.kick(Messages.getMessage(p, "kick.kicked", "%name%", "Negativity", "%reason%", reason));
				}
			} else {
				np.setDisconnecting(true);
				BanManager.executeBan(Ban.active(p.getUniqueId(), reason, "Negativity", SanctionnerType.PLUGIN, System.currentTimeMillis() + getConfig().getLong("ban.time", 2629800000l),
						"server_crash", p.getIP()));
			}
		} else if (getConfig().getBoolean("kick", true)) {
			p.kick(Messages.getMessage(p, "kick.kicked", "%name%", "Negativity", "%reason%", reason));
			np.setDisconnecting(true);
		}
	}
}
