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
import com.elikill58.negativity.common.protocols.data.InventoryMoveData;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.logger.Debug;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class InventoryMove extends Cheat implements Listeners {

	public InventoryMove() {
		super(CheatKeys.INVENTORY_MOVE, CheatCategory.MOVEMENT, Materials.NETHER_STAR, InventoryMoveData::new, CheatDescription.NO_FIGHT);
	}

	@Check(name = "stay-distance", description = "Keep distance while moving", conditions = { CheckConditions.NO_ELYTRA,
			CheckConditions.NO_USE_TRIDENT, CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NO_ON_BEDROCK })
	public void onMove(PlayerMoveEvent e, NegativityPlayer np, InventoryMoveData data) {
		if (e.isMoveLook() || !data.active)
			return;
		
		Player p = e.getPlayer();
		// if in water
		if (LocationUtils.isInWater(p.getLocation()) || p.getVelocity().length() > 0.1) {
			Adapter.getAdapter().debug(Debug.CHECK, "Velocity length: " + p.getVelocity().length());
			return;
		}
		/*if (p.hasOpenInventory()) {
			Adapter.getAdapter().debug("No opened inventory but data always running ?");
			return;
		}*/
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
			Adapter.getAdapter().debug(Debug.CHECK, "Time: " + data.timeSinceOpen + ", amount: " + amount);
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
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).<InventoryMoveData>getCheckData(this).reset();
	}

	private void checkInvMove(NegativityPlayer np) {
		np.<InventoryMoveData>getCheckData(this).active = true;
	}
}
