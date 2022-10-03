package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class InventoryMoveData extends CheckData {

	public double distance = 0, distanceXZ = 0;
	public int timeSinceOpen = 0;
	public boolean sprint, sneak, active;

	public InventoryMoveData(NegativityPlayer np) {
		super(np);
		reset();
	}
	
	public void update(double distance, double distanceXZ) {
		this.distance = distance;
		this.distanceXZ = distanceXZ;
		this.timeSinceOpen++;
	}
	
	public void reset() {
		this.sprint = np.getPlayer().isSprinting();
		this.sneak = np.getPlayer().isSneaking();
		this.active = false;
		this.distance = 0;
		this.distanceXZ = 0;
		this.timeSinceOpen = 0;
	}

	@Override
	public String toString() {
		return "InventoryMoveData{sprint=" + sprint + ",sneak=" + sneak + ",distance="
				+ String.format("%.3f", distance) + ",distanceXZ=" + String.format("%.3f", distance)  + ",time=" + timeSinceOpen + "}";
	}
}
