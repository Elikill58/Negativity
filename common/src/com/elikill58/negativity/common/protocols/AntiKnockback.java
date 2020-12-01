package com.elikill58.negativity.common.protocols;

import java.util.TimerTask;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.Arrow;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.events.player.PlayerDamageByEntityEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.maths.Expression;
import com.elikill58.negativity.api.packets.PacketContent.ContentModifier;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.support.WorldGuardSupport;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class AntiKnockback extends Cheat implements Listeners {

	public static final DataType<Double> DISTANCE_DAMAGE = new DataType<Double>("distance_damage",
			"Distance after Damage", () -> new DoubleDataCounter());

	public AntiKnockback() {
		super(CheatKeys.ANTI_KNOCKBACK, CheatCategory.COMBAT, Materials.STICK, false, true, "antikb", "anti-kb",
				"no-kb", "nokb");
	}

	@EventListener
	public void onDamage(PlayerDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		Player p = e.getEntity();

		if (p.isInsideVehicle()) {
			// Knockback is not applied to entities riding other entities
			return;
		}

		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || !checkActive("ticked"))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (p.hasPotionEffect(PotionEffectType.POISON) || Utils.hasThorns(p))
			return;
		if (Version.getVersion().isNewerOrEquals(Version.V1_9)) {
			ItemStack inHand = p.getItemInHand();
			if (inHand != null && inHand.getType().getId().contains("SHIELD"))
				return;
			ItemStack inOffHand = p.getItemInOffHand();
			if (inOffHand != null && inOffHand.getType().getId().contains("SHIELD"))
				return;
		}
		EntityType damagerType = e.getDamager().getType();
		if (damagerType.equals(EntityType.EGG) || damagerType.equals(EntityType.SNOWBALL)
				|| (Negativity.worldGuardSupport && WorldGuardSupport.isInRegionProtected(p)))
			return;
		if (damagerType.name().contains("TNT") || np.isTargetByIronGolem())
			return;
		if (Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;
		final Entity damager = e.getDamager();
		if (damager.getType().equals(EntityType.ARROW) && ((Arrow) damager).getShooter() instanceof Player)
			if (((Player) ((Arrow) damager).getShooter()).equals(p))
				return;

		new java.util.Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				if (e.isCancelled())
					return;
				final Location last = p.getLocation().clone();
				p.damage(0D);
				// p.setLastDamageCause(new EntityDamageEvent(p, DamageCause.CUSTOM, 0D));
				new java.util.Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						Location actual = p.getLocation();
						if (last.getWorld() != actual.getWorld() || p.isDead())
							return;
						double d = last.distance(actual);
						recordData(p.getUniqueId(), DISTANCE_DAMAGE, d);
						int relia = UniversalUtils.parseInPorcent(100 - d);
						if (d < 0.1 && !actual.getBlock().getType().equals(Materials.WEB) && !p.isSneaking()) {
							boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, AntiKnockback.this, relia,
									"ticked",
									"Distance after damage: " + d + "; Damager: "
											+ e.getDamager().getType().name().toLowerCase(),
									hoverMsg("main", "%distance%", d));
							if (isSetBack() && mayCancel)
								p.setVelocity(p.getVelocity().add(new Vector(0, 1, 0)));
						}
					}
				}, 250);
			}
		}, 50);
	}

	@EventListener
	public void onPacket(PacketSendEvent e) {
		if (!e.getPacket().getPacketType().equals(PacketType.Server.ENTITY_VELOCITY))
			return;
		if (!checkActive("packet"))
			return;
		ContentModifier<Integer> ints = e.getPacket().getContent().getIntegers();
		int entId = ints.read("a", -1);
		int velY = ints.read("c", -1);

		Adapter ada = Adapter.getAdapter();
		if (entId == -1 || velY == -1) {
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
			if (p.getEntityId() == entId) {
				NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
				if (!np.hasDetectionActive(this))
					return;

				if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
					return;
				if (!p.isOnGround() || np.isOnLadders || p.isInsideVehicle() || p.isFlying() || p.isDead())
					return;
				ada.runSync(() -> checkPlayerForVectorPacketAntiKb(p, velY, algo));
				return;
			}
		}
	}

	private void checkPlayerForVectorPacketAntiKb(Player p, int velY, String algo) {
		Adapter ada = Adapter.getAdapter();
		// don't check if there is a ceiling or anything that could block from taking kb
		if (hasAntiKbBypass(p)) {
			ada.debug("AntiKb detection: " + p.getName() + " has bypass.");
			return;
		}

		final int ticksToReact = (int) (1 * 20);// seconds for the client to get up

		if (velY < 5000) {
			// give client some time to react
			new java.util.Timer().schedule(new TimerTask() {
				public int iterations = 0;
				public double reachedY = 0 /* diff reached */, baseY = p.getLocation().getY();
				/*public Vector baseVector = p.getVelocity().clone();
				public Location basLoc = p.getLocation().clone();
				public boolean vectorChanged = false;*/

				@Override
				public void run() {
					iterations++;
					Location loc = p.getLocation();
					if (loc.getY() - baseY > reachedY)
						reachedY = loc.getY() - baseY;
					/*if (checkActive("vector")) {
						if (iterations <= 5) {
							double d = baseVector.distance(p.getVelocity());
							if (d != 0)
								vectorChanged = true;
							ada.debug("KB Distance: " + d);
						} else if (!vectorChanged && loc.distance(basLoc) > 0.3) {
							Negativity.alertMod(ReportType.WARNING, p, AntiKnockback.this, 90 + iterations, "vector",
									"No changes for the " + iterations + " times. Vector: " + baseVector.toString(),
									new CheatHover.Literal(
											"No direction changes during " + (((double) iterations) / 20) + " second"));

						}
					}*/
					if (iterations > ticksToReact) {
						// default algo : (0.00000008 * velY * velY) + (0.0001 * velY) - 0.0219
						double predictedY = new Expression(algo.replaceAll("velY", String.valueOf(velY)).replaceAll("reachedY", String.valueOf(reachedY))).calculate();
						double percentage = Math.abs(((reachedY - predictedY) / predictedY));
						if (predictedY > reachedY && percentage > 50) {
							Negativity.alertMod(ReportType.WARNING, p, AntiKnockback.this,
									UniversalUtils.parseInPorcent(percentage), "packet",
									"ReachedY: " + reachedY + ", predictedY: " + predictedY + ", percentage: "
											+ percentage + ", algo: " + algo + ".",
									new CheatHover.Literal("Reached Y too different from predicted Y"));
						} else
							ada.debug("AntiKb detection: prediction: " + predictedY + ", percentage: " + percentage
									+ ", reachedY: " + reachedY);
						cancel();
					}
				}
			}, 1000 / 20, 1000 / 20);
		}
	}

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> counter = data.getData(DISTANCE_DAMAGE);
		double av = counter.getAverage(), low = counter.getMin();
		String colorAverage = (av < 1 ? (av < 0.5 ? "&c" : "&6") : "&a");
		String colorLow = (low < 1 ? (low < 0.5 ? "&c" : "&6") : "&a");
		return Utils.coloredMessage("&6Distance after damage: &7Average: " + colorAverage + String.format("%.2f", av)
				+ "&7, Lower: " + colorLow + String.format("%.2f", low) + " &7(In " + counter.getSize() + " hits)");
	}

	public static boolean hasAntiKbBypass(Player p) {
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
		Location loc = player.getLocation().clone().add(0, 2, 0);
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
