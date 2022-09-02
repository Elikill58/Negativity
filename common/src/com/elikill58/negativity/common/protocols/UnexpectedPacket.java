package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType.Client;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction.EnumPlayerAction;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class UnexpectedPacket extends Cheat {

	public UnexpectedPacket() {
		super(CheatKeys.UNEXPECTED_PACKET, CheatCategory.PLAYER, Materials.JUKEBOX);
	}

	@Check(name = "vehicle-steer", description = "When moving in vehicle, but not in vehicle")
	public void onPacketReceive(PacketReceiveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		if (e.getPacket().getPacketType().equals(Client.STEER_VEHICLE)) {
			if (!p.isInsideVehicle()) {
				long timeLeftVehicle = System.currentTimeMillis() - np.longs.get(getKey(), "vehicle-left", 0l);
				if (timeLeftVehicle < 50)
					return; // just left, strange packet but prevent issue
				long amount = timeLeftVehicle / 50;
				boolean cancel = Negativity.alertMod(ReportType.WARNING, p, this,
						UniversalUtils.parseInPorcent(amount < 100 ? 50 + amount : 100), "vehicle-steer",
						"Actual vehicle: " + p.getVehicle() + ", timeLeft: " + timeLeftVehicle,
						new CheatHover.Literal("Say he's moving with vehicle when not in vehicle"),
						amount <= 0 ? 1 : (amount > 10000 ? 10000 : amount));
				if (cancel && isSetBack())
					e.setCancelled(true);
			}
		} else if (e.getPacket().getPacketType().equals(Client.ENTITY_ACTION)) {
			NPacketPlayInEntityAction action = (NPacketPlayInEntityAction) e.getPacket().getPacket();
			if (action.action.equals(EnumPlayerAction.START_SNEAKING) && p.isInsideVehicle()) {
				np.longs.set(getKey(), "vehicle-left", System.currentTimeMillis());
			}
		}
	}

	@Check(name = "spectator", description = "Spectate someone without in spectator")
	public void onSpectate(PacketReceiveEvent e) {
		Player p = e.getPlayer();
		if (e.getPacket().getPacketType().equals(Client.SPECTATE) && p.getGameMode().equals(GameMode.CREATIVE)) {
			boolean cancel = Negativity.alertMod(ReportType.WARNING, p, this, 100, "spectator",
					"Spectate when using gamemode: " + p.getGameMode().name(),
					new CheatHover.Literal("Spectate someone when using " + p.getGameMode().getName()));
			if (cancel && isSetBack())
				e.setCancelled(true);
		}
	}
}
