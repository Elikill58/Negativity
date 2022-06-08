package com.elikill58.negativity.bungee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.channel.ProxyChannelNegativityMessageEvent;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerCommandPreProcessEvent;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.bungee.impl.entity.BungeePlayer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.pluginMessages.ClientModsListMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.event.EventHandler;

@SuppressWarnings("deprecation")
public class BungeeListeners implements Listener {

	public static List<Report> report = new ArrayList<>();
	public static HashMap<String, BiConsumer<Player, byte[]>> channelListeners = new HashMap<>();

	@EventHandler
	public void onReceiveMessage(PluginMessageEvent e) {
		BiConsumer<Player, byte[]> cons = channelListeners.get(e.getTag());
		ProxiedPlayer p = (ProxiedPlayer) (e.getSender() instanceof ProxiedPlayer ? e.getSender()
				: (e.getReceiver() instanceof ProxiedPlayer ? e.getReceiver() : null));
		if (cons != null && p != null) {
			cons.accept(NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new BungeePlayer(p)).getPlayer(),
					e.getData());
		}
	}

	@EventHandler
	public void onNegativityPluginMessageReceived(PluginMessageEvent event) {
		if (!event.getTag().equalsIgnoreCase(NegativityMessagesManager.CHANNEL_ID)) {
			return;
		}

		event.setCancelled(true);
		
		ProxiedPlayer player = (ProxiedPlayer) (event.getSender() instanceof ProxiedPlayer ? event.getSender()
				: (event.getReceiver() instanceof ProxiedPlayer ? event.getReceiver() : null));
		if (player == null) {
			Adapter.getAdapter().getLogger().warn("Error while receiving a plugin message. Player null (Sender: " + event.getSender()
					+ " Receiver: " + event.getReceiver() + ")");
			return;
		}
		EventManager.callEvent(new ProxyChannelNegativityMessageEvent(NegativityPlayer.getNegativityPlayer(player.getUniqueId(),
					() -> new BungeePlayer(player)).getPlayer(), event.getData(), true));
	}

	@EventHandler
	public void onPreLogin(net.md_5.bungee.api.event.LoginEvent e) {
		PendingConnection co = e.getConnection();
		LoginEvent event = new LoginEvent(co.getUniqueId(), co.getName(),
				e.isCancelled() ? Result.KICK_BANNED : Result.ALLOWED, co.getAddress().getAddress(), getReason(e));
		EventManager.callEvent(event);
		if (!event.getLoginResult().equals(Result.ALLOWED)) {
			e.setCancelled(true);
			e.setCancelReason(new ComponentBuilder(event.getKickMessage()).create());
		}
	}

	private String getReason(net.md_5.bungee.api.event.LoginEvent e) {
		BaseComponent[] comp = e.getCancelReasonComponents();
		if (comp == null || comp.length == 0)
			return "";
		return comp[0].toPlainText();
	}

	@EventHandler
	public void onCommand(ChatEvent e) {
		if (!(e.getSender() instanceof ProxiedPlayer))
			return;
		ProxiedPlayer p = (ProxiedPlayer) e.getSender();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new BungeePlayer(p));
		String message = e.getMessage().substring(1);
		String cmd = message.split(" ")[0];
		String[] arg = message.replace(cmd + " ", "").split(" ");
		String prefix = arg.length == 0 ? "" : arg[arg.length - 1].toLowerCase(Locale.ROOT);
		PlayerCommandPreProcessEvent event = new PlayerCommandPreProcessEvent(np.getPlayer(), cmd, arg, prefix, true);
		EventManager.callEvent(event);
		if (event.isCancelled())
			e.setCancelled(true);
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new BungeePlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		ProxiedPlayer p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new BungeePlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
	}

	@EventHandler
	public void onServerChange(ServerConnectedEvent event) {
		try {
			ClientModsListMessage message = new ClientModsListMessage(event.getPlayer().getModList());
			event.getServer().sendData(NegativityMessagesManager.CHANNEL_ID,
					NegativityMessagesManager.writeMessage(message));
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not write ClientModsListMessage : " + e.getMessage());
		}
	}

	public static class Report {

		private Object[] place;
		private String cmd;

		public Report(String cmd, Object... parts) {
			place = new Object[] { "%name%", parts[0], "%cheat%", parts[1], "%reliability%", parts[2], "%ping%",
					parts[3] };
			this.cmd = cmd;
		}

		public TextComponent toMessage(ProxiedPlayer p) {
			TextComponent msg = new TextComponent(Messages.getMessage(p.getUniqueId(), "alert", place));
			String hover = Messages.getMessage(p.getUniqueId(), "alert_hover", place);
			if (hover.contains("\\n")) {
				ArrayList<TextComponent> components = new ArrayList<>();
				TextComponent hoverMessage = new TextComponent(
						new ComponentBuilder(hover.split("\\n")[hover.split("\\n").length - 2]).color(ChatColor.GOLD)
								.create());
				hoverMessage.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
				hoverMessage.addExtra(new TextComponent(
						new ComponentBuilder(hover.split("\\n")[hover.split("\\n").length - 1]).create()));
				components.add(hoverMessage);
				msg.setHoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(components.toArray(new BaseComponent[0]))));
			} else
				msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder(hover).create())));
			msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
			return msg;
		}
	}
}
