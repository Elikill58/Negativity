package com.elikill58.negativity.common.protocols;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerDamageByEntityEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.protocols.CheckConditions;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.report.ReportType;

public class Critical extends Cheat implements Listeners {
	
	public Critical() {
		super(CheatKeys.CRITICAL, CheatCategory.COMBAT, Materials.FIREBALL, false, false, "crit", "critic");
	}

	@Check(name = "ground", description = "Check damage according to Y", conditions = { CheckConditions.NO_INSIDE_VEHICLE, CheckConditions.SURVIVAL, CheckConditions.NO_FLY, CheckConditions.NO_GROUND })
	public void onDamage(PlayerDamageByEntityEvent e, NegativityPlayer np) {
		if (e.isCancelled())
			return;
		Player p = e.getEntity();

		// because of new PvP, this detection has to be remade
		if(Version.getVersion().isNewerOrEquals(Version.V1_9))
			return;

		if (p.getLocation().getY() % 1.0D == 0.0D) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, np.getAllWarn(this) > 5 ? 100 : 95, "ground", "");
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
	}
}
