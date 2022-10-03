package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.protocols.data.EmptyData;
import com.elikill58.negativity.universal.FlyingReason;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;

public class FastEat extends Cheat implements Listeners {
	
	public FastEat() {
		super(CheatKeys.FAST_EAT, CheatCategory.PLAYER, Materials.COOKED_BEEF, EmptyData::new, CheatDescription.HEALTH);
	}

	@EventListener
	public void onItemConsume(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		np.flyingReason = FlyingReason.EAT;
		np.eatMaterial = p.getItemInHand().getType();
	}
}
