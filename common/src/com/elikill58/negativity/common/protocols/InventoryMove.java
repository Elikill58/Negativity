package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryOpenEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.report.ReportType;

public class InventoryMove extends Cheat implements Listeners {

	private final InventoryMove instance;

	public InventoryMove() {
		super(CheatKeys.INVENTORY_MOVE, CheatCategory.MOVEMENT, Materials.NETHER_STAR, false, false, "invmove");
		instance = this;
	}

	@EventListener
	public void onClick(InventoryClickEvent e) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(e.getPlayer());
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove(e.getPlayer(), true, "Click");
	}

	@EventListener
	public void onOpen(InventoryOpenEvent e) {
		NegativityPlayer np = NegativityPlayer.getCached(e.getPlayer().getUniqueId());
		if (!np.hasDetectionActive(this))
			return;
		checkInvMove(e.getPlayer(), false, "Open");
	}

	private void checkInvMove(Player p, boolean check, String from) {
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.hasElytra())
			return;
		if (p.isSprinting() || p.isSneaking()) {
			Scheduler.getInstance().runDelayed(() -> {
				if (p.isSprinting() || p.isSneaking())
					Negativity.alertMod(ReportType.WARNING, p, instance,
						NegativityPlayer.getCached(p.getUniqueId()).getAllWarn(instance) > 5 ? 100 : 95, "sprint",
						"Detected when " + from + ". Sprint: " + p.isSprinting() + ", Sneak:" + p.isSneaking(), hoverMsg("main", "%name%", from));
			}, 3);
		} else if (check) {
			final Location lastLoc = p.getLocation().clone();
			Scheduler.getInstance().runDelayed(() -> {
				if (!lastLoc.getWorld().equals(p.getLocation().getWorld()))
					return;
				double dis = lastLoc.distance(p.getLocation());
				if (dis > 1 && (lastLoc.getY() - p.getLocation().getY()) < 0.1
					&& p.getOpenInventory() != null) {
					Negativity.alertMod(ReportType.WARNING, p, instance,
						NegativityPlayer.getCached(p.getUniqueId()).getAllWarn(instance) > 5 ? 100 : 95, "distance",
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
