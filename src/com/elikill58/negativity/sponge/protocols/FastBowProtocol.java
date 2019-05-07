package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer.FlyingReason;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.*;

public class FastBowProtocol extends Cheat {

	public FastBowProtocol() {
		super("FASTBOW", true, ItemTypes.BOW, false, true, "bow");
	}
	
	@Listener
	@Exclude(InteractEntityEvent.class)
	public void onPlayerInteract(InteractItemEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if(!np.hasDetectionActive(this))
			return;
		if(p.getItemInHand(HandTypes.MAIN_HAND).isPresent())
			if(ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand(HandTypes.MAIN_HAND).get().getType())) {
				ItemUseBypass ib = ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand(HandTypes.MAIN_HAND).get().getType());
				if(ib.getWhen().isClick() && ib.isForThisCheat(this))
					return;
			}
		if (np.getItemTypeInHand().equals(ItemTypes.BOW)) {
			np.flyingReason = FlyingReason.BOW;
			long actual = System.currentTimeMillis(), dif = actual - np.LAST_SHOT_BOW;
			if (np.LAST_SHOT_BOW != 0) {
				int ping = Utils.getPing(p);
				if (dif < (200 + ping)) {
					boolean mayCancel = SpongeNegativity.alertMod(ReportType.VIOLATION, p, this,
							Utils.parseInPorcent((dif < (50 + ping) ? 200 : 100) - dif - ping),
							"Player use Bow, last shot: " + np.LAST_SHOT_BOW + " Actual time: " + actual
									+ " Difference: " + dif + ", Warn: " + np.getWarn(this),
							"Time between last shot: " + dif + " (in milliseconds).");
					if (isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
			np.LAST_SHOT_BOW = actual;
		}
	}
}
