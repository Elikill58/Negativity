package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;

public class InventoryMoveProtocol extends Cheat implements Listener {

	private final InventoryMoveProtocol instance;

	public InventoryMoveProtocol() {
		super(CheatKeys.INVENTORY_MOVE, false, Utils.getMaterialWith1_15_Compatibility("NETHER_STAR", "LEGACY_NETHER_STAR"),
				CheatCategory.MOVEMENT, true, "invmove");
		instance = this;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null)
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer((Player) e.getWhoClicked());
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		checkInvMove((Player) e.getWhoClicked(), true, "Click");
	}

	@EventHandler
	public void onOpen(InventoryOpenEvent e) {
		if (!(e.getPlayer() instanceof Player) || e.getInventory() == null)
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer((Player) e.getPlayer());
		if (!np.ACTIVE_CHEAT.contains(this))
			return;
		checkInvMove((Player) e.getPlayer(), false, "Open");
	}

	private void checkInvMove(Player p, boolean check, String from) {
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.isSprinting() || p.isSneaking()) {
			Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (p.isSprinting() || p.isSneaking())
						SpigotNegativity.alertMod(ReportType.WARNING, p, instance,
								SpigotNegativityPlayer.getNegativityPlayer(p).getAllWarn(instance) > 5 ? 100 : 95,
									"Detected when " + from + ". Sprint: " + p.isSprinting() + ", Sneak:" + p.isSneaking(), "When " + from, "When " + from);
				}
			}, 3);
		} else if (check) {
			final Location lastLoc = p.getLocation().clone();
			Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
				@Override
				public void run() {
					double dis = lastLoc.distance(p.getLocation());
					if (dis > 1 && (lastLoc.getY() - p.getLocation().getY()) < 0.1
							&& p.getOpenInventory() != null) {
						SpigotNegativity.alertMod(ReportType.WARNING, p, instance,
								SpigotNegativityPlayer.getNegativityPlayer(p).getAllWarn(instance) > 5 ? 100 : 95,
									"Detected when " + from + ", Distance: " + dis + " Diff Y: " + (lastLoc.getY() - p.getLocation().getY()), "When " + from, "When " + from);
					}
				}
			}, 5);
		}
	}
}
