package com.elikill58.negativity.spigot.protocols;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.text.NumberFormat;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.protocols.reach.Point;
import com.elikill58.negativity.spigot.protocols.reach.Rect;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
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
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) throws Exception {
		if (!(e.getDamager() instanceof Player) || e.isCancelled() || e.getCause() != DamageCause.ENTITY_ATTACK)
			return;
		Player p = (Player) e.getDamager();
		if (p.hasMetadata("NPC"))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || e.getEntity() == null)
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		EntityType type = e.getEntityType();
		if (type == EntityType.ENDER_DRAGON || type.name().contains("PHANTOM") || type.name().contains("BALL") || type.name().contains("TNT") || type.name().contains("ARROW"))
			return;
		ItemStack inHand = Utils.getItemInHand(p);
		if (inHand == null || !inHand.getType().equals(Material.BOW)) {
			Entity cible = e.getEntity();
			Object pBB = PacketUtils.getBoundingBox(p);
			Object cibleBB = PacketUtils.getBoundingBox(cible);
			Adapter.getAdapter().runAsync(() -> {
				try {
					double dis = distance(pBB, cibleBB);
					recordData(p.getUniqueId(), HIT_DISTANCE, dis);
					Material blockType = p.getLocation().getBlock().getType();
					double maxReach = Adapter.getAdapter().getConfig().getDouble("cheats.forcefield.reach");
					if (dis > maxReach && !blockType.name().contains("WATER") && !blockType.name().contains("LAVA") && (dis > (maxReach + 1))) {
						Bukkit.getScheduler().runTask(SpigotNegativity.getInstance(), () -> {
							boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(dis * 2 * 10),
									"With: " + cible.getType().name().toLowerCase(Locale.ROOT) + ". Distance: " + dis,
									hoverMsg("distance", "%name%", cible.getName(), "%distance%", nf.format(dis)));
							if (isSetBack() && mayCancel)
								e.setCancelled(true);
						});
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			});
		}
	}

	private double sens = 0.005;

	private double distance(Object a, Object b) {
		if (a == null || b == null) {
			SpigotNegativity.getInstance().getLogger().info("Failed to get entity BoundingBox (=HitBox) A/B null: " + a + "/" + b);
			return 0.0;
		}
		try {
			Rect r1 = new Rect(a);
			Rect r2 = new Rect(b);
			Rect min = new Rect(r1, r2, (t) -> Math.min(t.a, t.b));
			Rect max = new Rect(r1, r2, (t) -> Math.max(t.a, t.b));
			Point m1 = min.getMid(), m2 = max.getMid(); // find mid of both rectangle
			Point mr1 = null, mr2 = null;
			for (double x = m1.x; x <= m2.x; x += sens) {
				for (double y = m1.y; y <= m2.y; y += sens) {
					for (double z = m1.z; z <= m2.z; z += sens) { // searching for point which just go outside of rect
						if (mr1 == null) {
							if (!min.isIn(x, y, z)) {
								mr1 = new Point(x, y, z);
							}
						} else if (mr2 == null) {
							if (max.isIn(x, y, z)) {
								mr2 = new Point(x, y, z);
							}
						} else {
							return mr1.distance(mr2);
						}
					}
				}
			}
			if (mr1 == null || mr2 == null) {
				// SpigotNegativity.getInstance().getLogger().warning("Cannot find valid point
				// for calculating distance: " + mr1 + " > " + mr2);
				return 0;
			}
			return mr1.distance(mr2);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> reach = data.getData(HIT_DISTANCE);
		double av = reach.getAverage(), maxReach = reach.getMax();
		String reachStr = "Hit distance (Av/Max) : " + getColor(av) + String.format("%.3f", av) + " / " + getColor(maxReach) + String.format("%.3f", maxReach);
		int nb = data.getData(FAKE_PLAYERS).getSize();
		return Utils.coloredMessage(reachStr + (nb > 0 ? " &7and &c" + nb + " &7fake players touched." : ""));
	}

	private String getColor(double d) {
		return (d > 3 ? (d > 4 ? "&c" : "&6") : "&a");
	}
}
