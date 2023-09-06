package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.detections.keys.CheatKeys.FLY;
import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockChecker;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.common.protocols.data.FlyData;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.Maths;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Fly extends Cheat implements Listeners {

	public Fly() {
		super(FLY, CheatCategory.MOVEMENT, Materials.FIREWORK, FlyData::new, CheatDescription.NO_FIGHT);
	}

	@Check(name = "no-ground-down", description = "When not in ground, check Y move", conditions = { CheckConditions.NO_GROUND, CheckConditions.NO_ALLOW_FLY })
	public void boatManager(PlayerMoveEvent e, NegativityPlayer np, FlyData data) {
		Player p = e.getPlayer();
		Location from = e.getFrom().clone(), to = e.getTo().clone();
		Location loc = p.getLocation().clone(), locUnder = p.getLocation().clone().sub(0, 1, 0), locUnderUnder = p.getLocation().clone().sub(0, 2, 0);
		boolean hasOtherThanAir = loc.getBlockCheckerXZ(1.5).hasOther(Materials.AIR), hasUnderOtherThanAir = locUnder.getBlockCheckerXZ(1).hasOther(Materials.AIR),
				hasUnderUnderOtherThanAir = locUnderUnder.getBlockCheckerXZ(1).hasOther(Materials.AIR);

		double d = to.getY() - from.getY();
		if (p.getVehicle() != null && p.getVehicle().getType().equals(EntityType.BOAT)) {
			double moveY = (to.getY() - from.getY());

			boolean wasWaterBelow = from.sub(0, 1, 0).getBlock().getType().getId().contains("WATER");
			boolean willWaterBelow = to.sub(0, 1, 0).getBlock().getType().getId().contains("WATER");
			if (wasWaterBelow && !willWaterBelow)
				data.boatFalling = true;

			if (data.boatFalling && !willWaterBelow && moveY >= 0)
				data.boatFalling = false;
		} else {
			data.boatFalling = false;
		}
		if (!np.isUsingSlimeBlock && !hasOtherThanAir && !hasUnderOtherThanAir && !data.boatFalling && !hasUnderUnderOtherThanAir && d != 0.5 && d != 0
				&& (from.getY() <= to.getY() || p.isInBoat()) && p.getVelocity().length() < d) {
			if (data.nbAirBelow > 6) { // we don't care when player jump
				int nb = LocationUtils.getNbAirBlockDown(p), porcent = parseInPorcent(nb * 15 + d);
				if (p.getLocation().add(0, -3, 0).getBlockChecker(1, 0, 1).hasOther(Materials.AIR))
					porcent = parseInPorcent(porcent - 15);
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, porcent, "no-ground-down",
						"Not ground (" + nb + " down), disY: " + d + ", vel: " + p.getVelocity() + ", fd: " + p.getFallDistance() + ", nbTime: " + data.nbAirBelow,
						hoverMsg(p.isInBoat() ? "boat_air_below" : "air_below", "%nb%", nb));
				if (mayCancel && isSetBack())
					LocationUtils.teleportPlayerOnGround(p);
			}
			data.nbAirBelow++;
		} else
			data.nbAirBelow = 0;
	}

	@Check(name = "omega-craft", description = "Check when player keep their Y move", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_FIGHT, CheckConditions.NO_USE_TRIDENT,
			CheckConditions.NO_FLY, CheckConditions.NO_SWIM, CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NO_CLIMB_BLOCK })
	public void omegaCraft(PlayerMoveEvent e, NegativityPlayer np, FlyData data) {
		Player p = e.getPlayer();
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;

		Location from = e.getFrom(), to = e.getTo();

		Location loc = p.getLocation().clone(), locUnder = p.getLocation().clone().sub(0, 1, 0);
		Block blockUnder = locUnder.getBlock();
		boolean isInWater = loc.getBlock().getType().getId().contains("WATER"), isOnWater = blockUnder.getType().getId().contains("WATER");

		double d = to.getY() - from.getY();
		if (d == 0) {
			for (Block b : p.getBoundingBox().move(0, 0.9, 0).getBlocks(p.getWorld()).getBlocks()) {
				if (b.getType().isSolid()) {
					data.flyMove.clear();
					return;
				}
			}
		}

		boolean onGround = p.isOnGround();
		if (p.getFallDistance() <= 0.000001 && !p.isInsideVehicle() && onGround == data.wasOnGround) {
			double i = to.toVector().distance(from.toVector());
			int amount = 0;
			synchronized (data.flyMove) {
				int size = data.flyMove.size();
				if (size > 1) {
					for (int x = 1; x < size - 1; x++) {
						double last = data.flyMove.get(x - 1);
						double current = data.flyMove.get(x);
						if ((last + current) == 0) {
							if (i < (size - 2)) {
								double next = data.flyMove.get(x + 1);
								if ((current + next) == 0) {
									amount++;
								}
							} else
								amount++;
						}
					}
				}
			}
			amount /= 2;
			if (amount > 1 && i > 0.01) {
				if (Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent((np.isBedrockPlayer() ? 70 : 85) + amount), "omega-craft",
						data.flyMove + " > " + onGround + " : " + data.wasOnGround + ", i: " + i + ", d: " + d + ", under: " + blockUnder.getType().getId(),
						new CheatHover.Literal("OmegaCraft: " + amount + " times with no Y changes"), amount > 1 ? amount - 1 : 1) && isSetBack())
					LocationUtils.teleportPlayerOnGround(p);
			}
		}
		if ((onGround && data.wasOnGround) || (d > 0.1 || d < -0.1) || LocationUtils.hasBoatAroundHim(p.getWorld(), e.getTo()) || p.isInsideVehicle() || !e.getTo().clone().add(0, 2, 0).getBlock().getType().isTransparent()
				|| isInWater || isOnWater || e.getTo().getBlockChecker(1.5).has("FENCE", "SLIME", "LILY", "VINE", "STAIRS", "BED", "WEB", "SNOW", "COBWEB"))
			data.flyMove.clear();
		else
			data.flyMove.add(d);

		data.wasOnGround = onGround;
	}

	@Check(name = "ground-checker", description = "Check for ground on no-ground packet", conditions = { CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NO_FLY,
			CheckConditions.NO_USE_SLIME, CheckConditions.NO_CLIMB_BLOCK, CheckConditions.SURVIVAL, CheckConditions.NO_MID_ENTITY_AROUND, CheckConditions.NO_LIQUID_AROUND })
	public void onGroundChecker(PacketReceiveEvent e, NegativityPlayer np, FlyData data) {
		Player p = e.getPlayer();
		NPacket packet = e.getPacket();
		if (packet.getPacketType().equals(PacketType.Client.POSITION) || packet.getPacketType().equals(PacketType.Client.POSITION_LOOK)) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) packet;

			boolean positionGround = Maths.isOnGround(flying.getY());

			if (positionGround != flying.isGround) {
				if (data.groundWarn++ > 4) {
					if (Negativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.FLY), 90, "ground-checker",
							"Motion: " + positionGround + " / " + flying.isGround + ", gw: " + data.groundWarn + ", y: " + flying.getY(), null, (long) (data.groundWarn - 3)) && isSetBack())
						LocationUtils.teleportPlayerOnGround(p);
				}
			} else {
				data.groundWarn = Math.max(data.groundWarn - 0.5, 0);
			}
		}
	}

	@Check(name = "suspicious-y", description = "Suspicious Y move", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ELYTRA, CheckConditions.NO_FIGHT, CheckConditions.NO_MID_ENTITY_AROUND,
			CheckConditions.NO_INSIDE_VEHICLE })
	public void onSuspiciousY(PlayerMoveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		Location from = e.getFrom(), to = e.getTo();
		double y = from.getY() - to.getY();
		Location loc = p.getLocation().clone(), locUnder = p.getLocation().clone().sub(0, 1, 0);
		String strY = String.valueOf(y);
		if (strY.contains("E") && !strY.equalsIgnoreCase("2.9430145066276694E-4") && !loc.getBlockCheckerXZ(1).has("SCAFFOLD", "LAVA", "WATER")
				&& !(np.isBedrockPlayer() && locUnder.getBlockCheckerXZ(1).has("SLAB", "FENCE", "STAIRS", "BED"))) {
			int eY = (int) Math.abs(Double.parseDouble(String.valueOf(y).split("E")[0]));
			boolean mayCancel = Negativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(120 - (eY * eY * eY)),
					"suspicious-y", "Suspicious Y: " + y);
			if (mayCancel && isSetBack())
				LocationUtils.teleportPlayerOnGround(p);
		}
	}

	@Check(name = "no-ground-y", description = "When not in ground, check y", conditions = { CheckConditions.NO_ELYTRA, CheckConditions.NO_MID_ENTITY_AROUND, CheckConditions.NO_INSIDE_VEHICLE,
			CheckConditions.SURVIVAL, CheckConditions.NO_FLY })
	public void onGroundY(PlayerMoveEvent e, NegativityPlayer np, FlyData data) {
		Player p = e.getPlayer();
		Location from = e.getFrom(), to = e.getTo();
		double i = to.toVector().distance(from.toVector());
		double distanceWithoutY = to.distanceXZ(from);
		if (distanceWithoutY == i && !p.isOnGround() && i != 0 && distanceWithoutY > 0.3 && p.getVelocity().length() < 0.5) {
			if (data.notMovingY > 1) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, 98, "no-ground-y",
						"Player not in ground but not moving Y. DistanceWithoutY: " + distanceWithoutY + ", vel: " + p.getVelocity());
				if (mayCancel && isSetBack())
					LocationUtils.teleportPlayerOnGround(p);
			}
			data.notMovingY++;
		} else
			data.notMovingY = 0;
	}

	@Check(name = "not-moving-y", description = "When not moving Y", conditions = { CheckConditions.NO_ELYTRA, CheckConditions.NO_MID_ENTITY_AROUND, CheckConditions.NO_INSIDE_VEHICLE,
			CheckConditions.NO_FLY, CheckConditions.SURVIVAL })
	public void onNotMovingY(PacketReceiveEvent e, NegativityPlayer np, FlyData data) {
		Player p = e.getPlayer();

		NPacket packet = e.getPacket();
		if (!packet.getPacketType().isFlyingPacket())
			return;
		NPacketPlayInFlying flying = (NPacketPlayInFlying) packet;

		BlockChecker blocksAround = p.getBoundingBox().expand(0.5, 0.5, 0.5).getBlocks(p.getWorld());
		if (blocksAround.has("WATER", "LAVA")) {
			data.notMovingY = 0;
			if (flying.hasLocation())
				data.lastY = flying.y;
			return;
		}

		if (flying.isGround) {
			if(flying.hasLocation()) {
				Location from = p.getLocation(), to = flying.getLocation(p.getWorld());
	
				double y = from.getY() - to.getY();
				Material type = from.getBlock().getType();
	
				double distance = from.distance(to);
				if (y == 0 && !type.isSolid() && !blocksAround.hasOther(Materials.AIR)) {
					if (++data.notMovingY > 2) {
						boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(data.notMovingY * 30), "not-moving-y",
								"Not moving Y: " + data.notMovingY + ", distance: " + distance + ", ws: " + p.getWalkSpeed() + ", fd: " + p.getFallDistance(), null, data.notMovingY < 3 ? 1 : data.notMovingY - 2);
						if (mayCancel && isSetBack())
							LocationUtils.teleportPlayerOnGround(p);
					}
				} else
					data.notMovingY = 0;
			}
		} else {
			if ((data.lastY == flying.y) && p.getTheoricVelocity().length() < 0.1) {
				if (++data.notMovingY > 2) {
					Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(90 + data.notMovingY), "not-moving-y",
							"Last Y: " + data.lastY + ", threshold: " + data.notMovingY, null, data.notMovingY);
				}
			} else
				data.notMovingY = 0;
		}
		if (flying.hasLocation())
			data.lastY = flying.y;
	}

	@Check(name = "no-ground-i", description = "When not moving Y", conditions = { CheckConditions.NO_ELYTRA, CheckConditions.NO_MID_ENTITY_AROUND, CheckConditions.NO_INSIDE_VEHICLE,
			CheckConditions.NO_SPRINT, CheckConditions.NO_ALLOW_FLY })
	public void notGroundI(PlayerMoveEvent e, NegativityPlayer np, FlyData data) {
		Player p = e.getPlayer();

		Location from = e.getFrom(), to = e.getTo();

		double i = to.toVector().distance(from.toVector());
		double d = to.getY() - from.getY();
		Location loc = p.getLocation().clone(), locUnder = p.getLocation().clone().sub(0, 1, 0), locUnderUnder = p.getLocation().clone().sub(0, 2, 0);
		Material typeUpper = loc.getBlock().getRelative(BlockFace.UP).getType();
		boolean inBoat = p.isInBoat();
		if (d > 0 && (i < p.getVelocity().getY() || p.getVelocity().length() < 0.5) && p.getVelocity().length() < 3 && locUnder.getBlock().getType().equals(Materials.AIR)
				&& locUnderUnder.getBlock().getType().equals(Materials.AIR) && (p.getFallDistance() == 0.0F || inBoat) && typeUpper.equals(Materials.AIR) && i > 0.8 && !p.isOnGround()) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent((int) i * 50), "no-ground-i",
					"Not ground, i: " + String.format("%.10f", i) + ", boat: " + inBoat + ", d: " + String.format("%.10f", d) + ", vel: " + p.getVelocity(),
					inBoat ? hoverMsg("boat") : null);
			if (mayCancel && isSetBack())
				LocationUtils.teleportPlayerOnGround(p);
		}
	}
}
