package com.elikill58.negativity.fabric.impl.packet;

import java.util.Locale;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.api.events.player.PlayerCommandPreProcessEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketManager;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;

public abstract class FabricPacketManager extends PacketManager {

	public void notifyHandlersReceive(PacketSourceType source, AbstractPacket packet) {
		Player p = packet.getPlayer();
		PacketReceiveEvent event = new PacketReceiveEvent(source, packet, p);
		if (packet.getPacket() instanceof NPacketPlayInChat) {
			NPacketPlayInChat chat = (NPacketPlayInChat) packet.getPacket();
			if (chat.message.startsWith("/")) {
				String cmd = chat.message.substring(1).split(" ")[0];
				String cmdArg = chat.message.substring(cmd.length() + 1); //+1 for the '/'
				if(cmdArg.startsWith(" "))
					cmdArg = cmdArg.substring(1);
				String[] arg = cmdArg.isBlank() ? new String[0] : cmdArg.split(" ");
				String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase(Locale.ROOT);
				PlayerCommandPreProcessEvent preProcess = new PlayerCommandPreProcessEvent(p, cmd, arg,
						prefix, false);
				EventManager.callEvent(preProcess);
				if (preProcess.isCancelled())
					event.setCancelled(true);
			} else {
				PlayerChatEvent chatEvent = new PlayerChatEvent(p, chat.message, "<%1$s> %2$s");// default MC format
				EventManager.callEvent(chatEvent);
				event.setCancelled(chatEvent.isCancelled());
			}
		} else if(packet.getPacket() instanceof NPacketPlayInFlying) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) packet.getPacket();
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
			Location to = flying.getLocation(p.getWorld());
			PlayerMoveEvent move = new PlayerMoveEvent(p, p.getLocation(), to == null ? p.getLocation() : to);
			EventManager.callEvent(move);
			if (move.hasToSet()) {
				// TODO manage when changed
			}
			if (move.isCancelled()) {
				event.setCancelled(true);
				return;
			}

			if (np.isFreeze && !p.getLocation().clone().sub(0, 1, 0).getBlock().getType().equals(Materials.AIR)) {
				event.setCancelled(true);
				return;
			}
		}
		EventManager.callEvent(event);
	}

	public void notifyHandlersSent(PacketSourceType source, AbstractPacket packet) {
		PacketSendEvent event = new PacketSendEvent(source, packet, packet.getPlayer());
		EventManager.callEvent(event);
	}
}
