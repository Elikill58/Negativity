package com.elikill58.negativity.common.protocols;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketPreReceiveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.EmptyData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class IncorrectPacket extends Cheat {

	private final List<PacketType> checkingPackets = Arrays.asList(PacketType.Client.BLOCK_DIG, PacketType.Client.BLOCK_PLACE);

	public IncorrectPacket() {
		super(CheatKeys.INCORRECT_PACKET, CheatCategory.WORLD, Materials.NAME_TAG, EmptyData::new);
	}

	@Check(name = "distance", description = "Check distance between player and sent packet", conditions = { CheckConditions.NO_TELEPORT, CheckConditions.NO_USE_TRIDENT })
	public void onPacket(PacketPreReceiveEvent e) {
		if (!e.hasPlayer())
			return;
		NPacket packet = e.getPacket();
		if (packet instanceof LocatedPacket && checkingPackets.contains(packet.getPacketType())) {
			LocatedPacket lp = (LocatedPacket) packet;
			if (!lp.hasLocation())
				return;
			if (packet instanceof NPacketPlayInBlockDig && !((NPacketPlayInBlockDig) packet).action.equals(DigAction.START_DIGGING)) // block dig but not good action
				return;
			Player p = e.getPlayer();
			Location loc = p.getLocation();
			double dx = loc.getX() - lp.getX() + 0.5D;
			double dz = loc.getZ() - lp.getZ() + 0.5D;
			int maxDistance = (p.getGameMode().equals(GameMode.CREATIVE) ? 49 : 42);
			double distance = dx * dx + dz * dz, distanceSpawn = new Vector(0, 0, 0).distance(loc.toBlockVector());
			if (distance > maxDistance && distanceSpawn > 100) { // distance originally used by spigot
				if (distanceSpawn > 1000 && distance > 1000)
					e.setCancelled(true); // cancel.
				int relia = UniversalUtils.parseInPorcent(distanceSpawn);
				int amount = (int) (distance < 1000 ? distance - maxDistance : distance / 10) / 10;
				if (amount <= 10) // can be a plugin
					return;
				if (Negativity.alertMod(distance > 10000 && distanceSpawn > 10000 ? ReportType.VIOLATION : ReportType.WARNING, p, this, relia, "distance",
						"Packet " + e.getPacket().getPacketName() + ", player loc: " + loc.toString() + ", packet loc: " + lp.getLocation(p.getWorld()).toString() + ", distance: " + distance
								+ ", spawn distance: " + distanceSpawn + ", hand: " + p.getItemInHand(),
						null, amount) && isSetBack())
					e.setCancelled(true);
			}
		}
	}
}
