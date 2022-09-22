package com.elikill58.negativity.common.protocols;

import java.util.Arrays;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerDamagedByEntityEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType.Client;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.CriticalData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;

public class Critical extends Cheat {
	
	public Critical() {
		super(CheatKeys.CRITICAL, CheatCategory.COMBAT, Materials.FIREBALL, CriticalData::new);
	}

	@Check(name = "ground", description = "Check damage according to Y", conditions = { CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.SURVIVAL, CheckConditions.NO_FLY, CheckConditions.NO_GROUND })
	public void onDamage(PlayerDamagedByEntityEvent e, NegativityPlayer np, CriticalData data) {
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
	public void onDamageA(PlayerDamagedByEntityEvent e, NegativityPlayer np, CriticalData data) {
		if (e.isCancelled())
			return;
		NPacketPlayInPositionLook l1 = data.positions.get(3);
		NPacketPlayInPositionLook l2 = data.positions.get(2);
		NPacketPlayInPositionLook l3 = data.positions.get(1);
		NPacketPlayInPositionLook base = data.positions.get(0);
		for(NPacketPlayInPositionLook checkingLocs : Arrays.asList(l1, l2, l3)) // check for no X/Z move
			if(checkingLocs.getX() != base.getX() || checkingLocs.getZ() != base.getZ())
				return;
		double baseY = base.getY();
		if(baseY + 0.11 == l3.getY() && baseY + 0.1100013579 == l3.getY() && baseY + 0.0000013579 == l3.getY()) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, np.getPlayer(), this, 100, "y-pos", "Positions: " + data.positions);
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
	}
	
	@EventListener
	public void onPacket(PacketReceiveEvent e) {
		if(e.getPacket().getPacketType().equals(Client.POSITION_LOOK)) {
			NegativityPlayer.getNegativityPlayer(e.getPlayer()).<CriticalData>getCheckData(this).add((NPacketPlayInPositionLook) e.getPacket().getPacket());
		}
	}
}
