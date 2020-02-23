package com.elikill58.negativity.sponge.protocols;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;

public class FastBowProtocol extends Cheat {

	public FastBowProtocol() {
		super(CheatKeys.FAST_BOW, true, ItemTypes.BOW, false, true, "bow");
	}

	@Listener
	@Exclude(InteractEntityEvent.class)
	public void onPlayerInteract(InteractItemEvent e, @First Player p) {
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this)) {
			return;
		}

		ItemType usedItemType = e.getItemStack().getType();
		ItemUseBypass usedItemBypass = ItemUseBypass.ITEM_BYPASS.get(usedItemType.getId());
		if (usedItemBypass != null && usedItemBypass.getWhen().isClick()
				&& usedItemBypass.isForThisCheat(this)) {
			return;
		}

		if (usedItemType == ItemTypes.BOW) {
			np.flyingReason = FlyingReason.BOW;
			long actual = System.currentTimeMillis();
			long dif = actual - np.LAST_SHOT_BOW;
			if (np.LAST_SHOT_BOW != 0) {
				int ping = Utils.getPing(p);
				if (dif < (200 + ping)) {
					ReportType violation;
					int reliability;
					if (dif < (50 + ping)) {
						violation = ReportType.VIOLATION;
						reliability = Utils.parseInPorcent(200 - dif - ping);
					} else {
						violation = ReportType.WARNING;
						reliability = Utils.parseInPorcent(100 - dif - ping);
					}
					boolean mayCancel = SpongeNegativity.alertMod(violation, p, this, reliability,
							"Player use Bow, last shot: " + np.LAST_SHOT_BOW + " Actual time: " + actual
									+ " Difference: " + dif + ", Warn: " + np.getWarn(this),
							"Time between last shot: " + dif + " (in milliseconds).");
					if (isSetBack() && mayCancel) {
						e.setCancelled(true);
					}
				}
			}
			np.LAST_SHOT_BOW = actual;
		}
	}

	@Override
	public String getHoverFor(NegativityPlayer p) {
		return "";
	}
}
