package com.elikill58.negativity.spigot.protocols;

import static com.elikill58.negativity.spigot.utils.LocationUtils.hasOtherThanExtended;
import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.NegativityPlayerMoveEvent;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.spigot.utils.LocationUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FlyProtocol extends Cheat implements Listener {

	public FlyProtocol() {
		super(CheatKeys.FLY, true, ItemUtils.FIREWORK, CheatCategory.MOVEMENT, true, "flyhack");
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(NegativityPlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = e.getNegativityPlayer();
		if (!np.hasDetectionActive(this) || e.isCancelled())
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (np.hasElytra() || p.getItemInHand().getType().name().contains("TRIDENT") || LocationUtils.hasMaterialAround(e.getTo(), ItemUtils.WATER_LILY, ItemUtils.WEB, Material.LADDER, Material.VINE))
			return;
		if(Version.getVersion().isNewerOrEquals(Version.V1_9) && p.hasPotionEffect(PotionEffectType.LEVITATION))
			return;

		if (p.hasPotionEffect(PotionEffectType.SPEED)) {
			if (Utils.getPotionEffect(p, PotionEffectType.SPEED).getAmplifier() > 5)
				return;
		}
		if (p.getAllowFlight() || p.getEntityId() == 100 || Utils.isSwimming(p))
			return;
		boolean mayCancel = false, inBoat = Utils.isInBoat(p);
		double y = e.getFrom().getY() - e.getTo().getY();
		Location loc = p.getLocation().clone(),
				locUnder = p.getLocation().clone().subtract(0, 1, 0),
				locUnderUnder = p.getLocation().clone().subtract(0, 2, 0);
		Material type = loc.getBlock().getType(), typeUpper = loc.getBlock().getRelative(BlockFace.UP).getType();
		boolean hasBuggedBlockAroundForGeyser = np.isBedrockPlayer() && LocationUtils.hasMaterialsAround(locUnder, "SLAB", "FENCE", "STAIRS", "BED");
		boolean isInWater = loc.getBlock().getType().name().contains("WATER"), isOnWater = locUnder.getBlock().getType().name().contains("WATER");
		if(String.valueOf(y).contains("E") && !String.valueOf(y).equalsIgnoreCase("2.9430145066276694E-4") && !p.isInsideVehicle() && !inBoat && !hasBuggedBlockAroundForGeyser
				&& !np.isInFight && !LocationUtils.hasBoatAroundHim(p.getLocation()) && !(isInWater || isOnWater) && !LocationUtils.hasMaterialsAround(loc, "SCAFFOLD")){
			int eY = (int) Math.abs(Double.parseDouble(String.valueOf(y).split("E")[0]));
			mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, UniversalUtils.parseInPorcent(120 - (eY * eY * eY)), "Suspicious Y: " + y);
		}
		double i = e.getTo().toVector().distance(e.getFrom().toVector());
		double d = e.getTo().getY() - e.getFrom().getY();
		if (!(p.isSprinting() && d > 0)
				&& locUnder.getBlock().getType().equals(Material.AIR)
				&& locUnderUnder.getBlock().getType().equals(Material.AIR)
				&& (p.getFallDistance() == 0.0F || inBoat)
				&& typeUpper.equals(Material.AIR) && i > 0.8
				&& !np.isOnGround()) {
			mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
					this, parseInPorcent((int) i * 50),
					"Player not in ground, i: " + i + ". Warn for fly: " + np.getWarn(this),
					inBoat ? hoverMsg("boat") : null);
		}

		if (!np.isUsingSlimeBlock && !hasOtherThanExtended(p.getLocation(), "AIR")
				&& !hasOtherThanExtended(locUnder, "AIR") && !np.contentBoolean.getOrDefault("boat-falling", false)
				&& !hasOtherThanExtended(locUnderUnder, "AIR") && d != 0.5 && d != 0 && !np.contentBoolean.getOrDefault("jump-boost-use", false)
				&& (e.getFrom().getY() <= e.getTo().getY()) && p.getVelocity().length() < 1.5) {
			if(!(p.hasPotionEffect(PotionEffectType.JUMP) && Utils.getPotionEffect(p, PotionEffectType.JUMP).getAmplifier() > 2)) {
				double nbTimeAirBelow = np.contentDouble.getOrDefault("fly-air-below", 0.0);
				np.contentDouble.put("fly-air-below", nbTimeAirBelow + 1);
				if(nbTimeAirBelow > 6) { // we don't care when player jump
					int nb = LocationUtils.getNbAirBlockDown(p), porcent = parseInPorcent(nb * 15 + d);
					if (nb < 5)
						porcent = parseInPorcent(porcent - 15);
					mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING, p,
							this, porcent, "Player not in ground (" + nb + " air blocks down), distance Y: " + d + ", inBoat: " + inBoat
									+ ". Warn for fly: " + np.getWarn(this),
									hoverMsg(inBoat ? "boat_air_below" : "air_below", "%nb%", nb));
				}
			}
		} else
			np.contentDouble.remove("fly-air-below");
		
		Location to = e.getTo().clone();
		to.setY(e.getFrom().getY());
		double distanceWithoutY = to.distance(e.getFrom());
		if (distanceWithoutY == i && !np.isOnGround() && i != 0
				&& typeUpper.equals(Material.AIR) && !p.isInsideVehicle()
				&& !type.name().contains("WATER") && distanceWithoutY > 0.3) {
			if (np.contentBoolean.getOrDefault("fly-not-moving-y", false))
				mayCancel = SpigotNegativity.alertMod(np.getWarn(this) > 5 ? ReportType.VIOLATION : ReportType.WARNING,
						p, this, 98, "Player not in ground but not moving Y. DistanceWithoutY: " + distanceWithoutY);
			np.contentBoolean.put("fly-not-moving-y", true);
		} else
			np.contentBoolean.put("fly-not-moving-y", false);
		

		boolean onGround = ((Entity) p).isOnGround(), wasOnGround = np.contentBoolean.getOrDefault("fly-wasOnGround", true);
		boolean hasBoatAround = p.getWorld().getNearbyEntities(loc, 3, 3, 3).stream().filter((entity) -> entity instanceof Boat).findFirst().isPresent();
		if(p.getFallDistance() <= 0.000001 && np.flyMoveAmount.size() > 1 && !p.isInsideVehicle()) {
			int size = np.flyMoveAmount.size();
			int amount = 0;
			for(int x = 1; x < size - 1; x++) {
				double last = np.flyMoveAmount.get(x - 1);
				double current = np.flyMoveAmount.get(x);
				if((last + current) == 0) {
					if(i < (size - 2)) {
						double next = np.flyMoveAmount.get(x + 1);
						if((current + next) == 0) {
							amount++;
						}
					} else
						amount++;
				}
			}
			if(amount > 0) {
				SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(90 + amount), "OmegaCraftFly - " + np.flyMoveAmount.size() + " > " + onGround + " : " + wasOnGround + ", d: " + d, (CheatHover) null, amount > 1 ? amount - 1 : 1);
			}
		}
		if((onGround && wasOnGround) || (d > 0.1 || d < -0.1) || LocationUtils.hasMaterialsAround(e.getTo(), "FENCE", "SLIME", "LILY") || LocationUtils.hasMaterialsAround(locUnder, "FENCE", "SLIME", "LILY", "VINE") || hasBoatAround)
			np.flyMoveAmount.clear();
		else
			np.flyMoveAmount.add(d);
		np.contentBoolean.put("fly-wasOnGround", onGround);
		
		if (isSetBack() && mayCancel) {
			Utils.teleportPlayerOnGround(p);
		}
	}
	

	/*@EventHandler
	public void onMove(NegativityPlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = e.getNegativityPlayer();
		if (!np.hasDetectionActive(this) || e.isCancelled())
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(np.isUsingSlimeBlock || p.isInsideVehicle() || np.hasElytra() || LocationUtils.hasMaterialAround(e.getTo(), ItemUtils.WATER_LILY, ItemUtils.WEB))
			return;
		if (p.getAllowFlight() || p.getEntityId() == 100 || Utils.isSwimming(p))
			return;
		boolean onGround = ((Entity) p).isOnGround(), wasOnGround = np.contentBoolean.getOrDefault("fly-wasOnGround", true);
		double y = e.getTo().getY() - e.getFrom().getY();
		List<Double> list = np.flyMoveAmount;
		if(p.getFallDistance() <= 0.000001 && list.size() > 1) {
			int size = list.size();
			int amount = 0;
			for(int i = 1; i < size - 1; i++) {
				double last = list.get(i - 1);
				double current = list.get(i);
				if((last + current) == 0) {
					if(i < (size - 2)) {
						double next = list.get(i + 1);
						if((current + next) == 0) {
							amount++;
						}
					} else
						amount++;
				}
			}
			if(amount > 0) {
				SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(90 + amount), "OmegaCraftFly - " + list.size() + " > " + onGround + " : " + wasOnGround, (CheatHover) null, amount > 1 ? amount - 1 : 1);
			}
		}
		if((onGround && wasOnGround) || (y > 0.1 || y < -0.1) || LocationUtils.hasMaterialsAround(e.getTo(), "FENCE", "SLIME", "LILY"))
			list.clear();
		else
			list.add(y);
		np.contentBoolean.put("fly-wasOnGround", onGround);
	}*/
	
	@EventHandler
	public void boatManager(NegativityPlayerMoveEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = e.getNegativityPlayer();
		boolean nextValue = np.contentBoolean.getOrDefault("boat-falling", false);
		if(p.isInsideVehicle() && p.getVehicle().getType().equals(EntityType.BOAT)) {
			Location from = e.getFrom().clone(), to = e.getTo().clone();
			double moveY = (to.getY() - from.getY());
			
			boolean wasWaterBelow = from.subtract(0, 1, 0).getBlock().getType().name().contains("WATER");
			boolean willWaterBelow = to.subtract(0, 1, 0).getBlock().getType().name().contains("WATER");
			if(wasWaterBelow && !willWaterBelow)
				nextValue = true;
			
			if(nextValue && !willWaterBelow && moveY >= 0)
				nextValue = false;
		} else {
			if(!nextValue)
				return; // already set to false, don't need to save it while put it in map
			nextValue = false;
		}
		
		np.contentBoolean.put("boat-falling", nextValue);
	}

	@Override
	public boolean isBlockedInFight() {
		return true;
	}
}
