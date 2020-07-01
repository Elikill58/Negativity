package com.elikill58.negativity.spigot.protocols;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.PlayerPacketsClearEvent;
import com.elikill58.negativity.spigot.utils.Utils;
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
		double sum = np.TIMER_COUNT.stream().mapToInt(Integer::intValue).sum() / np.TIMER_COUNT.size();
		int variation = Adapter.getAdapter().getConfig().getInt("cheats.timer.max_variation");
		
		if(np.TIMER_COUNT.size() > 5) // now we can remove first value (5 secs later)
			np.TIMER_COUNT.remove((int) 0);
		else // loading seconds
			return;
		int MAX = 20 + variation, MIN = 20 - variation;
		if(MAX > sum && MIN < sum)// in min/max variations
			return;
		Player p = e.getPlayer();
		int amount = (int) (sum > MAX ? sum - MAX : MIN - sum);
		SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - (Utils.getPing(p) / 100)),
				"Flying: " + flying + ", position: " + position + ", look: " + look + ", positionLook: " + positonLook + ", sum: " + sum,
				(CheatHover) null, amount > 0 ? amount : 1);
		// TODO implement setBack option for Timer
	}
}
