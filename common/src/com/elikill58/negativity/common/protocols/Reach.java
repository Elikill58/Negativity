package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class Reach extends Cheat {

	public static final DataType<Double> HIT_DISTANCE = new DataType<Double>("hit_distance", "Hit Distance",
			() -> new DoubleDataCounter());
	private static final List<Material> IGNORED_TYPE = Arrays.asList(Materials.BOW, Materials.FISHING_ROD);

	public Reach() {
		super(CheatKeys.REACH, CheatCategory.COMBAT, Materials.STONE_AXE, false, true);
	}

	@Check(name = "reach-event", description = "The reach", conditions = { CheckConditions.NO_THORNS,
			CheckConditions.NO_INSIDE_VEHICLE })
	public void onPacketReceive(PacketReceiveEvent e, NegativityPlayer np) {
		NPacket packet = e.getPacket().getPacket();
		Player p = e.getPlayer();
		if (packet.getPacketType().equals(PacketType.Client.POSITION)) {
			if (np.locations.has(getKey(), "entity-hit-position")) {
				Location positions = np.locations.remove(getKey(), "entity-hit-position");
				Entity cible = np.entities.remove(getKey(), "entity-hit-object");
				if(np.isTeleporting)
					return; // just beeing tp
				Location loc = p.getLocation();
				Adapter.getAdapter().debug("Positions: " + positions + ", locs: " + loc);
				double dis = getDistance(loc, positions);
				recordData(p.getUniqueId(), HIT_DISTANCE, dis);
				Adapter.getAdapter()
						.debug("Distance between " + p.getName() + " and " + cible.getName() + ": " + dis);
				double max = getConfig().getDouble("checks.reach-event.value", 3.2)
						+ (p.getGameMode().equals(GameMode.CREATIVE) ? 1 : 0);
				if (dis > max) {
					boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this,
							parseInPorcent((dis - max) * 90), "reach-event",
							"Exact distance: " + dis + ". Loc: " + loc.toString() + ", cible: " + positions,
							hoverMsg("distance", "%name%", cible.getName(), "%distance%", String.format("%.2f", dis)));
					if (isSetBack() && mayCancel)
						e.setCancelled(true);
				}
			}
		} else if (packet instanceof NPacketPlayInUseEntity) {
			NPacketPlayInUseEntity useEntity = (NPacketPlayInUseEntity) packet;
			if (!useEntity.action.equals(NPacketPlayInUseEntity.EnumEntityUseAction.ATTACK) || p.hasElytra())
				return;
			ItemStack inHand = p.getItemInHand();
			if (inHand != null && IGNORED_TYPE.contains(inHand.getType()))
				return;
			Entity cible = p.getWorld().getEntityWithID(useEntity.entityId);
			if (cible == null)
				return;
			Location cibleLoc = cible.getLocation();
			if (cible instanceof Player) {
				cibleLoc = NegativityPlayer.getNegativityPlayer((Player) cible).getPingedLocation();
			}
			np.locations.set(getKey(), "entity-hit-position", cibleLoc);
			np.entities.set(getKey(), "entity-hit-object", cible);
		}
	}

	private double getDistance(Location loc, Location other) {
		double width = 0.403125;
		double dx = Math.min(Math.abs(loc.getX() - (other.getX() - width)),
				Math.abs(loc.getX() - (other.getX() + width)));
		double dz = Math.min(Math.abs(loc.getZ() - (other.getZ() - width)),
				Math.abs(loc.getZ() - (other.getZ() + width)));
		return Math.sqrt(dx * dx + dz * dz);
	}

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> counters = data.getData(HIT_DISTANCE);
		return Utils.coloredMessage("Hit distance (Sum/Min/Max) : " + getColoredDistance(counters.getAverage()) + "/"
				+ getColoredDistance(counters.getMin()) + "/" + getColoredDistance(counters.getMax()));
	}

	private String getColoredDistance(double dis) {
		return Utils.coloredMessage((dis > 3 ? (dis > 4 ? "&c" : "&6") : "&a") + String.format("%.3f", dis) + "&r");
	}
}
