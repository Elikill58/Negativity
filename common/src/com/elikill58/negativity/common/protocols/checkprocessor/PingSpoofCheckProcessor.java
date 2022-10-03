package com.elikill58.negativity.common.protocols.checkprocessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.protocols.CheckProcessor;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.Cheat.CheatHover;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class PingSpoofCheckProcessor implements CheckProcessor {

	private static final Random R = new Random();
	
	private final Cheat c = Cheat.forKey(CheatKeys.PINGSPOOF);
	private final NegativityPlayer np;
	private final List<Long> waitingIds = new ArrayList<>();
	private final HashMap<Long, Long> timeById = new HashMap<>();
	private NPacketPlayOutKeepAlive lastKeepAliveSent = null;
	
	public PingSpoofCheckProcessor(NegativityPlayer np) {
		this.np = np;
	}
	
	@Override
	public void handlePacketSent(PacketSendEvent e) {
		if(!np.hasDetectionActive(c) || !c.checkActive("packet"))
			return;
		Player p = e.getPlayer();
		AbstractPacket packet = e.getPacket();
		if(packet.getPacketType().equals(PacketType.Server.KEEP_ALIVE)) {
			long pingId = R.nextInt(50000);
			waitingIds.add(pingId);
			timeById.put(pingId, System.currentTimeMillis());
			lastKeepAliveSent = (NPacketPlayOutKeepAlive) packet.getPacket();
			p.queuePacket(new NPacketPlayOutPing(pingId)); // send ping packet
		}
	}
	
	@Override
	public void handlePacketReceived(PacketReceiveEvent e) {
		if(!np.hasDetectionActive(c) || !c.checkActive("packet"))
			return;
		Player p = e.getPlayer();
		AbstractPacket packet = e.getPacket();
		if(packet.getPacketType().equals(PacketType.Client.KEEP_ALIVE)) {
			lastKeepAliveSent = null;
		} else if(packet.getPacket() instanceof NPacketPlayInPong || packet.getPacket().getPacketType().equals(PacketType.Client.PONG)) {
			NPacketPlayInPong pong = (NPacketPlayInPong) packet.getPacket();
			if(lastKeepAliveSent == null)// already received
				return;
			if(waitingIds.remove(pong.id)) { // is good id
				Long packetTime = timeById.remove(pong.id);
				if(packetTime == null) // should never append, but prevent error
					packetTime = System.currentTimeMillis();
				long realPing = System.currentTimeMillis() - packetTime;
				long reliability = p.getPing() > realPing ? p.getPing() - realPing : realPing - p.getPing(); // check if ping isn't updated yet
				long amount = (isReachable(p, p.getPing()) ? 5 : 1) * reliability / 100;
				if(amount == 0)
					amount = 1;
				Negativity.alertMod(ReportType.WARNING, p, c, UniversalUtils.parseInPorcent(reliability), "packet", "Ping: " + p.getPing() + ", real: " + realPing, new CheatHover.Literal("Real ping seems to be: " + realPing), amount);
			}
		}
	}
	
	private boolean isReachable(Player p, long ping) {
		try {
			return p.getAddress().getAddress().isReachable((int) ping + 50);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return false;
	}
}
