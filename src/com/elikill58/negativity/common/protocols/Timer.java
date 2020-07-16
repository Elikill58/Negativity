package com.elikill58.negativity.common.protocols;

import java.util.HashMap;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Timer extends Cheat implements Listeners {

	public Timer() {
		super(CheatKeys.TIMER, true, Materials.PACKED_ICE, CheatCategory.MOVEMENT, true);
	}
	
	@EventListener
	public void onPacketClear(PlayerPacketsClearEvent e) {
		NegativityPlayer np = e.getNegativityPlayer();
		if(!np.hasDetectionActive(this))
			return;
		HashMap<PacketType, Integer> packets = e.getPackets();
		int flying = packets.getOrDefault(PacketType.Client.FLYING, 0);
		int position = packets.getOrDefault(PacketType.Client.POSITION, 0);
		int look = packets.getOrDefault(PacketType.Client.LOOK, 0);
		int positonLook = packets.getOrDefault(PacketType.Client.POSITION_LOOK, 0);
		int count = flying + look + position + positonLook;
		np.TIMER_COUNT.add(count);
		double sum = np.TIMER_COUNT.stream().mapToInt(Integer::intValue).sum() / np.TIMER_COUNT.size();
		int variation = Adapter.getAdapter().getConfig().getInt("cheats.timer.max_variation");
		
		if(np.TIMER_COUNT.size() > 5) // now we can remove first value (5 secs later)
			np.TIMER_COUNT.remove((int) 0);
		else // loading seconds
			return;
		Player p = e.getPlayer();
		int MAX = (p.getGameMode().equals(GameMode.CREATIVE) ? 40 : 20);
		int MAX_VARIATION = MAX + variation;
		if(MAX_VARIATION > sum)// in min/max variations
			return;
		int amount = (int) (sum - MAX_VARIATION);
		Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - (p.getPing() / 100)),
				"Flying: " + flying + ", position: " + position + ", look: " + look + ", positionLook: " + positonLook + ", sum: " + sum,
				null, amount > 0 ? amount : 1);
		// TODO implement setBack option for Timer
	}
}
