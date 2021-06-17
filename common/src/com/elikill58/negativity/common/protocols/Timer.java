package com.elikill58.negativity.common.protocols;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Timer extends Cheat implements Listeners {

	public Timer() {
		super(CheatKeys.TIMER, CheatCategory.MOVEMENT, Materials.PACKED_ICE, true, false);
	}
	
	@Check(name = "packet")
	public void onPacketClear(PlayerPacketsClearEvent e) {
		NegativityPlayer np = e.getNegativityPlayer();
		HashMap<PacketType, Integer> packets = e.getPackets();
		int flying = packets.getOrDefault(PacketType.Client.FLYING, 0);
		int position = packets.getOrDefault(PacketType.Client.POSITION, 0);
		int look = packets.getOrDefault(PacketType.Client.LOOK, 0);
		int positonLook = packets.getOrDefault(PacketType.Client.POSITION_LOOK, 0);
		int count = flying + look + position + positonLook;
		np.TIMER_COUNT.add(count);
		
		if(np.TIMER_COUNT.size() > 5) // now we can remove first value (5 secs later)
			np.TIMER_COUNT.remove(0);
		else // loading seconds
			return;
		double sum = np.TIMER_COUNT.stream().mapToInt(Integer::intValue).sum() / np.TIMER_COUNT.size();
		int variation = getConfig().getInt("max_variation");
		Player p = e.getPlayer();
		int MAX = (p.getGameMode().equals(GameMode.CREATIVE) ? 40 : 20);
		int MAX_VARIATION = MAX + variation;
		if(MAX_VARIATION > sum)// in min/max variations
			return;
		List<Integer> medianList = new ArrayList<>(np.TIMER_COUNT);
		medianList.sort(Comparator.naturalOrder());
		int middle = medianList.size() / 2;
		int medianValue = (medianList.size() % 2 == 1) ? medianList.get(middle) : (int) ((medianList.get(middle-1) + medianList.get(middle)) / 2.0);
		boolean medianRespect = MAX_VARIATION > medianValue;
		int amount = (int) (sum - MAX_VARIATION);
		Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - (p.getPing() / 100) - (medianRespect ? 15 : -10)), "packet",
				"Flying: " + flying + ", position: " + position + ", look: " + look + ", positionLook: " + positonLook + ", sum: " + sum + ", median: " + medianValue,
				null, amount > 0 ? amount : 1);
		// TODO implement setBack option for Timer
	}
}
