package com.elikill58.negativity.spigot.protocols;

import static com.elikill58.negativity.spigot.utils.PacketUtils.getField;
import static com.elikill58.negativity.spigot.utils.PacketUtils.callMethod;
import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.text.NumberFormat;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.PacketType;
import com.elikill58.negativity.spigot.packets.event.PacketReceiveEvent;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;

public class ForceFieldProtocol extends Cheat implements Listener {

	public static final DataType<Double> HIT_DISTANCE = new DataType<Double>(() -> new DoubleDataCounter("hit_distance", "Hit Distance"));
	public static final DataType<Integer> FAKE_PLAYERS = new DataType<Integer>(() -> new IntegerDataCounter("fake_players", "Fake Players"));

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
		if(!p.hasLineOfSight(e.getEntity())) {
			mayCancel = SpigotNegativity.alertMod(ReportType.VIOLATION, p, this, parseInPorcent(90 + np.getWarn(this)), "Hit " + e.getEntity().getType().name()
					+ " but cannot see it, ping: " + Utils.getPing(p),
					hoverMsg("line_sight", "%name%", e.getEntity().getType().name().toLowerCase()));
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
			np.verificatorForMod.forEach((s, verif) -> {
				verif.getVerifData(this).ifPresent((data) -> data.getData(HIT_DISTANCE).add(dis));
			});
			if (dis > Adapter.getAdapter().getConfig().getDouble("cheats.forcefield.reach") && !e.getEntityType().equals(EntityType.ENDER_DRAGON)) {
				mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(dis * 2 * 10),
						"Big distance with: " + e.getEntity().getType().name().toLowerCase() + ". Exact distance: " + dis + ", without thorns"
						+ ". Ping: " + Utils.getPing(p), hoverMsg("distance", "%name%", e.getEntity().getName(), "%distance%", nf.format(dis)));
			}
		}
		final Location loc = p.getLocation().clone();
		Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
			public void run() {
				Location loc1 = p.getLocation();
				int gradeRounded = Math.round(Math.abs(loc.getYaw() - loc1.getYaw()));
				if (gradeRounded > 180.0) {
					SpigotNegativity.alertMod(ReportType.WARNING, p, Cheat.forKey(CheatKeys.FORCEFIELD), parseInPorcent(gradeRounded),
							"Player rotate too much (" + gradeRounded + "°) without thorns", hoverMsg("rotate", "%degrees%", gradeRounded));
				}
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
		try {
			Object nmsPacket = packet.getPacket();
			Location loc = p.getLocation();
			Object et = nmsPacket.getClass().getDeclaredMethod("a", PacketUtils.getNmsClass("World")).invoke(nmsPacket, PacketUtils.getWorldServer(loc));
			if(et == null)
				return;
			Location entityLoc = new Location(p.getWorld(), (double) getField(et, "locX"), (double) getField(et, "locY"), (double) getField(et, "locZ"));
			double dis = loc.distance(entityLoc);
			ItemStack inHand = Utils.getItemInHand(p);
			if(inHand != null && inHand.getType().equals(Material.BOW))
				return;
			SpigotNegativityPlayer.getNegativityPlayer(p).verificatorForMod.forEach((s, verif) -> {
				verif.getVerifData(this).ifPresent((data) -> data.getData(HIT_DISTANCE).add(dis));
			});
			if (dis < Adapter.getAdapter().getConfig().getDouble("cheats.forcefield.reach"))
				return;
			Class<?> entityClass = PacketUtils.getNmsClass("Entity");
			EntityType type = (EntityType) callMethod(entityClass.getDeclaredMethod("getBukkitEntity").invoke(et), "getType");
			if(type.equals(EntityType.ENDER_DRAGON))
				return;
			String entityName = (String) entityClass.getMethod("getName").invoke(et);
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(dis * 2 * 10),
					"Big distance with: " + type.name().toLowerCase() + ". Exact distance: " + dis + ", without thorns"
					+ ". Ping: " + Utils.getPing(p), hoverMsg("distance", "%name%", entityName, "%distance%", nf.format(dis)));
			if(mayCancel)
				packet.setCancelled(true);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	@Override
	public String compile(VerifData data, NegativityPlayer np) {
		double av = data.getData(HIT_DISTANCE).getAverage();
		int nb = data.getData(FAKE_PLAYERS).getSize();
		String color = (av > 3 ? (av > 4 ? "&c" : "&6") : "&a");
		return Utils.coloredMessage("Hit distance : " + color + String.format("%.3f", av) + (nb > 0 ? " &7and &c" + nb + " &7fake players touched." : ""));
	}
	
	public static void manageForcefieldForFakeplayer(Player p, SpigotNegativityPlayer np) {
		Cheat forcefield = Cheat.forKey(CheatKeys.FORCEFIELD);
		np.verificatorForMod.forEach((s, verif) -> verif.getVerifData(forcefield).ifPresent((data) -> data.getData(FAKE_PLAYERS).add(1)));
		double timeBehindStart = System.currentTimeMillis() - np.timeStartFakePlayer;
		SpigotNegativity.alertMod(np.fakePlayerTouched > 10 ? ReportType.VIOLATION : ReportType.WARNING, p, forcefield,
				parseInPorcent(np.fakePlayerTouched * 10), "Hitting fake entities. " + np.fakePlayerTouched
						+ " entites touch in " + timeBehindStart + " millisecondes",
						forcefield.hoverMsg("fake_players", "%nb%", np.fakePlayerTouched, "%time%", timeBehindStart));
	}
}
