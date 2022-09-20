package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.common.protocols.data.FastPlaceData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastPlace extends Cheat {

	public FastPlace() {
		super(CheatKeys.FAST_PLACE, CheatCategory.WORLD, Materials.DIRT, FastPlaceData::new, CheatDescription.BLOCKS);
	}

	@Check(name = "time", description = "Time between 2 place")
	public void onBlockPlace(PacketReceiveEvent e, NegativityPlayer np, FastPlaceData data) {
		Player p = e.getPlayer();
		if (e.getPacket().getPacketType().equals(PacketType.Client.POSITION_LOOK)) {
			data.timeFlying++;
		} else if (e.getPacket().getPacketType().equals(PacketType.Client.BLOCK_PLACE)) {
			if (data.timeFlying < 20) {
				data.times.add(data.timeFlying);
				if (data.times.size() > 2) {
					double total = 0;
					for (Integer temp : data.times)
						total += temp;
					double average = (total / data.times.size());

					if (average < 1.5) {
						boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
								UniversalUtils.parseInPorcent(100 - average * 5), "time",
								"Quickly: " + String.format("%.3f", average) + ", " + data.times);
						if (isSetBack() && mayCancel)
							e.setCancelled(true);
					}
				}
			} else if (data.timeFlying > 200) // if outdated
				data.times.clear();
			data.timeFlying = 0;
		}
	}
}
