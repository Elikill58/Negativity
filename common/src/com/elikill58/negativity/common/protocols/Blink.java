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
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Blink extends Cheat implements Listeners {
	
	public Blink() {
		super(CheatKeys.BLINK, CheatCategory.MOVEMENT, Materials.COAL_BLOCK, true, false);
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
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(e.getPlayer());
		AbstractPacket packet = e.getPacket();
		if (packet.getPacketType() != Client.KEEP_ALIVE) {
			np.TIME_OTHER_KEEP_ALIVE = System.currentTimeMillis();
			np.LAST_OTHER_KEEP_ALIVE = packet.getPacketName();
		}
	}
	
	@Check(name = "no-packet", description = "Count when player don't send any packet", conditions = { CheckConditions.SURVIVAL })
	public void onPacketClear(PlayerPacketsClearEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if (np.bypassBlink)
			return;
		int ping = p.getPing();
		if (ping < 140) {
			int total = np.ALL_PACKETS - np.PACKETS.getOrDefault(PacketType.Client.KEEP_ALIVE, 0);
			if (total == 0) {
				if(UniversalUtils.parseInPorcent(100 - ping) >= getReliabilityAlert()) {
					boolean last = np.IS_LAST_SEC_BLINK == 2;
					np.IS_LAST_SEC_BLINK++;
					long time_last = System.currentTimeMillis() - np.TIME_OTHER_KEEP_ALIVE;
					if (last && time_last >= 1000 && !np.LAST_OTHER_KEEP_ALIVE.equalsIgnoreCase("PacketPlayInCustomPayload")) {
						Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - ping),
								"no-packet", "No packet. Last other than KeepAlive: " + np.LAST_OTHER_KEEP_ALIVE + " there is: "
										+ time_last + "ms .");
					}
				}
			} else
				np.IS_LAST_SEC_BLINK = 0;
		} else 
			np.IS_LAST_SEC_BLINK = 0;
		
		if(ping < getMaxAlertPing()){
			int posLook = np.PACKETS.getOrDefault(PacketType.Client.POSITION_LOOK, 0), pos = np.PACKETS.getOrDefault(PacketType.Client.POSITION, 0);
			int allPos = posLook + pos;
			if(allPos > 60) {
				Negativity.alertMod(allPos > 70 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(20 + allPos), "position-packet",
						"PositionLook packet: " + posLook + " Position Packet: " + pos +  " (=" + allPos + ")");
			}
		}
	}
}
