package com.elikill58.negativity.velocity;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.channel.ProxyChannelNegativityMessageEvent;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerCommandPreProcessEvent;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.velocity.impl.entity.VelocityPlayer;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.ModInfo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class VelocityListeners {
	
	public static HashMap<String, BiConsumer<com.elikill58.negativity.api.entity.Player, byte[]>> channelListeners = new HashMap<>();
	
	@Subscribe
	public void onMessageReceived(PluginMessageEvent event) {
		if (!event.getIdentifier().equals(VelocityNegativity.NEGATIVITY_CHANNEL_ID)) {
			BiConsumer<com.elikill58.negativity.api.entity.Player, byte[]> cons = channelListeners.get(event.getIdentifier().getId());
			Player p = (Player) (event.getSource() instanceof Player ? event.getSource() : (event.getTarget() instanceof Player ? event.getTarget() : null));
			if(cons != null && p != null) {
				cons.accept(NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new VelocityPlayer(p)).getPlayer(), event.getData());
			}
			return;
		}
		
		event.setResult(PluginMessageEvent.ForwardResult.handled());
		
		Player p = (Player) (event.getSource() instanceof Player ? event.getSource() : (event.getTarget() instanceof Player ? event.getTarget() : null));
		if (p == null) {
			Adapter.getAdapter().getLogger().error("Source and Target not proxied (Source: " + event.getSource() + " Target: " + event.getTarget() + ")");
			return;
		}
		EventManager.callEvent(new ProxyChannelNegativityMessageEvent(NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new VelocityPlayer(p)).getPlayer(), event.getData()));
	}
	
	@Subscribe
	public void onLogin(com.velocitypowered.api.event.connection.LoginEvent e) {
		Player p = e.getPlayer();
		LoginEvent event = new LoginEvent(p.getUniqueId(), p.getUsername(), e.getResult().isAllowed() ? Result.ALLOWED : Result.KICK_BANNED, p.getRemoteAddress().getAddress(), "");
		EventManager.callEvent(event);
		if (!event.getLoginResult().equals(Result.ALLOWED))
			e.setResult(ResultedEvent.ComponentResult.denied(Component.text(event.getKickMessage())));
	}
	
	@Subscribe
	public void onPostLogin(PostLoginEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new VelocityPlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}
	
	@Subscribe
	public void onPlayerQuit(DisconnectEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new VelocityPlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}
	
	@Subscribe
	public void onCommandPreProcess(CommandExecuteEvent e) {
		if(!(e.getCommandSource() instanceof Player))
			return;
		Player p = (Player) e.getCommandSource();
		String message = e.getCommand().substring(1);
		String cmd = message.split(" ")[0];
		String[] arg = message.replace(cmd + " ", "").split(" ");
		String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase(Locale.ROOT);
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new VelocityPlayer(p));
		PlayerCommandPreProcessEvent event = new PlayerCommandPreProcessEvent(np.getPlayer(), cmd, arg, prefix, true);
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setResult(CommandResult.denied());
	}
	
	@Subscribe
	public void onServerChange(ServerConnectedEvent event) {
		List<ModInfo.Mod> modsList = event.getPlayer().getModInfo()
			.map(ModInfo::getMods)
			.orElseGet(Collections::emptyList);
		if (modsList.isEmpty()) {
			return;
		}
		
		Map<String, String> mods = new HashMap<>();
		for (ModInfo.Mod mod : modsList) {
			mods.put(mod.getId(), mod.getVersion());
		}
		
		try {
			byte[] rawMessage = NegativityMessagesManager.writeMessage(new ClientModsListMessage(mods));
			event.getServer().sendPluginMessage(VelocityNegativity.NEGATIVITY_CHANNEL_ID, rawMessage);
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not write ClientModsListMessage: " + e.getMessage());
		}
	}
	
	public static class Report {
		
		private final Object[] place;
		private final String cmd;
		
		public Report(String cmd, Object... parts) {
			place = new Object[]{"%name%", parts[0], "%cheat%", parts[1], "%reliability%", parts[2], "%ping%", parts[3]};
			this.cmd = cmd;
		}
		
		public TextComponent toMessage(Player p) {
			TextComponent.Builder msg = Component.text().content(Messages.getMessage(p.getUniqueId(), "alert", place));
			String hover = Messages.getMessage(p.getUniqueId(), "alert_hover", place);
			if (hover.contains("\\n")) {
				TextComponent.Builder hoverMessage = Component.text().color(NamedTextColor.GOLD);
				for (String s : hover.split("\\n")) {
					hoverMessage.append(Component.text(s));
				}
				msg.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMessage.build()));
			} else {
				msg.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(hover)));
			}
			return msg.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, cmd)).build();
		}
	}
}
