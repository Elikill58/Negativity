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
			CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NO_FALL_DISTANCE })
	public void onMove(PlayerMoveEvent e, NegativityPlayer np) {
		if (!e.isMoveLook() || !e.isMovePosition()) {
			return;
		}

		InventoryMoveData data = np.invMoveData;
		if (data == null) {
			return;
		}

		Player p = e.getPlayer();
		// if in water
		if (LocationUtils.isInWater(p.getLocation()) || p.getVelocity().length() > 0.1) {
			return;
		}
		if (p.getOpenInventory() == null) {
			Adapter.getAdapter().debug("No opened inventory but data always running ?");
			return;
		}
		double last = data.getLastDistance();
		double actual = e.getFrom().distance(e.getTo());
		if (actual >= last && actual >= p.getWalkSpeed()) { // if running at least at the same
			data.addTimeSinceOpen();
			if(data.getTimeSinceOpen() >= 5) {
				int amount = 1;
				if (p.isSprinting())
					amount += data.sprint ? 1 : 5; // more alerts if wasn't sprinting
				if (p.isSneaking())
					amount += data.sneak ? 1 : 5; // more alerts if wasn't sneaking
				Negativity
						.alertMod(np.getAllWarn(this) > 5 && amount > 1 ? ReportType.VIOLATION : ReportType.WARNING, p,
								this, UniversalUtils.parseInPorcent(80 + data.getTimeSinceOpen()), "stay-distance",
								"Sprint: " + p.isSprinting() + ", Sneak: " + p.isSneaking() + ", data: " + data + ", vel: "
										+ p.getVelocity() + ", fd: " + String.format("%.5f", p.getFallDistance()),
								null, amount);
			}
		}
		data.setDistance(actual);
	}

	@EventListener
	public void onClick(InventoryClickEvent e) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(e.getPlayer());
		if (np.hasDetectionActive(this))
			checkInvMove(np);
	}

	@EventListener
	public void onOpen(InventoryOpenEvent e) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(e.getPlayer());
		if (np.hasDetectionActive(this))
			checkInvMove(np);
	}

	@EventListener
	public void onClose(InventoryCloseEvent e) {
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).invMoveData = null;
	}

	private void checkInvMove(NegativityPlayer np) {
		np.invMoveData = new InventoryMoveData(np.getPlayer());
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
		
		public void addTimeSinceOpen() {
			this.timeSinceOpen++;
		}

		public void setDistance(double distance) {
			this.lastDistance = distance;
		}

		@Override
		public String toString() {
			return "InventoryMoveData{sprint=" + sprint + ",sneak=" + sneak + ",distance=" + String.format("%.3f", lastDistance) + ",time="
					+ timeSinceOpen + "}";
		}
	}
}
