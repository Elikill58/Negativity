package com.elikill58.negativity.api;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.packets.packet.NPacket;

public class TimingPacket {

	private static final ScheduledExecutorService POOL = Executors.newScheduledThreadPool(8);
	private static final long timeUpdate = 10, diffTime = 5;

	private ConcurrentHashMap<NPacket, Long> packets = new ConcurrentHashMap<NPacket, Long>();
	private final Player p;
	private final ScheduledFuture<?> cleaner;

	public TimingPacket(Player p) {
		this.p = p;
		this.cleaner = POOL.scheduleAtFixedRate(this::check, timeUpdate, timeUpdate, TimeUnit.MILLISECONDS);
	}

	public void add(NPacket packet) {
		int ping = p.getPing();
		if(ping < diffTime)
			finalReceive(packet);
		else
			packets.put(packet, System.currentTimeMillis() + p.getPing());
	}

	public void destroy() {
		cleaner.cancel(false);
		packets.clear();
	}

	public void check() {
		if (!p.isOnline()) {
			destroy();
			return;
		}
		long currentTime = System.currentTimeMillis();
		for(Entry<NPacket, Long> entries : new HashMap<>(packets).entrySet()) {
			NPacket key = entries.getKey();
        	long time = entries.getValue();
			long diff = currentTime - (time + timeUpdate);
			if (diff > -diffTime) { // -5 for very near packets
				packets.remove(key);
				finalReceive(key);
			}
		}
	}
	
	private void finalReceive(NPacket packet) {
		PacketReceiveEvent e = new PacketReceiveEvent(packet, p);
		EventManager.callEvent(e);
	}
}