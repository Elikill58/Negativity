package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.negativity.PlayerPacketsClearEvent;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.ray.entity.EntityRayBuilder;
import com.elikill58.negativity.api.ray.entity.EntityRayResult;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class ForceField extends Cheat {

	public static final DataType<Integer> FAKE_PLAYERS = new DataType<Integer>("fake_players", "Fake Players", () -> new IntegerDataCounter());

	private NumberFormat nf = NumberFormat.getInstance();
	
	public ForceField() {
		super(CheatKeys.FORCEFIELD, CheatCategory.COMBAT, Materials.DIAMOND_SWORD, true, false, "ff", "killaura");
		nf.setMaximumIntegerDigits(2);
	}
	
	@Check(name = "packet", description = "Count packet")
	public void onPacketClear(PlayerPacketsClearEvent e, NegativityPlayer np) {
		int arm = e.getPackets().getOrDefault(PacketType.Client.ARM_ANIMATION, 0);
		int useEntity = e.getPackets().getOrDefault(PacketType.Client.USE_ENTITY, 0);
		if (arm > 16 && useEntity > 20) {
			ReportType type = ReportType.WARNING;
			if (np.getWarn(this) > 5)
				type = ReportType.VIOLATION;
			Negativity.alertMod(type, e.getPlayer(), this, UniversalUtils.parseInPorcent(arm + useEntity + np.getWarn(this)),
					"packet", "ArmAnimation (Attack in one second): " + arm + ", UseEntity (interaction with other entity): "
					+ useEntity);
		}
	}

	@Check(name = "line-sight", description = "Player has line of sight the cible", conditions = CheckConditions.SURVIVAL)
	public void onEntityDamageByEntity(PlayerDamageEntityEvent e, NegativityPlayer np) {
		if (e.isCancelled())
			return;
		Player p = e.getPlayer();
		boolean mayCancel = false;
		Entity cible = e.getDamaged();
		EntityRayResult ray = new EntityRayBuilder(p).build().compile();
		List<Entity> lookingEntities = ray.getEntitiesFounded();
		if(p != cible && !p.hasLineOfSight(cible)) {
			mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, parseInPorcent(90 + np.getWarn(this)), "line-sight",
					"Hit " + cible.getType().name() + " (" + cible.getName() + ") but cannot see it. Looking: " + lookingEntities,
					hoverMsg("line_sight", "%name%", cible.getType().name().toLowerCase(Locale.ROOT)));
		} else
			Adapter.getAdapter().debug(p.getName() + " can see " + cible.getName() + ". " + (lookingEntities.contains(cible) ? "See cible." : "Don't see: " + lookingEntities));
		if (isSetBack() && mayCancel)
			e.setCancelled(true);
	}
	
	/*@EventListener
	public void onPacket(PacketReceiveEvent e) {
		AbstractPacket packet = e.getPacket();
		if(!packet.getPacketType().equals(PacketType.Client.USE_ENTITY))
			return;
		Player p = e.getPlayer();
		if(p.getGameMode().equals(GameMode.CREATIVE))
			return;
		ItemStack inHand = p.getItemInHand();
		if(inHand != null && inHand.getType().equals(Materials.BOW))
			return;
		try {
			Object nmsPacket = packet.getPacket();
			Location loc = p.getLocation();
			Object nmsEntity = nmsPacket.getClass().getDeclaredMethod("a", PacketUtils.getNmsClass("World")).invoke(nmsPacket, PacketUtils.getWorldServer(loc));
			if(nmsEntity == null)
				return;
			Location entityLoc = getLocationNMSEntity(nmsEntity, p.getWorld());
			double dis = loc.distance(entityLoc);
			recordData(p.getUniqueId(), HIT_DISTANCE, dis);
			if (dis < Adapter.getAdapter().getConfig().getDouble("cheats.forcefield.reach"))
				return;
			Class<?> entityClass = PacketUtils.getNmsClass("Entity");
			Entity cible = (Entity) entityClass.getDeclaredMethod("getBukkitEntity").invoke(nmsEntity);
			EntityType type = cible.getType();
			if(type.equals(EntityType.ENDER_DRAGON) || type.equals(EntityType.ENDERMAN))
				return;
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(dis * 2 * 10),
					"[Packet] Big distance with: " + type.name().toLowerCase() + ". Exact distance: " + dis + ", without thorns"
					+ ". Ping: " + p.getPing(), hoverMsg("distance", "%name%", PacketUtils.getNmsEntityName(nmsEntity), "%distance%", nf.format(dis)));
			if(mayCancel)
				packet.setCancelled(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public Location getLocationNMSEntity(Object src, World baseWorld) {
		return Adapter.getAdapter().createLocation(baseWorld, getFieldOrMethod(src, "locX"), getFieldOrMethod(src, "locY"), getFieldOrMethod(src, "locZ"));
	}
	
	private double getFieldOrMethod(Object src, String name) {
		Class<?> entityClass = PacketUtils.getNmsClass("Entity");
		try {
			Method m = entityClass.getDeclaredMethod(name);
			m.setAccessible(true);
			return (double) m.invoke(src);
		} catch (NoSuchMethodException e) {
			try {
				Field f = entityClass.getDeclaredField(name);
				f.setAccessible(true);
				return f.getDouble(src);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}*/
	
	public static void manageForcefieldForFakeplayer(Player p, NegativityPlayer np) {
		if(np.fakePlayerTouched == 0) return;
		Cheat forcefield = Cheat.forKey(CheatKeys.FORCEFIELD);
		forcefield.recordData(p.getUniqueId(), FAKE_PLAYERS, 1);
		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		Negativity.alertMod(np.fakePlayerTouched > 10 ? ReportType.VIOLATION : ReportType.WARNING, p, forcefield,
				parseInPorcent(np.fakePlayerTouched * 10), "ghost", "Hitting fake entities. " + np.fakePlayerTouched
				+ " entites touch in " + timeBehindStart + " millisecondes",
				forcefield.hoverMsg("fake_players", "%nb%", np.fakePlayerTouched, "%time%", timeBehindStart));
	}
}
