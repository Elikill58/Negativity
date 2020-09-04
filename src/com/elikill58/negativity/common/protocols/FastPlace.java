package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.LongDataCounter;

public class FastPlace extends Cheat implements Listeners {
	
	public static final DataType<Long> TIME_PLACE = new DataType<Long>("time_player", "Time between places", () -> new LongDataCounter());

	public FastPlace() {
		super(CheatKeys.FAST_PLACE, false, Materials.DIRT, CheatCategory.WORLD, true, "fp");
	}

	@EventListener
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if(Adapter.getAdapter().getLastTPS() < 19.1)
			return;
		
		int ping = p.getPing();
		long last = System.currentTimeMillis() - np.LAST_BLOCK_PLACE, lastPing = last - (ping / 9);
		if(last < 10000) // last block is too old
			recordData(p.getUniqueId(), TIME_PLACE, last);
		np.LAST_BLOCK_PLACE = System.currentTimeMillis();
		if (checkActive("time") && lastPing < getConfig().getInt("time_2_place", 50)) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(50 + lastPing),
					"time", "Block placed too quickly. Last time: " + last + ", Last with ping: "
					+ lastPing + ".", hoverMsg("main", "%time%", last));
			if(isSetBack() && mayCancel)
				e.setCancelled(true);
		}
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Long> counter = data.getData(TIME_PLACE);
		long av = counter.getAverage(), low = counter.getMin();
		String colorAverage = (av < 100 ? (av < 20 ? "&c" : "&6") : "&a");
		String colorLow = (low < 100 ? (low < 20 ? "&c" : "&6") : "&a");
		return Utils.coloredMessage("&6Time between place: &7Average: " + colorAverage + av + "&7, Lower: " + colorLow + low + " &7(For " + counter.getSize() + " blocks)");
	}
}
