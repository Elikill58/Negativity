package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class InventoryMoveProtocol extends Cheat implements Listener {

	public InventoryMoveProtocol() {
		super(CheatKeys.INVENTORY_MOVE, false, Material.NETHER_STAR, CheatCategory.MOVEMENT, true, "invmove");
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || np.hasElytra() || p.isInsideVehicle()
				|| p.getLocation().getBlock().getType().name().contains("WATER") || p.getFallDistance() > 0.5
				|| np.inventoryMoveData == null) // if in vehicle, in water or falling
			return;
		InventoryMoveData data = np.inventoryMoveData;
		if (data == null)
			return;
		else if (p.getOpenInventory() == null) {
			Adapter.getAdapter().debug("No opened inventory but data always running ?");
			return;
		}
		double last = data.getLastDistance();
		double actual = e.getFrom().distance(e.getTo());
		if (actual >= last && data.timeSinceOpen >= 2) { // if running at least at the same
			int amount = 1;
			if (p.isSprinting())
				amount += data.sprint ? 1 : 5; // more alerts if wasn't sprinting
			if (p.isSneaking())
				amount += data.sneak ? 1 : 5; // more alerts if wasn't sneaking
			SpigotNegativity.alertMod(np.getAllWarn(this) > 5 && amount > 1 ? ReportType.VIOLATION : ReportType.WARNING,
					p, this, UniversalUtils.parseInPorcent(80 + data.getTimeSinceOpen()),
					"Sprint: " + p.isSprinting() + ", Sneak:" + p.isSneaking() + ", data: " + data, (CheatHover) null,
					amount);
		}
		data.setDistance(actual);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null)
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer((Player) e.getWhoClicked());
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove(np, (Player) e.getWhoClicked(), "Click");
	}

	@EventHandler
	public void onOpen(InventoryOpenEvent e) {
		if (!(e.getPlayer() instanceof Player) || e.getInventory() == null)
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer((Player) e.getPlayer());
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove(np, (Player) e.getPlayer(), "Open");
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (e.getPlayer() instanceof Player)
			SpigotNegativityPlayer.getNegativityPlayer((Player) e.getPlayer()).inventoryMoveData = null;
	}

	private void checkInvMove(SpigotNegativityPlayer np, Player p, String from) {
		if (np.hasElytra() || p.isInsideVehicle() || p.getLocation().getBlock().getType().name().contains("WATER"))
			return;
		np.inventoryMoveData = new InventoryMoveData(p);
		/*
		 * if (p.isSprinting() || p.isSneaking()) {
		 * Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> { if
		 * (p.isSprinting() || p.isSneaking())
		 * SpigotNegativity.alertMod(ReportType.WARNING, p, InventoryMoveProtocol.this,
		 * np.getAllWarn(InventoryMoveProtocol.this) > 5 ? 100 : 95, "Detected when " +
		 * from + ". Sprint: " + p.isSprinting() + ", Sneak:" + p.isSneaking(),
		 * hoverMsg("main", "%name%", from));
		 * 
		 * }, 3); } else if (check) { final Location lastLoc = p.getLocation().clone();
		 * Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> {
		 * if(!lastLoc.getWorld().equals(p.getLocation().getWorld())) return; double dis
		 * = lastLoc.distance(p.getLocation()); if (dis > 1 && (lastLoc.getY() -
		 * p.getLocation().getY()) < 0.1 && p.getOpenInventory() != null) {
		 * SpigotNegativity.alertMod(ReportType.WARNING, p, InventoryMoveProtocol.this,
		 * np.getAllWarn(InventoryMoveProtocol.this) > 5 ? 100 : 95, "Detected when " +
		 * from + ", Distance: " + dis + " Diff Y: " + (lastLoc.getY() -
		 * p.getLocation().getY()), hoverMsg("main", "%name%", from)); } }, 5); }
		 */
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}

	public static class InventoryMoveData {

		private double lastDistance = 0;
		public int timeSinceOpen = 0;
		public final boolean sprint, sneak;

		public InventoryMoveData(Player p) {
			this.sprint = p.isSprinting();
			this.sneak = p.isSneaking();
		}

		public double getLastDistance() {
			return lastDistance;
		}
		
		public int getTimeSinceOpen() {
			return timeSinceOpen;
		}
		
		public void setDistance(double distance) {
			this.lastDistance = distance;
			this.timeSinceOpen++;
		}

		@Override
		public String toString() {
			return "InventoryMoveData{sprint=" + sprint + ",sneak=" + sneak + ",distance=" + lastDistance + ",time="
					+ timeSinceOpen + "}";
		}
	}
}
