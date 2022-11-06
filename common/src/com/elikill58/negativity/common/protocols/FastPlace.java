package com.elikill58.negativity.common.protocols;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.block.BlockPlaceEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.common.protocols.data.FastPlaceData;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastPlace extends Cheat implements Listeners {

	public FastPlace() {
		super(CheatKeys.FAST_PLACE, CheatCategory.WORLD, Materials.DIRT, FastPlaceData::new, CheatDescription.BLOCKS);
	}

	@Check(name = "time", description = "Time between 2 place")
	public void onBlockPlace(PacketReceiveEvent e, FastPlaceData data) {
		if (e.getPacket().getPacketType().isFlyingPacket()) {
			data.timeFlying++;
		}
	}
	
	@EventListener
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		FastPlaceData data = NegativityPlayer.getNegativityPlayer(p).getCheckData(this);
		if (data.timeFlying < 20) {
			data.times.add(data.timeFlying);
			if (data.times.size() > 2) {
				List<Integer> medianList = new ArrayList<>(data.times);
				medianList.sort(Comparator.naturalOrder());
				int middle = medianList.size() / 2;
				int median = (medianList.size() % 2 == 1) ? medianList.get(middle) : (int) ((medianList.get(middle - 1) + medianList.get(middle)) / 2.0);
				Adapter.getAdapter().debug("Median: " + median);
				if (median < getConfig().getDouble("checks.times.median", 2)) {
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
							UniversalUtils.parseInPorcent(100 - median * 2), "time",
							"Quickly: " + median + ", " + data.times, null, median == 0 ? 50 : (median == 1 ? 25 : 10 - median));
					if (isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
		} else if (data.timeFlying > 200) // if outdated
			data.times.clear();
		data.timeFlying = 0;
	}
}
