package com.elikill58.negativity.spigot.protocols;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.PlayerPacketsClearEvent;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class TimerProtocol extends Cheat implements Listener {

	public TimerProtocol() {
		super(CheatKeys.TIMER, true, Material.PACKED_ICE, CheatCategory.MOVEMENT, true);
	}
	
	@EventHandler
	public void onPacketClear(PlayerPacketsClearEvent e) {
		SpigotNegativityPlayer np = e.getNegativityPlayer();
		if(!np.hasDetectionActive(this))
			return;
		HashMap<PacketType, Integer> packets = e.getPackets();
		int flying = packets.getOrDefault(PacketType.Client.FLYING, 0);
		int position = packets.getOrDefault(PacketType.Client.POSITION, 0);
		int look = packets.getOrDefault(PacketType.Client.LOOK, 0);
		int positonLook = packets.getOrDefault(PacketType.Client.POSITION_LOOK, 0);
		int count = flying + look + position + positonLook;
		np.TIMER_COUNT.add(count);
		
		if(np.TIMER_COUNT.size() > 5) // now we can remove first value (5 secs later)
			np.TIMER_COUNT.remove((int) 0);
		else // loading seconds
			return;
		double sum = np.TIMER_COUNT.stream().mapToInt(Integer::intValue).sum() / np.TIMER_COUNT.size();
		int variation = Adapter.getAdapter().getConfig().getInt("cheats.timer.max_variation");
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
		if(!medianRespect) // prevent false flag
			return;
		int amount = (int) (sum - MAX_VARIATION);
		SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - (np.ping / 100) - (medianRespect ? 15 : -10)),
				"Flying: " + flying + ", position: " + position + ", look: " + look + ", positionLook: " + positonLook + ", sum: " + sum + ", median: " + medianValue,
				(CheatHover) null, amount > 0 ? amount : 1);
		// TODO implement setBack option for Timer
	}
}
