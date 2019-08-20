package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;

@SuppressWarnings("deprecation")
public class AutoClickProtocol extends Cheat implements Listener {

	public AutoClickProtocol() {
		super("AUTOCLICK", false, Material.FISHING_ROD, false, true, "auto-click", "autoclic");
	}

	public static final int CLICK_ALERT = Adapter.getAdapter().getIntegerInConfig("cheats.autoclick.click_alert");

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		ItemStack item = e.getItem();
		if (item != null)
			if (item.getType() == Material.SUGAR_CANE)
				return;
		manageClick(e.getPlayer(), e);
	}
	
	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player))
			return;
		manageClick((Player) e.getDamager(), e);
	}
	
	private void manageClick(Player p, Cancellable e) {
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (p.getItemInHand() != null)
			if (ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType())) {
				ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType());
				if (ib.getWhen().isClick() && ib.isForThisCheat(this))
					return;
			}
		np.ACTUAL_CLICK++;
		np.updateCheckMenu();
		int ping = Utils.getPing(p), click = np.ACTUAL_CLICK - (ping / 9);
		if (click > CLICK_ALERT && np.ACTIVE_CHEAT.contains(this)) {
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this,
					Utils.parseInPorcent(np.ACTUAL_CLICK * 2.5),
					"Clicks in one second: " + np.ACTUAL_CLICK + "; Last second: " + np.LAST_CLICK
							+ "; Better click in one second: " + np.BETTER_CLICK + " Ping: " + ping,
					np.ACTUAL_CLICK + " clicks");
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
}
