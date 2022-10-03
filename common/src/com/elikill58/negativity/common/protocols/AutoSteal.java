package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.AUTO_STEAL;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryAction;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.AutoStealData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.bypass.checkers.ItemUseBypass;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AutoSteal extends Cheat {

	public AutoSteal() {
		super(AUTO_STEAL, CheatCategory.PLAYER, Materials.CHEST, AutoStealData::new);
	}

	@Check(name = "time-click", description = "Time between 2 clicks", conditions = { CheckConditions.SURVIVAL,
			CheckConditions.NO_ON_BEDROCK })
	public void onInvClick(InventoryClickEvent e, NegativityPlayer np, AutoStealData data) {
		Player p = e.getPlayer();
		ItemStack inHand = p.getItemInHand();
		if (inHand != null)
			if (ItemUseBypass.hasBypassWithClick(p, this, inHand, e.getAction().name()))
				return;
		long actual = System.currentTimeMillis(), dif = actual - data.invClick;
		int ping = p.getPing();
		if (e.getCurrentItem() != null) {
			if (data.invSlot == e.getSlot())
				data.invItem = e.getCurrentItem().getType();
			
			if (data.invItem == e.getCurrentItem().getType())
				return;
		}
		if (dif < 0 || data.invSlot == e.getSlot()) {
			data.invSlot = e.getSlot();
			data.invClick = actual;
			return;
		}
		int timeClick = getConfig().getInt("checks.time-click.time", 55);
		if ((ping + timeClick) >= dif && data.invSlot != e.getSlot()) {
			if (data.invWas && !e.getAction().equals(InventoryAction.LEFT_SHIFT)) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent((100 + timeClick) - dif - ping), "time-click",
						"Time between 2 click: " + dif + ", action: " + e.getAction().name(),
						hoverMsg("main", "%time%", dif));
				if (isSetBack() && mayCancel)
					e.setCancelled(true);
			}
			data.invWas = true;
		} else
			data.invWas = false;
		data.invSlot = e.getSlot();
		data.invClick = actual;
	}
}
