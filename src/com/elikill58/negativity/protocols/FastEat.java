package com.elikill58.negativity.protocols;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.player.PlayerItemConsumeEvent;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.FlyingReason;

public class FastEat extends Cheat implements Listeners {
	
	public FastEat() {
		super(CheatKeys.FAST_EAT, true, Materials.COOKED_BEEF, CheatCategory.PLAYER, true, "fasteat", "autoeat");
	}

	@EventListener
	public void onItemConsume(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		np.flyingReason = FlyingReason.EAT;
		np.eatMaterial = p.getItemInHand().getType();
	}
}
