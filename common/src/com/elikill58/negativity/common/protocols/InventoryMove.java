package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryCloseEvent;
import com.elikill58.negativity.api.events.inventory.InventoryOpenEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class InventoryMove extends Cheat implements Listeners {

	public InventoryMove() {
		super(CheatKeys.INVENTORY_MOVE, CheatCategory.MOVEMENT, Materials.NETHER_STAR, CheatDescription.NO_FIGHT);
	}

	@Check(name = "stay-distance", description = "Keep distance while moving", conditions = { CheckConditions.NO_ELYTRA,
			CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_INSIDE_VEHICLE })
	public void onMove(PlayerMoveEvent e, NegativityPlayer np) {
		if (e.isMoveLook())
			return;

		InventoryMoveData data = np.invMoveData;
		if (data == null)
			return;
		
		Player p = e.getPlayer();
		// if in water
		if (LocationUtils.isInWater(p.getLocation()) || p.getVelocity().length() > 0.1) {
			Adapter.getAdapter().debug("Velocity length: " + p.getVelocity().length());
			return;
		}
		if (p.getOpenInventory() == null) {
			Adapter.getAdapter().debug("No opened inventory but data always running ?");
			return;
		}
		int amount = 0;
		if(p.isSprinting())
			amount += data.sprint ? 1 : 5; // means it started sprinting since inv open
		if(p.isSneaking())
			amount += data.sneak ? 1 : 5; // means it started sneaking since inv open

		double distance = e.getFrom().distance(e.getTo());
		double distanceXZ = e.getFrom().distanceXZ(e.getTo());
		if (distanceXZ >= data.distanceXZ && distanceXZ >= p.getWalkSpeed()) // if running at least at the same
			amount += (data.distanceXZ - distanceXZ) + 1; // +1 to always have alert
		
		if(distance >= data.distance && distance >= p.getWalkSpeed() && p.getFallDistance() < 0.5) // fall "allow" to make the distance goes brr
			amount += (data.distanceXZ - distanceXZ) + 1 - p.getFallDistance();
		if(data.timeSinceOpen > 2)
			Adapter.getAdapter().debug("Time: " + data.timeSinceOpen + ", amount: " + amount);
		if (data.timeSinceOpen >= 3 && amount > 2) {
			Negativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(80 + data.timeSinceOpen), "stay-distance",
					"Sprint: " + p.isSprinting() + ", Sneak: " + p.isSneaking() + ", data: " + data + ", vel: "
							+ p.getVelocity() + ", fd: " + String.format("%.5f", p.getFallDistance()),
					null, amount);
		}
		data.update(distance, distanceXZ);
	}

	@EventListener
	public void onClick(InventoryClickEvent e) {
		if (e.isCancelled())
			return;
		//Adapter.getAdapter().debug("Click inventory");
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(e.getPlayer());
		if (np.hasDetectionActive(this))
			checkInvMove(np);
	}

	@EventListener
	public void onOpen(InventoryOpenEvent e) {
		if (e.isCancelled())
			return;
		//Adapter.getAdapter().debug("Open inventory");
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(e.getPlayer());
		if (np.hasDetectionActive(this))
			checkInvMove(np);
	}

	@EventListener
	public void onClose(InventoryCloseEvent e) {
		//Adapter.getAdapter().debug("Close inventory");
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).invMoveData = null;
	}

	private void checkInvMove(NegativityPlayer np) {
		if(np.invMoveData == null)
			np.invMoveData = new InventoryMoveData(np.getPlayer());
	}

	public static class InventoryMoveData {

		public double distance = 0, distanceXZ = 0;
		public int timeSinceOpen = 0;
		public final boolean sprint, sneak;

		public InventoryMoveData(Player p) {
			this.sprint = p.isSprinting();
			this.sneak = p.isSneaking();
		}
		
		public void update(double distance, double distanceXZ) {
			this.distance = distance;
			this.distanceXZ = distanceXZ;
			this.timeSinceOpen++;
		}

		@Override
		public String toString() {
			return "InventoryMoveData{sprint=" + sprint + ",sneak=" + sneak + ",distance="
					+ String.format("%.3f", distance) + ",distanceXZ=" + String.format("%.3f", distance)  + ",time=" + timeSinceOpen + "}";
		}
	}
}
