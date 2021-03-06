package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;

public class InventoryMoveProtocol extends Cheat implements Listener {

	public InventoryMoveProtocol() {
		super(CheatKeys.INVENTORY_MOVE, false, Material.NETHER_STAR, CheatCategory.MOVEMENT, true, "invmove");
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null)
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer((Player) e.getWhoClicked());
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove((Player) e.getWhoClicked(), true, "Click");
	}

	/*@EventHandler
	public void onOpen(InventoryOpenEvent e) {
		if (!(e.getPlayer() instanceof Player) || e.getInventory() == null)
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer((Player) e.getPlayer());
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove((Player) e.getPlayer(), false, "Open");
	}*/

	private void checkInvMove(Player p, boolean check, String from) {
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if(np.hasElytra() || p.isInsideVehicle() || p.getLocation().getBlock().getType().name().contains("WATER"))
			return;
		if (p.isSprinting() || p.isSneaking()) {
			Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> {
				if (p.isSprinting() || p.isSneaking())
					SpigotNegativity.alertMod(ReportType.WARNING, p, InventoryMoveProtocol.this,
							np.getAllWarn(InventoryMoveProtocol.this) > 5 ? 100 : 95,
								"Detected when " + from + ". Sprint: " + p.isSprinting() + ", Sneak:" + p.isSneaking(), hoverMsg("main", "%name%", from));
				
			}, 3);
		} else if (check) {
			final Location lastLoc = p.getLocation().clone();
			Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> {
				if(!lastLoc.getWorld().equals(p.getLocation().getWorld()))
					return;
				double dis = lastLoc.distance(p.getLocation());
				if (dis > 1 && (lastLoc.getY() - p.getLocation().getY()) < 0.1
						&& p.getOpenInventory() != null) {
					SpigotNegativity.alertMod(ReportType.WARNING, p, InventoryMoveProtocol.this,
							np.getAllWarn(InventoryMoveProtocol.this) > 5 ? 100 : 95,
								"Detected when " + from + ", Distance: " + dis + " Diff Y: " + (lastLoc.getY() - p.getLocation().getY()), hoverMsg("main", "%name%", from));
				}
			}, 5);
		}
	}
	
	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
