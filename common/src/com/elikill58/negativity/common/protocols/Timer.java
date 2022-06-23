package com.elikill58.negativity.common.protocols;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Timer extends Cheat {

	public Timer() {
		super(CheatKeys.TIMER, CheatCategory.MOVEMENT, Materials.PACKED_ICE);
	}
	
	@Check(name = "packet", description = "Check Y move only")
	public void onPacketClear(PlayerPacketsClearEvent e) {
		NegativityPlayer np = e.getNegativityPlayer();
		HashMap<PacketType, Integer> packets = e.getPackets();
		int flying = packets.getOrDefault(PacketType.Client.FLYING, 0);
		int position = packets.getOrDefault(PacketType.Client.POSITION, 0);
		int look = packets.getOrDefault(PacketType.Client.LOOK, 0);
		int positonLook = packets.getOrDefault(PacketType.Client.POSITION_LOOK, 0);
		int steerVehicle = packets.getOrDefault(PacketType.Client.STEER_VEHICLE, 0);
		int count = flying + look + position + positonLook - steerVehicle;
		if(count < 0)
			return;
		np.timerCount.add(count);
		
		if(np.timerCount.size() > 6) // now we can remove first value (6 secs later)
			np.timerCount.remove(0);
		else // loading seconds
			return;
		double sum = np.timerCount.stream().mapToInt(Integer::intValue).sum() / np.timerCount.size();
		int variation = getConfig().getInt("max_variation");
		Player p = e.getPlayer();
		int MAX = (p.getGameMode().equals(GameMode.CREATIVE) ? 40 : 20);
		int MAX_VARIATION = MAX + variation;
		if(MAX_VARIATION > sum)// in min/max variations
			return;
		List<Integer> medianList = new ArrayList<>(np.timerCount);
		medianList.sort(Comparator.naturalOrder());
		int middle = medianList.size() / 2;
		int medianValue = (medianList.size() % 2 == 1) ? medianList.get(middle) : (int) ((medianList.get(middle-1) + medianList.get(middle)) / 2.0);
		if(medianValue < MAX + 3) // if only one time, and median is safe
			return;
		boolean medianRespect = MAX_VARIATION > medianValue;
		int amount = (int) (sum - MAX_VARIATION);
		Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - (p.getPing() / 100) - (medianRespect ? 15 : -10)), "packet",
				"Flying: " + flying + ", position: " + position + ", look: " + look + ", positionLook: " + positonLook + ", sum: " + sum + ", median: " + medianValue,
				null, amount > 0 ? amount : 1);
		// TODO implement setBack option for Timer
	}
}
