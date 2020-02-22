package com.elikill58.negativity.spigot.events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.commands.ReportCommand;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Minerate.MinerateType;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class PlayersEvents implements Listener {

	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		UUID playerId = e.getPlayer().getUniqueId();
		SpigotNegativityPlayer.removeFromCache(playerId);

		NegativityAccount account = Adapter.getAdapter().getNegativityAccount(playerId);
		if(Ban.isBanned(account)) {
			if(Ban.canConnect(account))
				return;
			boolean isDef = false;
			for(BanRequest br : account.getBanRequest())
				if(br.isDef())
					isDef = true;
			e.setResult(Result.KICK_BANNED);
			e.setKickMessage(Messages.getMessage(e.getPlayer(), "ban.kick_" + (isDef ? "def" : "time"), "%reason%", account.getBanReason(), "%time%" , account.getBanTime(), "%by%", account.getBanBy()));
			Adapter.getAdapter().invalidateAccount(account.getPlayerId());
		}
	}

	private boolean hasSentBungeecordMessage = false;
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(UniversalUtils.isMe(p.getUniqueId()))
			p.sendMessage(ChatColor.GREEN + "Ce serveur utilise Negativity ! Waw :')");
		if(!hasSentBungeecordMessage && !SpigotNegativity.isOnBungeecord) {
			Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> {
				try {
					byte[] pingMessage = NegativityMessagesManager.writeMessage(new ProxyPingMessage());
					p.sendPluginMessage(SpigotNegativity.getInstance(), NegativityMessagesManager.CHANNEL_ID, pingMessage);
				} catch (IOException ex) {
					SpigotNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not write ProxyPingMessage.", ex);
				}
			}, 20);
			hasSentBungeecordMessage = true;
		}
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer());
		np.TIME_INVINCIBILITY = System.currentTimeMillis() + 8000;
		if (Perm.hasPerm(np, "showAlert")) {
			if(ReportCommand.REPORT_LAST.size() > 0) {
				for (String msg : ReportCommand.REPORT_LAST)
					p.sendMessage(msg);
				ReportCommand.REPORT_LAST.clear();
			}
			Bukkit.getScheduler().runTaskAsynchronously(SpigotNegativity.getInstance(), () -> Utils.sendUpdateMessageIfNeed(p));
		}
		SpigotNegativity.manageAutoVerif(p);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(),
				() -> SpigotNegativityPlayer.removeFromCache(p.getUniqueId()), 2);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if(np.isFreeze && !p.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR))
			e.setCancelled(true);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		if(!(SuspectManager.ENABLED && SuspectManager.CHAT))
			return;
		String msg = e.getMessage().toLowerCase();
		String[] content = msg.split(" ");
		List<Player> suspected = new ArrayList<>();
		List<Cheat> cheats = new ArrayList<>();
		for(String s : content) {
			for(Cheat c : Cheat.values())
				for(String alias : c.getAliases())
					if(alias.equalsIgnoreCase(s) || alias.contains(s) || alias.startsWith(s))
						cheats.add(c);
			for(Player p : Utils.getOnlinePlayers()) {
				if(p.getName().equalsIgnoreCase(s) || p.getName().toLowerCase().startsWith(s) || p.getName().contains(s))
					suspected.add(p);
				else if(p.getDisplayName() != null)
					if(p.getDisplayName().equalsIgnoreCase(s) || p.getDisplayName().toLowerCase().startsWith(s) || p.getDisplayName().contains(s))
						suspected.add(p);
			}
		}
		for(Player suspect : suspected)
			SuspectManager.analyzeText(SpigotNegativityPlayer.getNegativityPlayer(suspect), cheats);
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer()).mineRate.addMine(MinerateType.getMinerateType(e.getBlock().getType().name()));
	}
}
