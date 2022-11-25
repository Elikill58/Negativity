package com.elikill58.negativity.common.server;

import java.util.Locale;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PrePacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerChatEvent;
import com.elikill58.negativity.api.events.player.PlayerCommandPreProcessEvent;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.events.player.PlayerToggleActionEvent;
import com.elikill58.negativity.api.events.player.PlayerToggleActionEvent.ToggleAction;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction.EnumPlayerAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;

public class NegativityPacketListener implements Listeners {

	@EventListener
	public void onPrePacketReceive(PrePacketReceiveEvent e) {
		if (!e.hasPlayer() || e.getPacket().getPacketType() == null)
			return;
		Player p = e.getPlayer();
		NPacket packet = e.getPacket();
		if (packet instanceof NPacketPlayInChat) {
			NPacketPlayInChat chat = (NPacketPlayInChat) packet;
			if (chat.message.startsWith("/")) {
				String cmd = chat.message.substring(1).split(" ")[0];
				String cmdArg = chat.message.substring(cmd.length() + 1); // +1 for the '/'
				if (cmdArg.startsWith(" "))
					cmdArg = cmdArg.substring(1);
				String[] arg = cmdArg.replace(" ", "").isEmpty() ? new String[0] : cmdArg.split(" ");
				String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase(Locale.ROOT);
				PlayerCommandPreProcessEvent preProcess = new PlayerCommandPreProcessEvent(p, cmd, arg, prefix, false);
				EventManager.callEvent(preProcess);
				if (preProcess.isCancelled())
					e.setCancelled(true);
			} else {
				PlayerChatEvent chatEvent = new PlayerChatEvent(p, chat.message);// default MC format
				EventManager.callEvent(chatEvent);
				e.setCancelled(chatEvent.isCancelled());
			}
		}
	}

	@EventListener
	public void onPacketReceive(PacketReceiveEvent e) {
		if (!e.hasPlayer() || e.getPacket().getPacketType() == null)
			return;
		Player p = e.getPlayer();
		NPacket packet = e.getPacket();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		PacketType type = packet.getPacketType();
		if (type == PacketType.Client.BLOCK_DIG && packet instanceof NPacketPlayInBlockDig) {
			NPacketPlayInBlockDig blockDig = (NPacketPlayInBlockDig) packet;
			if (blockDig.action != DigAction.FINISHED_DIGGING)
				return;

			Block b = blockDig.getBlock(p.getWorld());
			BlockBreakEvent event = new BlockBreakEvent(p, b);
			EventManager.callEvent(event);
			// TODO check for cancelling block break
			/*
			 * if(event.isCancelled()) e.setCancelled(event.isCancelled());
			 */
		} else if (type == PacketType.Client.USE_ENTITY) {
			np.isAttacking = true;
			NPacketPlayInUseEntity useEntityPacket = (NPacketPlayInUseEntity) packet;
			if (useEntityPacket.action.equals(EnumEntityUseAction.ATTACK)) {
				for (Entity entity : p.getWorld().getEntities()) {
					if (entity.isSameId(String.valueOf(useEntityPacket.entityId))) {
						PlayerDamageEntityEvent event = new PlayerDamageEntityEvent(p, entity, false);
						EventManager.callEvent(event);
						// TODO check for cancelling entity damage
						/*
						 * if(event.isCancelled()) e.setCancelled(event.isCancelled());
						 */
					}
				}
			}
		} else if (type.isFlyingPacket()) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) packet;
			if (flying.hasLocation()) {
				PlayerMoveEvent moveEvent = new PlayerMoveEvent(p, p.getLocation(), flying.getLocation(p.getWorld()));
				EventManager.callEvent(moveEvent);
				p.teleport(moveEvent.getFrom()); // shitty way to cancel action already done by server
			}
		} else if (packet instanceof NPacketPlayInEntityAction) {
			NPacketPlayInEntityAction action = (NPacketPlayInEntityAction) packet;
			ToggleAction toggle = null;
			if(action.action == EnumPlayerAction.START_SNEAKING)
				toggle = ToggleAction.SNEAK;
			else if(action.action == EnumPlayerAction.START_SPRINTING)
				toggle = ToggleAction.SPRINT;
			
			if(toggle != null)
				EventManager.callEvent(new PlayerToggleActionEvent(p, toggle, false));
		}
	}
	
	/**
	 * 
	
	public void onStartFly(PlayerStartFlyingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.FLY, false));
	}
	
	public void onStopFly(PlayerStopFlyingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.FLY, false));
	}
	
	public void onStartSneak(PlayerStartSneakingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.SNEAK, false));
	}
	
	public void onStopSneak(PlayerStopSneakingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.SNEAK, false));
	}
	
	public void onStartSprint(PlayerStartSprintingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.SPRINT, false));
	}
	
	public void onStopSprint(PlayerStopSprintingEvent e) {
		EventManager.callEvent(new PlayerToggleActionEvent(MinestomEntityManager.getPlayer(e.getPlayer()), ToggleAction.SPRINT, false));
	}
	 */
}
