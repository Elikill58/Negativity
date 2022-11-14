package com.elikill58.negativity.common.protocols;

import java.util.function.Consumer;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.maths.Expression;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.common.protocols.data.EmptyData;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class AntiKnockback extends Cheat {

	public static final DataType<Double> DISTANCE_DAMAGE = new DataType<Double>("distance_damage",
			"Distance after Damage", () -> new DoubleDataCounter());

	public AntiKnockback() {
		super(CheatKeys.ANTI_KNOCKBACK, CheatCategory.COMBAT, Materials.STICK, EmptyData::new, CheatDescription.VERIF);
	}

	@Check(name = "packet", description = "Packet velocity", conditions = { CheckConditions.SURVIVAL })
	public void onPacket(PacketSendEvent e) {
		if (!e.getPacket().getPacketType().equals(PacketType.Server.ENTITY_VELOCITY) || checkActive("packet"))
			return;
		NPacketPlayOutEntityVelocity packet = (NPacketPlayOutEntityVelocity) e.getPacket();
		int entId = packet.entityId;
		double velY = packet.vec.getY();

		Adapter ada = Adapter.getAdapter();
		if (entId == -1 || velY == -1) {
			if(entId == -1)
				ada.debug("The AntiKnockback is disabled because the entity ID is " + entId + " and the velocity is " + velY
					+ " for EntityVelocity.");
			return;
		}

		String algo = getConfig().getString("checks.packet.algo");
		if (algo.equalsIgnoreCase("0"))
			return;

		// search for player
		for (Player p : ada.getOnlinePlayers()) {

			// found player
			if (p.isSameId(String.valueOf(entId))) {
				NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
				if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
					return;
				if (!p.isOnGround() || np.isOnLadders || p.isInsideVehicle() || p.isFlying() || p.isDead())
					return;
				ada.runSync(() -> checkPlayerForVectorPacketAntiKb(p, velY, algo));
				return;
			}
		}
	}

	private void checkPlayerForVectorPacketAntiKb(Player p, double velY, String algo) {
		Adapter ada = Adapter.getAdapter();
		// don't check if there is a ceiling or anything that could block from taking kb
		if (hasAntiKbBypass(p)) {
			ada.debug("AntiKb detection: " + p.getName() + " has bypass.");
			return;
		}

		final int ticksToReact = 20;// seconds for the client to get up

		if (velY < 5000) {
			// give client some time to react
			Scheduler.getInstance().runRepeating(new Consumer<ScheduledTask>() {
				public int iterations = 0;
				public double reachedY = 0 /* diff reached */, baseY = p.getLocation().getY();
				/*public Vector baseVector = p.getVelocity().clone();
				public Location basLoc = p.getLocation().clone();
				public boolean vectorChanged = false;*/

				@Override
				public void accept(ScheduledTask task) {
					iterations++;
					Location loc = p.getLocation();
					if (loc.getY() - baseY > reachedY)
						reachedY = loc.getY() - baseY;
					/*
					This detection is in WIP. Why ?
					
					It's bad when people don't move. I think it can be usefull, but actually it's not
					 
					if (checkActive("vector")) {
						if (iterations <= 5) {
							double d = baseVector.distance(p.getVelocity());
							if (d != 0)
								vectorChanged = true;
							ada.debug("KB Distance: " + String.format("%.3f", d));
						} else if (loc.distance(basLoc) > (iterations * p.getWalkSpeed()) && p.getVelocity().length() > 0.09) {
							Negativity.alertMod(ReportType.WARNING, p, AntiKnockback.this, UniversalUtils.parseInPorcent((vectorChanged ? 70 : 90) + iterations), "vector",
									"No changes " + iterations + " times. Vector: " + baseVector.toString() + ", velocity: " + p.getVelocity() + ", dis: " + loc.distance(basLoc) + ", ws: " + p.getWalkSpeed(),
									new CheatHover.Literal(
											"No direction changes during " + (((double) iterations) / 20) + " second"));
						} else
							ada.debug("Vector: " + String.format("%.3f", loc.distance(basLoc)) + ", " + (iterations * p.getWalkSpeed()));
					}*/
					if (iterations > ticksToReact && p.getVelocity().equals(p.getTheoricVelocity())) { // wait until the person take the kb
						// default algo : (0.00000008 * velY * velY) + (0.0001 * velY) - 0.0219
						double predictedY = new Expression(algo.replaceAll("velY", String.valueOf(velY)).replaceAll("reachedY", String.valueOf(reachedY))).calculate();
						double percentage = Math.abs(((reachedY - predictedY) / predictedY)) * 100;
						if (predictedY > reachedY && percentage > 50) {
							Negativity.alertMod(ReportType.WARNING, p, AntiKnockback.this,
								UniversalUtils.parseInPorcent(percentage), "packet",
								"ReachedY: " + reachedY + ", predictedY: " + predictedY + ", percentage: "
									+ percentage,
								new CheatHover.Literal("Reached Y too different from predicted Y"));
						} else
							ada.debug("AntiKb detection: prediction: " + predictedY + ", percentage: " + percentage
								+ ", reachedY: " + reachedY);
						task.cancel();
					}
				}
			}, 1, 1);
		}
	}

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> counter = data.getData(DISTANCE_DAMAGE);
		double av = counter.getAverage(), low = counter.getMin();
		String colorAverage = (av < 1 ? (av < 0.5 ? "&c" : "&6") : "&a");
		String colorLow = (low < 1 ? (low < 0.5 ? "&c" : "&6") : "&a");
		return ChatColor.color("&6Distance after damage: &7Average: " + colorAverage + String.format("%.2f", av)
				+ "&7, Lower: " + colorLow + String.format("%.2f", low) + " &7(In " + counter.getSize() + " hits)");
	}

	public static boolean hasAntiKbBypass(Player p) {
		for(ItemStack item : p.getInventory().getArmorContent())
			if(item != null && item.getType().getId().contains("NETHERITE"))
				return true;
		return isInWater(p.getLocation()) || isInWeb(p.getLocation()) || hasCeiling(p);
	}

	public static boolean isInWater(Location loc) {
		return loc.getBlock().isLiquid() || loc.clone().add(0, -1, 0).getBlock().isLiquid()
				|| loc.clone().add(0, 1, 0).getBlock().isLiquid();
	}

	public static boolean isInWeb(Location loc) {
		return isInWebForLocation(loc) || isInWebForLocation(loc.clone().add(0, 1, 0));
	}

	private static boolean isInWebForLocation(Location loc) {
		double x = loc.getX() - loc.getBlockX(), z = loc.getZ() - loc.getBlockZ();

		if (isWeb(loc.getBlock()))
			return true;
		else if (x < 0.31 && isWeb(loc.getBlock().getRelative(BlockFace.WEST)))
			return true;
		else if (x > 0.69 && isWeb(loc.getBlock().getRelative(BlockFace.EAST)))
			return true;
		else if (z < 0.31 && isWeb(loc.getBlock().getRelative(BlockFace.NORTH)))
			return true;
		else if (z > 0.69 && isWeb(loc.getBlock().getRelative(BlockFace.SOUTH)))
			return true;
		else if (x > 0.71 && z < 0.3 && isWeb(loc.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH)))
			return true;
		else if (x > 0.71 && z > 0.71 && isWeb(loc.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH)))
			return true;
		else if (x < 0.31 && z > 0.71 && isWeb(loc.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH)))
			return true;
		else if (x < 0.31 && z < 0.31 && isWeb(loc.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH)))
			return true;
		return false;
	}

	private static boolean isWeb(Block b) {
		return b.getType().equals(Materials.WEB);
	}

	public static boolean hasCeiling(Player player) {
		return hasCeilingForLoc(player, player.getLocation().clone().add(0, 2, 0))/*
				|| hasCeilingForLoc(player, player.getLocation().clone().add(0, 1, 0))
				|| hasCeilingForLoc(player, player.getLocation())*/;
	}

	private static boolean hasCeilingForLoc(Player player, Location loc) {
		if (loc.getBlock().getType().isSolid())
			return true;
		else if (loc.getX() > 0.66 && loc.getBlock().getRelative(BlockFace.EAST).getType().isSolid())
			return true;
		else if (loc.getX() < -0.66 && loc.getBlock().getRelative(BlockFace.WEST).getType().isSolid())
			return true;
		else if (loc.getZ() > 0.66 && loc.getBlock().getRelative(BlockFace.SOUTH).getType().isSolid())
			return true;
		else if (loc.getZ() < -0.66 && loc.getBlock().getRelative(BlockFace.NORTH).getType().isSolid())
			return true;
		return false;
	}
}
