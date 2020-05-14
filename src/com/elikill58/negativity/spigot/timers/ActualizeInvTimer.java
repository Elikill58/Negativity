package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.inventories.AbstractInventory.InventoryType;
import com.elikill58.negativity.spigot.inventories.CheckMenuInventory;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ActualizeInvTimer extends BukkitRunnable {

	private static final boolean INV_FREEZE_ACTIVE = Adapter.getAdapter().getConfig().getBoolean("inventory.inv_freeze_active");

	@Override
	public void run() {
		for (Player p : Inv.CHECKING.keySet()) {
			if (p.getOpenInventory() != null) {
				String title = Utils.getInventoryTitle(p.getOpenInventory());
				if (title.equals(Inv.NAME_ACTIVED_CHEAT_MENU) || title.equals(Inv.NAME_FORGE_MOD_MENU)) {
				} else if (title.equals(Inv.NAME_CHECK_MENU))
					CheckMenuInventory.actualizeCheckMenu(p, Inv.CHECKING.get(p));
				else if (title.equals(Inv.NAME_ALERT_MENU))
					AbstractInventory.getInventory(InventoryType.ALERT).ifPresent((inv) -> inv.actualizeInventory(p, Inv.CHECKING.get(p)));
				//AlertInventory.actualizeAlertMenu(p, Inv.CHECKING.get(p));
				else
					Inv.CHECKING.remove(p);
			} else
				Inv.CHECKING.remove(p);
		}
		for (Player p : Utils.getOnlinePlayers()) {
			if (SpigotNegativityPlayer.getNegativityPlayer(p).isFreeze && INV_FREEZE_ACTIVE) {
				AbstractInventory.open(InventoryType.FREEZE, p);
			}
		}
	}
}
