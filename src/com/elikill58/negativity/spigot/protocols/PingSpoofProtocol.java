package com.elikill58.negativity.spigot.protocols;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;

public class PingSpoofProtocol extends Cheat implements Listener {

	public PingSpoofProtocol() {
		super(CheatKeys.PINGSPOOF, false, Material.CRACKED_STONE_BRICKS, CheatCategory.PLAYER, true, "ping",
				"spoofing");

		Bukkit.getScheduler().runTaskTimer(SpigotNegativity.getInstance(), () -> {
			for (Player p : Utils.getOnlinePlayers()) {
				managePingSpoof(p, SpigotNegativityPlayer.getNegativityPlayer(p));
			}
		}, 6, 6);
	}

	public static void managePingSpoof(Player p, SpigotNegativityPlayer np) {
		int newPing = Utils.getPing(p), lastPing = np.LAST_PING;
		if (newPing == lastPing)
			return;
		np.LAST_PING = newPing;
		if (newPing <= 200)
			return;
		Bukkit.getScheduler().runTaskAsynchronously(SpigotNegativity.getInstance(), () -> {
			try {
				if (p.getPlayer().getAddress().getAddress().isReachable(newPing - 150)) {
					SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.PINGSPOOF), 98,
							"Last ping: " + lastPing + ", new ping: " + newPing + ".");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
}
