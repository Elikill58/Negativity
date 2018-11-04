package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ActualizeInvTimer extends BukkitRunnable {

	public static final boolean INV_FREEZE_ACTIVE = Adapter.getAdapter().getBooleanInConfig("inventory.inv_freeze_active");

	@Override
	public void run() {
		for (Player p : Inv.CHECKING.keySet()) {
			if (p.getOpenInventory().getTopInventory().getTitle().equals(Inv.NAME_ACTIVED_CHEAT_MENU)) {
			} else if (p.getOpenInventory().getTopInventory().getTitle().equals(Inv.NAME_CHECK_MENU))
				Inv.actualizeCheckMenu(p, Inv.CHECKING.get(p));
			else if (p.getOpenInventory().getTopInventory().getTitle().equals(Inv.NAME_ALERT_MENU))
				Inv.actualizeAlertMenu(p, Inv.CHECKING.get(p));
			else
				Inv.CHECKING.remove(p);
		}
		for (Player p : Utils.getOnlinePlayers()) {
			if (SpigotNegativityPlayer.getNegativityPlayer(p).isFreeze && INV_FREEZE_ACTIVE) {
				p.closeInventory();
				Inv.openFreezeMenu(p);
			}
		}
	}
}
