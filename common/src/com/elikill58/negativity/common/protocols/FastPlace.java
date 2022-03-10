package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.LongDataCounter;

public class FastPlace extends Cheat {
	
	public static final DataType<Long> TIME_PLACE = new DataType<Long>("time_player", "Time between places", () -> new LongDataCounter());

	public FastPlace() {
		super(CheatKeys.FAST_PLACE, CheatCategory.WORLD, Materials.DIRT, false, true, "fp");
	}

	@Check(name = "time", description = "Time between 2 place", conditions = { CheckConditions.SURVIVAL })
	public void onBlockPlace(BlockPlaceEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		
		int ping = p.getPing();
		if(ping < 50)
			ping = 50;
		long last = System.currentTimeMillis() - np.lastBlockPlace, lastPing = last + (ping / 50);
		if(last < 1000) // last block is too old
			recordData(p.getUniqueId(), TIME_PLACE, last);
		np.lastBlockPlace = System.currentTimeMillis();
		if ((lastPing + 5) < getConfig().getInt("time_2_place", 50)) { // 5 is to prevent some processing time issue
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - lastPing),
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
