package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.FAST_BOW;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.entity.EntityShootBowEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastBow extends Cheat implements Listeners {
	
	public FastBow() {
		super(FAST_BOW, true, Materials.BOW, CheatCategory.COMBAT, true, "bow");
	}
	
	@EventListener
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		ItemStack item = p.getItemInHand();
		if(item == null)
			return;
		if(!np.hasDetectionActive(this) || !checkActive("last-shot"))
			return;
		
		if(ItemUseBypass.ITEM_BYPASS.containsKey(item.getType().getId())) {
			ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(item.getType().getId());
			if(ib.getWhen().isClick() && ib.isForThisCheat(this))
				if(e.getAction().name().toLowerCase().contains(ib.getWhen().name().toLowerCase()))
					return;
		}
		if (item.getType().equals(Materials.BOW) && e.getAction().name().contains("RIGHT_CLICK")) {
			np.flyingReason = FlyingReason.BOW;
			long lastShotWithBow = np.longs.get(FAST_BOW, "last-shot", 0l);
			long actual = System.currentTimeMillis(), dif = actual - lastShotWithBow;
			if (lastShotWithBow != 0) {
				int ping = p.getPing();
				if (dif < (200 + ping)) {
					boolean mayCancel = false;
					if (dif < (50 + ping))
						mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, UniversalUtils.parseInPorcent(200 - dif - ping),
								"last-shot", "Player use Bow, last shot: " + lastShotWithBow
								+ " Actual time: " + actual + " Difference: " + dif + ", Warn: " + np.getWarn(this), hoverMsg("main", "%time%", dif));
					else
						mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - dif - ping),
								"last-shot", "Player use Bow, last shot: " + lastShotWithBow
								+ " Actual time: " + actual + " Difference: " + dif + ", Warn: " + np.getWarn(this), hoverMsg("main", "%time%", dif));
					if(isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
			np.longs.set(FAST_BOW, "last-shot", actual);
		}
	}
	
	@EventListener
	public void onShot(EntityShootBowEvent e){
		if(e.getEntity().getType().equals(EntityType.PLAYER))
			NegativityPlayer.getNegativityPlayer((Player) e.getEntity()).flyingReason = FlyingReason.BOW;
	}
}
