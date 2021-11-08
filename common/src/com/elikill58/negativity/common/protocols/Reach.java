package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.utils.UniversalUtils.parseInPorcent;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;

public class Reach extends Cheat implements Listeners {

	public static final DataType<Double> HIT_DISTANCE = new DataType<Double>("hit_distance", "Hit Distance", () -> new DoubleDataCounter());
	private NumberFormat nf = NumberFormat.getInstance();
	private static final List<Material> IGNORED_TYPE = Arrays.asList(Materials.BOW, Materials.FISHING_ROD);
	
	public Reach() {
		super(CheatKeys.REACH, CheatCategory.COMBAT, Materials.STONE_AXE, false, true);
		nf.setMaximumIntegerDigits(2);
	}

	@Check(name = "reach-event", description = "The reach", conditions = { CheckConditions.SURVIVAL, CheckConditions.NOT_THORNS })
	public void onCheckReach(PlayerDamageEntityEvent e, NegativityPlayer np) {
		if (e.isCancelled())
			return;
		Player p = e.getPlayer();
		boolean mayCancel = false;
		ItemStack inHand = p.getItemInHand();
		if(inHand == null || !IGNORED_TYPE.contains(inHand.getType())) {
			Entity et = e.getDamaged();
			BoundingBox bb1 = p.getBoundingBox(), bb2 = et.getBoundingBox();
			double dis = bb1.getNearestPoint(bb2.getMid()).distance(bb2.getNearestPoint(bb2.getMid()));
			recordData(p.getUniqueId(), HIT_DISTANCE, dis);
			if (dis > getConfig().getDouble("check.reach-event.value", 3.1) && !et.getType().equals(EntityType.ENDER_DRAGON) && !p.getLocation().getBlock().getType().getId().contains("WATER")) {
				String entityName = Version.getVersion().equals(Version.V1_7) ? et.getType().name().toLowerCase(Locale.ROOT) : et.getName();
				mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, parseInPorcent(dis * 2 * 10), "reach-event",
						"High distance with: " + et.getType().name().toLowerCase(Locale.ROOT) + ". Exact distance: " + dis + ", without thorns", hoverMsg("distance", "%name%", entityName, "%distance%", nf.format(dis)));
			}
		}
		if (isSetBack() && mayCancel)
			e.setCancelled(true);
	}
	
	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Double> counters = data.getData(HIT_DISTANCE);
		return Utils.coloredMessage("Hit distance (Sum/Min/Max) : " + getColoredDistance(counters.getAverage())
				+ "/" + getColoredDistance(counters.getMin()) + "/" + getColoredDistance(counters.getMax()));
	}
	
	private String getColoredDistance(double dis) {
		return (dis > 3 ? (dis > 4 ? "&c" : "&6") : "&a") + String.format("%.3f", dis);
	}
}
