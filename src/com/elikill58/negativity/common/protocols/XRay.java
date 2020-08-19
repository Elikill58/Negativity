package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.block.BlockBreakEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.Minerate.MinerateType;

public class XRay extends Cheat {
	
	public XRay() {
		super(CheatKeys.XRAY, false, Materials.EMERALD_ORE, CheatCategory.WORLD, false);
	}
	
	@EventListener
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		NegativityAccount.get(p.getUniqueId()).getMinerate().addMine(MinerateType.fromId(e.getBlock().getType().getId()), p);
	}
}
