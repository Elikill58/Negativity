package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.common.protocols.data.FastPlaceData;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.logger.Debug;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastPlace extends Cheat implements Listeners {

	public FastPlace() {
		super(CheatKeys.FAST_PLACE, CheatCategory.WORLD, Materials.DIRT, FastPlaceData::new, CheatDescription.BLOCKS);
	}

	@Check(name = "time", description = "Time between 2 place")
	public void onBlockPlace(PacketReceiveEvent e, FastPlaceData data) {
		Player p = e.getPlayer();
		NPacket packet = e.getPacket();
		if (packet.getPacketType().isFlyingPacket()) {
			data.reduce();
			return;
		} else if (!(packet instanceof NPacketPlayInBlockPlace))
			return;
		long actual = System.currentTimeMillis();
		long diff = actual - data.lastTime;
		if (diff < getConfig().getDouble("checks.time.time_place", 25)) { // TODO check according to ticks
			if (++data.buffer > 2) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - diff * (1/data.buffer)), "time", "Diff: " + diff + ", " + data.buffer);
				if (isSetBack() && mayCancel)
					e.setCancelled(true);
			}
		} else if(diff > 500) // old
			data.buffer = 0;
		else
			data.reduce();
		Adapter.getAdapter().debug(Debug.CHECK, "Diff: " + diff + ", buffer: " + data.buffer);
		data.lastTime = actual;
	}
}
