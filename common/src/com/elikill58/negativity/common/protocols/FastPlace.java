package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.common.protocols.data.FastPlaceData;
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
	public void onBlockPlace(PacketReceiveEvent e, NegativityPlayer np, FastPlaceData data) {
		Player p = e.getPlayer();
		NPacket packet = e.getPacket();
		int actual = np.getTicks();
		if (packet.getPacketType().isFlyingPacket()) {
			int diff = actual - data.lastTick;
			if(diff > 20)
				data.times.clear();
		} else if (packet instanceof NPacketPlayInBlockPlace) {
			NPacketPlayInBlockPlace place = (NPacketPlayInBlockPlace) packet;
			ItemStack item = place.hand == null || place.hand.equals(Hand.MAIN) ? p.getItemInHand() : p.getItemInOffHand();
			if(item == null || !item.getType().isSolid())
				return; // can't be placed
			int diff = actual - data.lastTick;
			data.times.add(diff);
			double sum = (double) data.times.stream().mapToInt(Integer::intValue).sum() / data.times.size();
			if (data.times.size() > 2 && sum < getConfig().getDouble("checks.time.time_ticks", 3) && diff < 5) {
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - diff * (1 / sum)), "time",
							"Diff: " + diff + ", times: " + data.times);
					if (isSetBack() && mayCancel)
						e.setCancelled(true);
			}
			data.lastTick = actual;
		}
	}
}
