package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.ReachData;
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
		super(CheatKeys.REACH, CheatCategory.COMBAT, Materials.STONE_AXE, ReachData::new, CheatDescription.VERIF);
	}

	@Check(name = "reach-event", description = "The reach", conditions = { CheckConditions.NO_THORNS,
			CheckConditions.NO_INSIDE_VEHICLE })
	public void onPacketReceive(PacketReceiveEvent e, NegativityPlayer np, ReachData data) {
		NPacket packet = e.getPacket();
		Player p = e.getPlayer();
		if (packet.getPacketType().isFlyingPacket()) {
			if (data.cible != null) {
				if (np.isTeleporting || (data.cible instanceof Player
						&& NegativityPlayer.getNegativityPlayer((Player) data.cible).isTeleporting)) {
					Adapter.getAdapter().debug("Beeing TP " + data.cible);
					data.reset();
					return; // just beeing tp
				}
				Location loc = p.getLocation();
				Adapter.getAdapter().debug("Positions: " + data.cibleLocation + ", locs: " + loc);
				double dis = getDistance(loc, data.cibleLocation);
				recordData(p.getUniqueId(), HIT_DISTANCE, dis);
				Adapter.getAdapter()
						.debug("Distance between " + p.getName() + " and " + data.cible.getName() + ": " + dis);
				double max = getConfig().getDouble("checks.reach-event.value", 3.2)
						+ (p.getGameMode().equals(GameMode.CREATIVE) ? 1 : 0);
				if (dis > max) {
					if (Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent((dis - max) * 90),
							"reach-event",
							"Exact distance: " + dis + ". Loc: " + loc.toString() + ", cible: " + data.cibleLocation,
							hoverMsg("distance", "%name%", data.cible.getName(), "%distance%",
									String.format("%.2f", dis)))
							&& isSetBack())
						e.setCancelled(true);
				}
				data.reset();
			}
		} else if (packet instanceof NPacketPlayInUseEntity) {
			NPacketPlayInUseEntity useEntity = (NPacketPlayInUseEntity) packet;
			if (!useEntity.action.equals(NPacketPlayInUseEntity.EnumEntityUseAction.ATTACK) || p.hasElytra())
				return;
			ItemStack inHand = p.getItemInHand();
			if (inHand != null && IGNORED_TYPE.contains(inHand.getType()))
				return;
			Entity cible = p.getWorld().getEntityById(useEntity.entityId).orElse(null);
			if (cible == null) {
				Adapter.getAdapter().debug("Failed to find entity with ID " + useEntity.entityId + ", all: " + p.getWorld().getEntities());
				return;
			}
			Adapter.getAdapter().debug("Select entity with ID " + useEntity.entityId + ", type: " + cible.getType());
			data.cible = cible;
			data.cibleLocation = cible.getLocation();
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
		return ChatColor.color("Hit distance (Sum/Min/Max) : " + getColoredDistance(counters.getAverage()) + "/"
				+ getColoredDistance(counters.getMin()) + "/" + getColoredDistance(counters.getMax()));
	}

	private String getColoredDistance(double dis) {
		return (dis > 3 ? (dis > 4 ? "&c" : "&6") : "&a") + String.format("%.3f", dis) + "&r";
	}
}
