package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Cheat;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.adapter.Adapter;

@SuppressWarnings("deprecation")
public class AutoClickProtocol implements Listener {

	public static final int CLICK_ALERT = Adapter.getAdapter().getIntegerInConfig("cheats.autoclick.click_alert");
	public static final Cheat CHEAT = Cheat.AUTOCLICK;

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (e.getAction() != Action.LEFT_CLICK_AIR)
			return;
		ItemStack item = e.getItem();
		if (item != null)
			if (item.getType() == Material.SUGAR_CANE)
				return;
		if (p.getItemInHand() != null)
			if (ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType())) {
				ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType());
				if (ib.getWhen().isClick() && ib.isForThisCheat(CHEAT))
					if (e.getAction().name().toLowerCase().contains(ib.getWhen().name().toLowerCase()))
						return;
			}
		np.ACTUAL_CLICK++;
		int ping = Utils.getPing(p), click = np.ACTUAL_CLICK - (ping / 9);
		if (click > CLICK_ALERT) {
			np.addWarn(CHEAT);
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, CHEAT,
					Utils.parseInPorcent(np.ACTUAL_CLICK * 2.5),
					"Clicks in one second: " + np.ACTUAL_CLICK + "; Last second: " + np.LAST_CLICK
							+ "; Better click in one second: " + np.BETTER_CLICK + " Ping: " + ping,
					np.ACTUAL_CLICK + " clicks");
			if (CHEAT.isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onLeftClickPlayer(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (p.getItemInHand() != null)
			if (ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType())) {
				ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType());
				if (ib.getWhen().isClick() && ib.isForThisCheat(CHEAT))
					return;
			}
		np.ACTUAL_CLICK++;
		int ping = Utils.getPing(p), click = np.ACTUAL_CLICK - (ping / 9);
		if (click > CLICK_ALERT) {
			np.addWarn(CHEAT);
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, CHEAT,
					Utils.parseInPorcent(np.ACTUAL_CLICK * 2.5),
					"Clicks in one second: " + np.ACTUAL_CLICK + "; Last second: " + np.LAST_CLICK
							+ "; Better click in one second: " + np.BETTER_CLICK + " Ping: " + ping,
					np.ACTUAL_CLICK + " clicks");
			if (CHEAT.isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
}
