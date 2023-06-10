package com.elikill58.negativity.spigot.protocols;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.blocks.SpigotLocation;
import com.elikill58.negativity.spigot.utils.LocationUtils;
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
		if (!np.hasDetectionActive(this) || np.isUsingTrident() || np.hasElytra() || p.isInsideVehicle() || p.getFallDistance() > 0.5 || np.inventoryMoveData == null
				|| LocationUtils.isInWater(new SpigotLocation(p.getLocation())) || p.getVelocity().length() > 0.1 || !np.inventoryMoveData.active || LocationUtils.hasMaterialsAround(new SpigotLocation(e.getFrom()), "PISTON")) {
			Adapter.getAdapter().debug("Velocity length: " + p.getVelocity().length());
			return;
		}
		if (p.getOpenInventory() == null) {
			np.inventoryMoveData = null;
			Adapter.getAdapter().debug("No opened inventory but data always running ?");
			return;
		}
		InventoryMoveData data = np.inventoryMoveData;
		int amount = 0;
		if (p.isSprinting())
			amount += data.sprint ? 1 : 5; // means it started sprinting since inv open
		if (p.isSneaking())
			amount += data.sneak ? 1 : 5; // means it started sneaking since inv open

		double distance = e.getFrom().distance(e.getTo());
		double distanceXZ = LocationUtils.distanceXZ(e.getFrom(), e.getTo());
		if (distanceXZ >= data.distanceXZ && distanceXZ >= p.getWalkSpeed()) // if running at least at the same
			amount += (data.distanceXZ - distanceXZ) + 1; // +1 to always have alert

		if (distance >= data.distance && distance >= p.getWalkSpeed() && p.getFallDistance() < 0.5) // fall "allow" to make the distance goes brr
			amount += (data.distanceXZ - distanceXZ) + 1 - p.getFallDistance();
		if (data.timeSinceOpen > 2)
			Adapter.getAdapter().debug("Time: " + data.timeSinceOpen + ", amount: " + amount);
		if (data.timeSinceOpen >= 3 && amount > 2) {
			SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(80 + data.timeSinceOpen),
					"Sprint: " + p.isSprinting() + ", Sneak: " + p.isSneaking() + ", data: " + data + ", vel: " + p.getVelocity() + ", fd: " + String.format("%.5f", p.getFallDistance()),
					(CheatHover) null, amount);
		}
		data.update(distance, distanceXZ);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null || !e.getSlotType().equals(SlotType.CONTAINER) || e.isCancelled())
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer((Player) e.getWhoClicked());
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove(np, (Player) e.getWhoClicked(), "Click");
	}

	@EventHandler
	public void onOpen(InventoryOpenEvent e) {
		if (!(e.getPlayer() instanceof Player) || e.isCancelled())
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer((Player) e.getPlayer());
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove(np, (Player) e.getPlayer(), "Open");
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if (e.getPlayer() instanceof Player)
			SpigotNegativityPlayer.getNegativityPlayer((Player) e.getPlayer()).inventoryMoveData.reset((Player) e.getPlayer());
	}

	private void checkInvMove(SpigotNegativityPlayer np, Player p, String from) {
		if (np.hasElytra() || p.isInsideVehicle() || p.getLocation().getBlock().getType().name().contains("WATER"))
			return;
		np.inventoryMoveData.active = true;
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}

	public static class InventoryMoveData {

		public Player p;
		public double distance = 0, distanceXZ = 0;
		public int timeSinceOpen = 0;
		public boolean sprint, sneak, active;

		public InventoryMoveData() {
			reset(null);
		}

		public void update(double distance, double distanceXZ) {
			this.distance = distance;
			this.distanceXZ = distanceXZ;
			this.timeSinceOpen++;
		}

		public void reset(Player p) {
			this.sprint = p == null ? false : p.isSprinting();
			this.sneak = p == null ? false : p.isSneaking();
			this.active = false;
			this.distance = 0;
			this.distanceXZ = 0;
			this.timeSinceOpen = 0;
		}

		@Override
		public String toString() {
			return "InventoryMoveData{sprint=" + sprint + ",sneak=" + sneak + ",distance=" + String.format("%.3f", distance) + ",distanceXZ=" + String.format("%.3f", distance) + ",time="
					+ timeSinceOpen + "}";
		}
	}
}
