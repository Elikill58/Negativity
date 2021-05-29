package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.NegativityPlayerMoveEvent;
import com.elikill58.negativity.spigot.listeners.PlayerPacketsClearEvent;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class BlinkProtocol extends Cheat implements Listener {
	
	public BlinkProtocol() {
		super(CheatKeys.BLINK, true, Material.COAL_BLOCK, CheatCategory.MOVEMENT, true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e){
		SpigotNegativityPlayer.getNegativityPlayer(e.getEntity()).bypassBlink = true;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(NegativityPlayerMoveEvent e){
		e.getNegativityPlayer().bypassBlink = false;
	}
	
	@EventHandler
	public void onPacketClear(PlayerPacketsClearEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = e.getNegativityPlayer();
		if(!np.hasDetectionActive(this))
			return;
		if (!(!np.bypassBlink && (p.getGameMode().equals(GameMode.ADVENTURE) || p.getGameMode().equals(GameMode.SURVIVAL))))
			return;
		int ping = np.ping;
		if (ping < 140 && !np.isBedrockPlayer()) {
			int total = np.ALL - np.PACKETS.getOrDefault(PacketType.Client.KEEP_ALIVE, 0);
			if (total == 0) {
				if(UniversalUtils.parseInPorcent(100 - ping) >= getReliabilityAlert()) {
					boolean last = np.IS_LAST_SEC_BLINK == 2;
					np.IS_LAST_SEC_BLINK++;
					long time_last = System.currentTimeMillis() - np.TIME_OTHER_KEEP_ALIVE;
					if (last && np.LAST_OTHER_KEEP_ALIVE != null && !np.LAST_OTHER_KEEP_ALIVE.equalsIgnoreCase("PacketPlayInCustomPayload")) {
						SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(100 - ping),
								"No packet. Last other than KeepAlive: " + np.LAST_OTHER_KEEP_ALIVE + " there is: "
										+ time_last + "ms . Ping: " + ping + ". Warn: " + np.getWarn(this));
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
				SpigotNegativity.alertMod(allPos > 70 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(20 + allPos), "PositionLook packet: " + posLook + " Position Packet: " + pos +  " (=" + allPos + ") Ping: " + ping + " Warn for Timer: " + np.getWarn(this));
			}
		}
	}
}
