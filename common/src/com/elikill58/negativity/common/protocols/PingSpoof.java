package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.PINGSPOOF;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.common.protocols.data.PingSpoofData;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.LongDataCounter;

public class PingSpoof extends Cheat implements Listeners {

	public static final DataType<Long> PLAYER_PING = new DataType<Long>("player_ping", "Ping", LongDataCounter::new);
	
	public PingSpoof() {
		super(PINGSPOOF, CheatCategory.PLAYER, Materials.SPONGE, PingSpoofData::new, CheatDescription.VERIF);
	}

	@Check(name = "packet", description = "Check for packet order")
	public void onPacket(PacketEvent e, NegativityPlayer np) {}
	
	@EventListener
	public void onPacketSent(PacketSendEvent e) {
		AbstractPacket packet = e.getPacket();
		if(packet.getPacketType().equals(PacketType.Server.KEEP_ALIVE)) {
			PingSpoofData data = (PingSpoofData) getOrCreate(NegativityPlayer.getNegativityPlayer(e.getPlayer()));
			data.pingId = ((NPacketPlayOutKeepAlive) packet.getPacket()).time;
			data.pingTime = System.currentTimeMillis();
		}
	}
	
	@EventListener
	public void onPacketReceive(PacketReceiveEvent e) {
		AbstractPacket packet = e.getPacket();
		if(packet.getPacketType().equals(PacketType.Client.KEEP_ALIVE)) {
			NPacketPlayInKeepAlive keepAlive = (NPacketPlayInKeepAlive) packet.getPacket();
			PingSpoofData data = (PingSpoofData) getOrCreate(NegativityPlayer.getNegativityPlayer(e.getPlayer()));
			if(data.pingId == keepAlive.time && data.pingId != 0) {
				recordData(e.getPlayer().getUniqueId(), PLAYER_PING, System.currentTimeMillis() - data.pingTime);
			}
		}
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Long> counters = data.getData(PLAYER_PING);
		return ChatColor.color("Latency (Sum/Min/Max) : " + counters.getAverage() + "/" + counters.getMin() + "/" + counters.getMax());
	}
}
