package com.elikill58.negativity.common.protocols;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class IncorrectPacket extends Cheat {

	private final List<PacketType> allowedFarPacket = Arrays.asList(PacketType.Client.TELEPORT_ACCEPT);

	public IncorrectPacket() {
		super(CheatKeys.INCORRECT_PACKET, CheatCategory.WORLD, Materials.NAME_TAG);
	}

	@Check(name = "distance", description = "Check distance between player and sent packet")
	public void onPacket(PacketReceiveEvent e) {
		if (!e.hasPlayer())
			return;
		NPacket packet = e.getPacket().getPacket();
		if (packet instanceof LocatedPacket && !allowedFarPacket.contains(packet.getPacketType())) {
			LocatedPacket lp = (LocatedPacket) packet;
			if(!lp.hasLocation())
				return;
			Player p = e.getPlayer();
			Location loc = p.getLocation();
			double dx = loc.getX() - lp.getX() + 0.5D;
			double dy = loc.getY() - lp.getY() + 0.5D + 1.5D;
			double dz = loc.getZ() - lp.getZ() + 0.5D;
			int maxDistance = (p.getGameMode().equals(GameMode.CREATIVE) ? 49 : 36) + 5; // +5 is to prevent false flags
			double distance = dx * dx + dy * dy + dz * dz;
			if (distance > maxDistance) { // distance originally used by spigot
				e.setCancelled(true); // cancel. Everytime.
				int relia = UniversalUtils.parseInPorcent(distance * (distance < 100 ? 2.5 : 1));
				int amount = (int) (distance < 1000 ? distance - maxDistance : distance / 10);
				if (amount <= 0)
					amount = 1;
				Negativity.alertMod(distance > 1000 ? ReportType.VIOLATION : ReportType.WARNING, p, this, relia,
						"distance",
						"Packet " + e.getPacket().getPacketName() + ", player loc: " + loc.toString() + ", packet loc: "
								+ lp.getLocation(p.getWorld()).toString() + ", distance: " + distance,
						null, amount);
			}
		}
	}
}
