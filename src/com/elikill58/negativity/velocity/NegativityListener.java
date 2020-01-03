package com.elikill58.negativity.velocity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.util.ModInfo.Mod;

import net.kyori.text.TextComponent;
import net.kyori.text.TextComponent.Builder;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;

public class NegativityListener {

	public static List<Report> report = new ArrayList<>();

	@Subscribe
	public void onMessageReceived(PluginMessageEvent event) {
		if (!event.getIdentifier().getId().toLowerCase().contains("negativity"))
			return;
		if(event.getIdentifier().getId().equalsIgnoreCase(UniversalUtils.CHANNEL_NEGATIVITY_BUNGEECORD) || event.getIdentifier().getId().startsWith(UniversalUtils.CHANNEL_NEGATIVITY_BUNGEECORD)) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
		    out.writeUTF("no");
		    Player player = (Player) (event.getSource() instanceof Player ? event.getSource() : (event.getTarget() instanceof Player ? event.getTarget() : null));
		    if(player != null)
		    	player.getCurrentServer().ifPresent((srv) -> {srv.sendPluginMessage(new LegacyChannelIdentifier(UniversalUtils.CHANNEL_NEGATIVITY_BUNGEECORD), out.toByteArray());});
		    else 
				VelocityNegativity.getInstance().getLogger().error("Source and Target not proxied (Sender: "
						+ event.getSource() + " Receiver: " + event.getTarget() + ")");
		} else {
			try (ByteArrayInputStream ba = new ByteArrayInputStream(event.getData());
					DataInputStream in = new DataInputStream(ba)) {
				String line = in.readUTF();
			    Player p = (Player) (event.getSource() instanceof Player ? event.getSource() : (event.getTarget() instanceof Player ? event.getTarget() : null));
				if (p == null)
					VelocityNegativity.getInstance().getLogger().error("Source and Target not proxied (Sender: "
							+ event.getSource() + " Receiver: " + event.getTarget() + ")");
				String[] parts = line.split("/\\*\\*/");
				if (parts.length > 3) {
					String[] place = new String[] { "%name%", parts[0], "%cheat%", parts[1], "%reliability%", parts[2],
							"%ping%", parts[3] };
					String alertMessage = parts.length > 5 ? "alert" : parts[5];
					for (Player pp : VelocityNegativity.getInstance().getServer().getAllPlayers())
						if (Perm.hasPerm(VelocityNegativityPlayer.getNegativityPlayer(pp), "showAlert")) {
							Builder msg = TextComponent.builder();
							msg.append(VelocityMessages.getMessage(pp, alertMessage, place));
							String hover = (VelocityMessages.getStringMessage(pp, "alert_hover", place) + (parts.length > 4 && !parts[4].equalsIgnoreCase("") ? "\n" + parts[4] : ""));
							if (hover.contains("\n")) {
								Builder hoverMessage = TextComponent.builder("").color(TextColor.GOLD);
								for(String s : hover.split("\\n"))
									hoverMessage.append(VelocityMessages.coloredBungeeMessage(s));
								msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, hoverMessage.build()));
							} else
								msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, VelocityMessages.coloredBungeeMessage(hover)));
							msg.clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, getCommand(p, pp)));
							pp.sendMessage(msg.build());
						}
				} else {
					String[] place = new String[] { "%name%", parts[0], "%reason%", parts[1], "%report%", parts[2] };
					boolean hasPermitted = false;
					for (Player pp : VelocityNegativity.getInstance().getServer().getAllPlayers())
						if (Perm.hasPerm(VelocityNegativityPlayer.getNegativityPlayer(pp), "showReport")) {
							hasPermitted = true;
							Builder msg = TextComponent.builder();
							msg.append(VelocityMessages.getMessage(pp, "report", place));
							msg.clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, getCommand(p, pp)));
							pp.sendMessage(msg.build());
						}
					if (!hasPermitted)
						report.add(new Report("/server " + p.getCurrentServer().get().getServerInfo().getName(), place));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getCommand(Player p, Player pp) {
		return (pp.getCurrentServer().get().getServerInfo().equals(p.getCurrentServer().get().getServerInfo()) ? "/tp " : "/server ") + p.getCurrentServer().get().getServerInfo().getName();
	}
	
	@Subscribe
	public void onPostLogin(PostLoginEvent e) {
		Player p = e.getPlayer();
		VelocityNegativityPlayer np = VelocityNegativityPlayer.getNegativityPlayer(p);
		if(Ban.isBanned(np.getAccount())) {
			if(Ban.canConnect(np.getAccount()))
				return;
			boolean isDef = false;
			for(BanRequest br : np.getAccount().getBanRequest())
				if(br.isDef())
					isDef = true;
			p.disconnect(VelocityMessages.getMessage(e.getPlayer(), "ban.kick_" + (isDef ? "def" : "time"), "%reason%", np.getAccount().getBanReason(), "%time%" , np.getAccount().getBanTime(), "%by%", np.getAccount().getBanBy()));
			return;
		}
		VelocityNegativity.getInstance().getServer().getScheduler().buildTask(VelocityNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				final ByteArrayDataOutput out = ByteStreams.newDataOutput();
				if(p.getModInfo().isPresent()) {
					List<Mod> mods = p.getModInfo().get().getMods();
					out.writeUTF("mod:" + mods.size());
					for(Mod m : mods)
						out.writeUTF(m.getId() + ":" + m.getVersion());
				} else
					out.writeUTF("mod:0");
				p.getCurrentServer().ifPresent((srv) -> {srv.sendPluginMessage(new LegacyChannelIdentifier(UniversalUtils.CHANNEL_NEGATIVITY_BUNGEECORD), out.toByteArray());});
			}
		}).delay(1, TimeUnit.SECONDS);
	    
		if (Perm.hasPerm(np, "showAlert"))
			for (Report msg : report) {
				p.sendMessage(msg.toMessage(p));
				report.remove(msg);
			}
	}

	public static class Report {

		private String[] place;
		private String cmd;

		public Report(String cmd, String... parts) {
			place = new String[] { "%name%", parts[0], "%cheat%", parts[1], "%reliability%", parts[2], "%ping%",
					parts[3] };
			this.cmd = cmd;
		}

		public TextComponent toMessage(Player p) {
			Builder msg = TextComponent.builder(VelocityMessages.getStringMessage(p, "alert", place));
			String hover = VelocityMessages.getStringMessage(p, "alert_hover", place);
			if (hover.contains("\\n")) {
				Builder hoverMessage = TextComponent.builder("");
				hoverMessage.color(TextColor.GOLD);
				for(String s : hover.split("\\n"))
					hoverMessage.append(TextComponent.builder(s));
				msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, hoverMessage.build()));
			} else
				msg.hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.builder(hover).build()));
			msg.clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, cmd));
			return msg.build();
		}
	}
}
