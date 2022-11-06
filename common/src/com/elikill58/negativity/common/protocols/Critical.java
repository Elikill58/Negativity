package com.elikill58.negativity.common.protocols;

import java.util.Arrays;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerDamagedByEntityEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.CriticalData;
import com.elikill58.negativity.common.protocols.data.CriticalData.TimedFlyingPacket;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Critical extends Cheat implements Listeners {
	
	public Critical() {
		super(CheatKeys.CRITICAL, CheatCategory.COMBAT, Materials.FIREBALL, CriticalData::new);
	}

	@Check(name = "ground", description = "Check damage according to Y", conditions = { CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.SURVIVAL, CheckConditions.NO_FLY, CheckConditions.NO_GROUND })
	public void onDamaged(PlayerDamagedByEntityEvent e, NegativityPlayer np, CriticalData data) {
		if (e.isCancelled())
			return;
		Player p = e.getPlayer();

		if(Version.getVersion().equals(Version.V1_8)) { // old pvp system
			if (p.getLocation().getY() % 1.0D == 0.0D) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, np.getAllWarn(this) > 5 ? 100 : 95, "ground", "y: " + p.getLocation().getY());
				if(mayCancel && isSetBack())
					e.setCancelled(true);
			}
		}
	}

	@Check(name = "y-pos", description = "Check for Y move before attack")
	public void onPlayerDamage(PlayerDamageEntityEvent e, NegativityPlayer np, CriticalData data) {
		if (e.isCancelled())
			return;
		TimedFlyingPacket l3 = data.positions.get(3);
		TimedFlyingPacket l2 = data.positions.get(2);
		TimedFlyingPacket l1 = data.positions.get(1);
		TimedFlyingPacket base = data.positions.get(0);
		for(TimedFlyingPacket checkingLocs : Arrays.asList(l2, l3)) // check for no X/Z move
			if(checkingLocs.getX() != l1.getX() || checkingLocs.getZ() != l1.getZ() || checkingLocs.isGround())
				return;
		double baseY = base.getY();
		if(baseY + 0.11 == l1.getY() && baseY + 0.1100013579 == l2.getY() && baseY + 0.0000013579 == l3.getY()) {
			Player p = e.getPlayer();
			long timeFor3Packets = l3.getTime() - l1.getTime();
			long tmpPing = p.getPing();
			if(tmpPing < 50) // 1 tick
				tmpPing = 50;
			long diffPacket = tmpPing - timeFor3Packets;
			if(diffPacket > 0) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(101 - timeFor3Packets), "y-pos", "Positions: " + data.positions + ", time3Packet: " + timeFor3Packets + ", diffPinged: " + diffPacket, null, diffPacket);
				if(mayCancel && isSetBack())
					e.setCancelled(true);
			}
		}
	}
	
	@EventListener
	public void onPacket(PacketReceiveEvent e) {
		if(e.getPacket().getPacketType().isFlyingPacket()) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) e.getPacket().getPacket();
			if(flying.hasLocation()) {
				NegativityPlayer.getNegativityPlayer(e.getPlayer()).<CriticalData>getCheckData(this).add(flying);
			}
		}
	}
}
