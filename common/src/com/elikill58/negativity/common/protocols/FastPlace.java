package com.elikill58.negativity.common.protocols;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastPlace extends Cheat {
	
	public FastPlace() {
		super(CheatKeys.FAST_PLACE, CheatCategory.WORLD, Materials.DIRT, CheatDescription.BLOCKS);
	}

	@Check(name = "time", description = "Time between 2 place")
	public void onBlockPlace(PacketReceiveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		
		if(e.getPacket().getPacketType().isFlyingPacket()) {
			np.ints.set(getKey(), "times-flying", np.ints.get(getKey(), "times-flying", 0) + 1);
		} else if(e.getPacket().getPacketType().equals(PacketType.Client.BLOCK_PLACE)) {
			Integer flying = np.ints.remove(getKey(), "times-flying");
			if(flying == null)
				return;
			if(flying < 20) {
				List<Integer> list = np.listIntegers.get(getKey(), "times", new ArrayList<>());
				list.add(flying);
				if(list.size() >= 2) {
					double total = 0;
					for(Integer temp : list)
						total += temp;
					double average = (total / list.size());
					
					if (average < 3) {
						boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - average * 5),
								"time", "Quickly: " + String.format("%.3f", average) + ", " + list);
						if(isSetBack() && mayCancel)
							e.setCancelled(true);
					}
				}
			} else {
				if(flying > 200) // if outdated
					np.listIntegers.remove(getKey(), "times");
			}
		}
	}
}
