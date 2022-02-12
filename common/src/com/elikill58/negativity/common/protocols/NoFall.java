package com.elikill58.negativity.common.protocols;

import java.util.Arrays;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.LocationUtils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.playerModifications.PlayerModificationsManager;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class NoFall extends Cheat implements Listeners {

	public NoFall() {
		super(CheatKeys.NO_FALL, CheatCategory.MOVEMENT, Materials.YELLOW_WOOL, true, false);
	}
	
	@Check(name = "motion-y", description = "Motion Y when fall", conditions = { CheckConditions.NO_USE_ELEVATOR, CheckConditions.SURVIVAL,
			CheckConditions.NO_ALLOW_FLY, CheckConditions.NO_ELYTRA, CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NO_LIQUID_AROUND })
	public void onMoveMotionY(PlayerMoveEvent e, NegativityPlayer np) {
		if(e.isCancelled())
			return;
		Player p = e.getPlayer();
		if(p.hasPotionEffect(PotionEffectType.SPEED))
			return;
		if(Version.getVersion().isNewerOrEquals(Version.V1_13) && p.hasPotionEffect(PotionEffectType.SLOW_FALLING))
			return;
		Location from = e.getFrom(), to = e.getTo();
		Block b = p.getLocation().getBlock();
		Location locDown = b.getRelative(BlockFace.DOWN).getLocation();
		Location locUp = b.getRelative(BlockFace.UP).getLocation();
		double motionY = from.getY() - to.getY();
		if(p.isOnGround() && to.clone().add(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()).getBlock().getType().equals(Materials.AIR)
				&& locDown.getBlock().getType().equals(Materials.AIR) && locUp.getBlock().getType().equals(Materials.AIR)
				&& !LocationUtils.hasMaterialsAround(locDown, "STAIRS", "SCAFFOLD", "SLAB", "HONEY_BLOCK") && !np.isInFight && !b.isWaterLogged()
				&& ((motionY > p.getWalkSpeed() && p.getFallDistance() == 0) || motionY > (p.getWalkSpeed() / 2)) && p.getFallDistance() > 0.2 && p.getWalkSpeed() > p.getFallDistance()) {
			if (locUp.getBlock().getType().getId().contains("WATER") || LocationUtils.isUsingElevator(p))
				np.useAntiNoFallSystem = true;
			if (!np.useAntiNoFallSystem) {
				int porcent = UniversalUtils.parseInPorcent(900 * motionY);
				Negativity.alertMod(ReportType.WARNING, p, this, porcent, "motion-y", "New NoFall - Player on ground. motionY: " + motionY + ", walkSpeed: " + p.getWalkSpeed()
						+ ", onGround: " + p.isOnGround() + ", fallDistance: " + p.getFallDistance(), new Cheat.CheatHover.Literal("MotionY (on ground): " + motionY));
			}
		} else if(motionY < 0.1)
			np.useAntiNoFallSystem = false;
	}
	
	@Check(name = "distance-no-ground", description = "Distance when player NOT in ground", conditions = { CheckConditions.NO_GROUND, CheckConditions.NO_FALL_DISTANCE,
			CheckConditions.SURVIVAL, CheckConditions.NO_ALLOW_FLY, CheckConditions.NO_ELYTRA, CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NO_LIQUID_AROUND })
	public void onMoveDistanceNoGround(PlayerMoveEvent e, NegativityPlayer np) {
		if (e.isCancelled())
			return;
		Player p = e.getPlayer();
		if(p.hasPotionEffect(PotionEffectType.SPEED) || !p.getLocation().clone().sub(0, 1, 0).getBlock().getType().equals(Materials.AIR))
			return;
		if(Version.getVersion().isNewerOrEquals(Version.V1_13) && p.hasPotionEffect(PotionEffectType.SLOW_FALLING))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double distance = to.toVector().distance(from.toVector());
		if(distance == 0.0D || from.getY() < to.getY())
			return;
		int relia = UniversalUtils.parseInPorcent(distance * 100);
		if (distance > 2D) {
			boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, relia, "distance-no-ground",
					"Player not in ground no fall Damage. FallDistance: " + p.getFallDistance()
							+ ", DistanceBetweenFromAndTo: " + distance);
			if(mayCancel)
				np.NO_FALL_DAMAGE += 1;
		} else if (np.NO_FALL_DAMAGE != 0) {
			if (isSetBack())
				manageDamage(p, np.NO_FALL_DAMAGE, relia);
			np.NO_FALL_DAMAGE = 0;
		}
	}

	@Check(name = "distance-ground", description = "DIstance when player in now on ground", conditions = { CheckConditions.GROUND, CheckConditions.NO_FALL_DISTANCE, CheckConditions.SURVIVAL, CheckConditions.NO_ALLOW_FLY, CheckConditions.NO_ELYTRA, CheckConditions.NO_INSIDE_VEHICLE })
	public void onMoveDistanceGround(PlayerMoveEvent e, NegativityPlayer np) {
		if (e.isCancelled())
			return;
		Player p = e.getPlayer();
		if(p.hasPotionEffect(PotionEffectType.SPEED) || !p.getLocation().clone().sub(0, 1, 0).getBlock().getType().equals(Materials.AIR))
			return;
		if(Version.getVersion().isNewerOrEquals(Version.V1_13) && p.hasPotionEffect(PotionEffectType.SLOW_FALLING))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double distance = to.toVector().distance(from.toVector());
		if(LocationUtils.hasMaterialsAround(to, "WATER") || distance == 0.0D || from.getY() < to.getY())
			return;
		Vector direction = p.getVelocity().clone();
		int relia = UniversalUtils.parseInPorcent(distance * 100);
		double distanceVector = to.toVector().clone().add(direction).distance(from.toVector());
		double disWithDirY = from.clone().add(direction).toVector().setY(0).distanceSquared(to.toVector().setY(0));
		if (distance > 0.79D && !(p.getWalkSpeed() > 0.45F && PlayerModificationsManager.isSpeedUnlocked(p))) {
			boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, relia, "distance-ground",
					"Player in ground. FallDamage: " + p.getFallDistance() + ", Distance From/To: "
							+ distance + ", Velocity Y: " + p.getVelocity().getY() + ", distanceVector: " + distanceVector + ", disDirY: " + disWithDirY);
			if(mayCancel)
				np.NO_FALL_DAMAGE += 1;
		} else if (np.NO_FALL_DAMAGE != 0) {
			if (isSetBack())
				manageDamage(p, np.NO_FALL_DAMAGE, relia);
			np.NO_FALL_DAMAGE = 0;
		}
	}
	
	@Check(name = "have-to-ground", description = "Player try to spoof ground", conditions = { CheckConditions.SURVIVAL, CheckConditions.NO_ALLOW_FLY, CheckConditions.NO_ELYTRA, CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.NO_GROUND })
	public void onMoveHaveToGround(PlayerMoveEvent e, NegativityPlayer np) {
		if (e.isCancelled())
			return;
		Player p = e.getPlayer();
		if(p.hasPotionEffect(PotionEffectType.SPEED) || p.getLocation().clone().sub(0, 1, 0).getBlock().getType().equals(Materials.AIR))
			return;
		if(Version.getVersion().isNewerOrEquals(Version.V1_13) && p.hasPotionEffect(PotionEffectType.SLOW_FALLING))
			return;
		Location from = e.getFrom(), to = e.getTo();
		double distance = to.toVector().distance(from.toVector());
		if(LocationUtils.hasMaterialsAround(to, "WATER") || distance == 0.0D || from.getY() < to.getY())
			return;
		double motionY = from.getY() - to.getY();
		Material justUnder = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
		if(justUnder.isSolid() && p.getFallDistance() > 3.0 && !np.isInFight && motionY <= 0) {
			int ping = p.getPing(), relia = UniversalUtils.parseInPorcent(100 - (ping / 5) + p.getFallDistance());
			boolean mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, relia, "have-to-ground",
					"Player not ground with fall damage (FallDistance: " + p.getFallDistance() + "). Block down: " + justUnder.getId()
							+ ", DistanceBetweenFromAndTo: " + distance);
			if(mayCancel && isSetBack())
				manageDamage(p, (int) p.getFallDistance(), relia);
		}
	}
	
	@Check(name = "packet", description = "Player send spoofing packet when risk to have fall damage", conditions = CheckConditions.SURVIVAL)
	public void onPacket(PacketReceiveEvent e, NegativityPlayer np) {
		Player p = e.getPlayer();
		AbstractPacket packet = e.getPacket();
		PacketType type = packet.getPacketType();
    	if(!type.isFlyingPacket())
    		return;
    	NPacketPlayInFlying flying = (NPacketPlayInFlying) packet.getPacket();
    	if(flying.isGround) {
    		float lastFall = np.floats.get(getKey(), "last-fall", 0f);
	    	for(float f : Arrays.asList(2f, 3f)) {
	    		if(lastFall < f && p.getFallDistance() > f) { // just pass over specific amount of fall
	    			Location loc = flying.getLocation(p.getWorld());
	    			if(loc == null)
	    				loc = p.getLocation();
	    			Block justBelow = loc.clone().sub(0, 0.2, 0).getBlock();
	    			boolean belowTransparent = justBelow.getType().isTransparent();
	    			boolean downTransparent = justBelow.getRelative(BlockFace.DOWN).getType().isTransparent();
	    			if((belowTransparent || downTransparent) && !LocationUtils.hasOtherThan(justBelow.getLocation(), Materials.AIR)) {
	    				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, 95, "packet",
	    						"Fall: " + lastFall + ", " + p.getFallDistance() + ", block: " + justBelow, null, (belowTransparent && downTransparent ? 5 : 1));
	    				if(mayCancel && isSetBack())
	    					manageDamage(p, (int) p.getFallDistance(), 95);
	    			}
	    		}
	    	}
    	}
    	
    	np.floats.set(getKey(), "last-fall", p.getFallDistance());
	}
	
	private void manageDamage(Player p, int damage, int relia) {
		p.damage(damage >= p.getHealth() ? (getConfig().getBoolean("set_back.kill.active") ? damage : p.getHealth() - 0.5) : damage);
	}
}
