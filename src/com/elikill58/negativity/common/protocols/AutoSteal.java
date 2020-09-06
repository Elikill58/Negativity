package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.AUTO_STEAL;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.bypass.ItemUseBypass;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AutoSteal extends Cheat implements Listeners {

	public AutoSteal() {
		super(AUTO_STEAL, false, Materials.CHEST, CheatCategory.PLAYER, true, "steal");
	}

	public static final int TIME_CLICK = 55;
	
	@EventListener
	public void onInvClick(InventoryClickEvent e) {
		Player p = e.getPlayer();
		if(!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)))
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if(!np.hasDetectionActive(this))
			return;
		if(p.getItemInHand() != null)
			if(ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType().getId())) {
				ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType().getId());
				if(ib.getWhen().isClick() && ib.isForThisCheat(this))
					if(e.getAction().name().toLowerCase().contains(ib.getWhen().name().toLowerCase()))
						return;
			}
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
		if(dif < 0)
			return;
		if((ping + TIME_CLICK) >= dif && tempSlot != e.getSlot()){
			if(np.booleans.get(AUTO_STEAL, "inv-was", false)){
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent((100 + TIME_CLICK) - dif - ping),
						"time-click", "Time between 2 click: " + dif, hoverMsg("main", "%time%", dif));
				if(isSetBack() && mayCancel)
					e.setCancelled(true);
			}
			np.booleans.set(AUTO_STEAL, "inv-was", true);
		} else np.booleans.remove(AUTO_STEAL, "inv-was");
		np.longs.set(AUTO_STEAL, "inv-click", actual);
	}
}
