package com.elikill58.negativity.protocols;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Entity;
import com.elikill58.negativity.common.entity.EntityType;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.player.PlayerDamageByEntityEvent;
import com.elikill58.negativity.common.item.ItemStack;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.common.location.World;
import com.elikill58.negativity.common.utils.Utils;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class ForceField extends Cheat implements Listeners {

	public static final DataType<Double> HIT_DISTANCE = new DataType<Double>("hit_distance", "Hit Distance", () -> new DoubleDataCounter());
	public static final DataType<Integer> FAKE_PLAYERS = new DataType<Integer>("fake_players", "Fake Players", () -> new IntegerDataCounter());

	private NumberFormat nf = NumberFormat.getInstance();
	
	public ForceField() {
		super(CheatKeys.FORCEFIELD, true, Materials.DIAMOND_SWORD, CheatCategory.COMBAT, true, "ff", "killaura");
		nf.setMaximumIntegerDigits(2);
	}

	@EventListener
	public void onEntityDamageByEntity(PlayerDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player) || e.isCancelled())
			return;
		Player p = (Player) e.getDamager();
		NegativityPlayer np = NegativityPlayer.getCached(p.getUniqueId());
		if (!np.hasDetectionActive(this) || e.getEntity() == null)
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		boolean mayCancel = false;
		Entity cible = e.getEntity();
		int ping = p.getPing();
		if(p.hasLineOfSight(cible)) {
			mayCancel = Negativity.alertMod(ReportType.VIOLATION, p, this, parseInPorcent(90 + np.getWarn(this)), "Hit " + cible.getType().name()
					+ " but cannot see it, ping: " + ping,
					hoverMsg("line_sight", "%name%", cible.getType().name().toLowerCase()));
		}
		if(Utils.hasThorns(p)) {
			if (isSetBack() && mayCancel)
				e.setCancelled(true);
			return;
		}
		Location tempLoc = e.getEntity().getLocation().clone();
		tempLoc.setY(p.getLocation().getY());
		double dis = tempLoc.distance(p.getLocation());
		ItemStack inHand = p.getItemInHand();
		if(inHand == null || !inHand.getType().equals(Materials.BOW)) {
			recordData(p.getUniqueId(), HIT_DISTANCE, dis);
			if (dis > Adapter.getAdapter().getConfig().getDouble("cheats.forcefield.reach") && !e.getDamager().getType().equals(EntityType.ENDER_DRAGON)) {
				String entityName = Version.getVersion().equals(Version.V1_7) ? e.getEntity().getType().name().toLowerCase() : e.getEntity().getName();
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(dis * 2 * 10),
						"Big distance with: " + e.getEntity().getType().name().toLowerCase() + ". Exact distance: " + dis + ", without thorns"
						+ ". Ping: " + ping, hoverMsg("distance", "%name%", entityName, "%distance%", nf.format(dis)));
			}
		}
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
	}*/
	
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
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		double av = data.getData(HIT_DISTANCE).getAverage();
		int nb = data.getData(FAKE_PLAYERS).getSize();
		String color = (av > 3 ? (av > 4 ? "&c" : "&6") : "&a");
		return Utils.coloredMessage("Hit distance : " + color + String.format("%.3f", av) + (nb > 0 ? " &7and &c" + nb + " &7fake players touched." : ""));
	}
	
	public static void manageForcefieldForFakeplayer(Player p, NegativityPlayer np) {
		if(np.fakePlayerTouched == 0) return;
		Cheat forcefield = Cheat.forKey(CheatKeys.FORCEFIELD);
		forcefield.recordData(p.getUniqueId(), FAKE_PLAYERS, 1);
		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		Negativity.alertMod(np.fakePlayerTouched > 10 ? ReportType.VIOLATION : ReportType.WARNING, p, forcefield,
				parseInPorcent(np.fakePlayerTouched * 10), "Hitting fake entities. " + np.fakePlayerTouched
						+ " entites touch in " + timeBehindStart + " millisecondes",
						forcefield.hoverMsg("fake_players", "%nb%", np.fakePlayerTouched, "%time%", timeBehindStart));
	}
}
