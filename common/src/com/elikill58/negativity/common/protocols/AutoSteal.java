package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.AUTO_STEAL;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryAction;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.bypass.checkers.ItemUseBypass;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AutoSteal extends Cheat {

	public AutoSteal() {
		super(AUTO_STEAL, CheatCategory.PLAYER, Materials.CHEST);
	}

	public static final int TIME_CLICK = 55;
	
	@Check(name = "time-click", description = "Time between 2 clicks", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ON_BEDROCK })
	public void onInvClick(InventoryClickEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		ItemStack inHand = p.getItemInHand();
		if(inHand != null)
			if(ItemUseBypass.hasBypassWithClick(p, this, inHand, e.getAction().name()))
				return;
		long actual = System.currentTimeMillis(), dif = actual - np.longs.get(AUTO_STEAL, "inv-click", 0l);
		int ping = p.getPing();
		int tempSlot = np.ints.get(AUTO_STEAL, "inv-slot", 0);
		if (e.getCurrentItem() != null) {
			Material lastType = np.materials.get(AUTO_STEAL, "last-click", null);
			if (tempSlot == e.getSlot()) {
				lastType = e.getCurrentItem().getType();
				np.materials.set(AUTO_STEAL, "last-click", e.getCurrentItem().getType());
			}
			if (lastType == e.getCurrentItem().getType())
				return;
		}
		np.ints.set(AUTO_STEAL, "inv-slot", e.getSlot());
		if(dif < 0 || tempSlot == e.getSlot())
			return;
		if((ping + TIME_CLICK) >= dif && tempSlot != e.getSlot()){
			if(np.booleans.get(AUTO_STEAL, "inv-was", false) && !e.getAction().equals(InventoryAction.LEFT_SHIFT)){
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent((100 + TIME_CLICK) - dif - ping),
						"time-click", "Time between 2 click: " + dif + ", action: " + e.getAction().name(), hoverMsg("main", "%time%", dif));
				if(isSetBack() && mayCancel)
					e.setCancelled(true);
			}
			np.booleans.set(AUTO_STEAL, "inv-was", true);
		} else np.booleans.remove(AUTO_STEAL, "inv-was");
		np.longs.set(AUTO_STEAL, "inv-click", actual);
	}
}
