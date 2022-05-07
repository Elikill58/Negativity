package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerDeathEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Client;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Blink extends Cheat implements Listeners {
	
	public Blink() {
		super(CheatKeys.BLINK, CheatCategory.MOVEMENT, Materials.COAL_BLOCK);
	}

	@EventListener
	public void onPlayerDeath(PlayerDeathEvent e){
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).bypassBlink = true;
	}
	
	@EventListener
	public void onPlayerMove(PlayerMoveEvent e){
		NegativityPlayer.getNegativityPlayer(e.getPlayer()).bypassBlink = false;
	}
	
	@EventListener
	public void onPacketReceive(PacketReceiveEvent e) {
		AbstractPacket packet = e.getPacket();
		if (packet.getPacketType() != Client.KEEP_ALIVE && e.hasPlayer()) {
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(e.getPlayer());
			np.otherKeepAliveTime = System.currentTimeMillis();
			np.otherKeepAlivePacket = packet.getPacketType();
		}
	}
	
	@Check(name = "no-packet", description = "Count when player don't send any packet", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ON_BEDROCK })
	public void onPacketClear(PlayerPacketsClearEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if (np.bypassBlink || np.otherKeepAliveTime == 0)
			return;
		int ping = p.getPing();
		if (ping < 140) {
			int total = np.allPackets - np.packets.getOrDefault(PacketType.Client.KEEP_ALIVE, 0);
			if (total == 0) {
				if(UniversalUtils.parseInPorcent(100 - ping) >= getReliabilityAlert()) {
					boolean last = np.booleans.get(getKey(), "no-packet-is", false);
					long time_last = System.currentTimeMillis() - np.otherKeepAliveTime;
					if (last && time_last >= 1000 && !np.otherKeepAlivePacket.equals(PacketType.Client.CUSTOM_PAYLOAD)) {
						Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - ping),
								"no-packet", "No packet. Last other than KeepAlive: " + np.otherKeepAlivePacket.getPacketName() + " there is: "
										+ time_last + "ms.");
					}
					np.booleans.set(getKey(), "no-packet-is", true);
				}
			} else
				np.booleans.remove(getKey(), "no-packet-is");
		} else 
			np.booleans.remove(getKey(), "no-packet-is");
		
		if(ping < getMaxAlertPing()){
			int posLook = np.packets.getOrDefault(PacketType.Client.POSITION_LOOK, 0), pos = np.packets.getOrDefault(PacketType.Client.POSITION, 0);
			int allPos = posLook + pos;
			if(allPos > 60) {
				Negativity.alertMod(allPos > 70 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(20 + allPos), "position-packet",
						"PositionLook packet: " + posLook + " Position Packet: " + pos +  " (=" + allPos + ")");
			}
		}
	}
}
