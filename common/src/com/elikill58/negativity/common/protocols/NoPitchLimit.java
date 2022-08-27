package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerifData;
import com.elikill58.negativity.universal.verif.VerifData.DataType;
import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.FloatDataCounter;

public class NoPitchLimit extends Cheat {

	public static final DataType<Float> PITCH = new DataType<Float>("pitch", "Pitch", () -> new FloatDataCounter());

	public NoPitchLimit() {
		super(CheatKeys.NO_PITCH_LIMIT, CheatCategory.PLAYER, Materials.SKELETON_SKULL, CheatDescription.VERIF);
	}

	@Check(name = "head-mov", description = "Check head movement")
	public void headMov(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		float pitch = p.getLocation().getPitch();
		recordData(p.getUniqueId(), PITCH, pitch);
		if (Math.abs(pitch) > 90) {
			if (Negativity.alertMod(ReportType.WARNING, p, this,
					UniversalUtils.parseInPorcent(pitch < 0 ? pitch * -1 : pitch), "head-mov",
					"Strange head movements: " + pitch) && isSetBack())
				e.setCancelled(true);
		}
	}

	@Override
	public String makeVerificationSummary(VerifData data, NegativityPlayer np) {
		DataCounter<Float> counter = data.getData(PITCH);
		return Utils.coloredMessage("&6Pitch &7Min: " + String.format("%.2f", counter.getMin()) + "&7, Max: "
				+ String.format("%.2f", counter.getMax()) + " &8(Normal when -90 < pitch < 90)");
	}
}
