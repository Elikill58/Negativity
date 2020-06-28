package com.elikill58.negativity.spigot.protocols;

import static com.elikill58.negativity.universal.utils.ReflectionUtils.callMethod;
import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.PlayerPacketsClearEvent;
import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.event.PacketReceiveEvent;
import com.elikill58.negativity.spigot.utils.LocationUtils;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class ForceFieldProtocol extends Cheat implements Listener {

	public static final DataType<Double> HIT_DISTANCE = new DataType<Double>("hit_distance", "Hit Distance", () -> new DoubleDataCounter());
	public static final DataType<Integer> FAKE_PLAYERS = new DataType<Integer>("fake_players", "Fake Players", () -> new IntegerDataCounter());

	private NumberFormat nf = NumberFormat.getInstance();
	
	public ForceFieldProtocol() {
		super(CheatKeys.FORCEFIELD, true, Material.DIAMOND_SWORD, CheatCategory.COMBAT, true, "ff", "killaura");
		nf.setMaximumIntegerDigits(2);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player) || e.isCancelled())
			return;
		Player p = (Player) e.getDamager();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.ACTIVE_CHEAT.contains(this) || e.getEntity() == null)
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		boolean mayCancel = false;
		Entity cible = e.getEntity();
		if(!(LocationUtils.hasLineOfSight(p, cible.getLocation()) && LocationUtils.hasLineOfSight(p, cible.getLocation().clone().subtract(0, 1, 0)))) {
			mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, this, parseInPorcent(90 + np.getWarn(this)), "Hit " + cible.getType().name()
					+ " but cannot see it, ping: " + Utils.getPing(p),
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
		ItemStack inHand = Utils.getItemInHand(p);
		if(inHand == null || !inHand.getType().equals(Material.BOW)) {
			recordData(p.getUniqueId(), HIT_DISTANCE, dis);
			if (dis > Adapter.getAdapter().getConfig().getDouble("cheats.forcefield.reach") && !e.getEntityType().equals(EntityType.ENDER_DRAGON)) {
				mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(dis * 2 * 10),
						"Big distance with: " + e.getEntity().getType().name().toLowerCase() + ". Exact distance: " + dis + ", without thorns"
						+ ". Ping: " + Utils.getPing(p), hoverMsg("distance", "%name%", e.getEntity().getName(), "%distance%", nf.format(dis)));
			}
		}
		final Location loc = p.getLocation().clone();
		Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), () -> {
			Location loc1 = p.getLocation();
			int gradeRounded = Math.round(Math.abs(loc.getYaw() - loc1.getYaw()));
			if (gradeRounded > 180.0) {
				SpigotNegativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(gradeRounded),
						"Player rotate too much (" + gradeRounded + "°) without thorns", hoverMsg("rotate", "%degrees%", gradeRounded));
			}
		}, 1);
		if (isSetBack() && mayCancel)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPacket(PacketReceiveEvent e) {
		AbstractPacket packet = e.getPacket();
		if(!packet.getPacketType().equals(PacketType.Client.USE_ENTITY))
			return;
		Player p = e.getPlayer();
		ItemStack inHand = Utils.getItemInHand(p);
		if(inHand != null && inHand.getType().equals(Material.BOW))
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
			EntityType type = (EntityType) callMethod(entityClass.getDeclaredMethod("getBukkitEntity").invoke(nmsEntity), "getType");
			if(type.equals(EntityType.ENDER_DRAGON))
				return;
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(dis * 2 * 10),
					"Big distance with: " + type.name().toLowerCase() + ". Exact distance: " + dis + ", without thorns"
					+ ". Ping: " + Utils.getPing(p), hoverMsg("distance", "%name%", PacketUtils.getNmsEntityName(nmsEntity), "%distance%", nf.format(dis)));
			if(mayCancel)
				packet.setCancelled(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public Location getLocationNMSEntity(Object src, World baseWorld) {
		return new Location(baseWorld, getFieldOrMethod(src, "locX"), getFieldOrMethod(src, "locY"), getFieldOrMethod(src, "locZ"));
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
	
	@EventHandler
	public void onPacketClear(PlayerPacketsClearEvent e) {
		int use = e.getPackets().getOrDefault(PacketType.Client.USE_ENTITY, 0);
		if(use > 8) {
			Player p = e.getPlayer();
			int ping = Utils.getPing(p);
			SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(use * 10 - ping),
					use + " USE_ENTITY packets sent. Ping: " + ping, (CheatHover) null, use - 7);
		}
	}
	
	public static void manageForcefieldForFakeplayer(Player p, SpigotNegativityPlayer np) {
		if(np.fakePlayerTouched == 0) return;
		Cheat forcefield = Cheat.forKey(CheatKeys.FORCEFIELD);
		forcefield.recordData(p.getUniqueId(), FAKE_PLAYERS, 1);
		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		SpigotNegativity.alertMod(np.fakePlayerTouched > 10 ? ReportType.VIOLATION : ReportType.WARNING, p, forcefield,
				parseInPorcent(np.fakePlayerTouched * 10), "Hitting fake entities. " + np.fakePlayerTouched
						+ " entites touch in " + timeBehindStart + " millisecondes",
						forcefield.hoverMsg("fake_players", "%nb%", np.fakePlayerTouched, "%time%", timeBehindStart));
	}
}
