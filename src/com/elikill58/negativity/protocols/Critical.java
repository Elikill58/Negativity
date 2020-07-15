package com.elikill58.negativity.protocols;

import com.elikill58.negativity.common.GameMode;
import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.EventListener;
import com.elikill58.negativity.common.events.Listeners;
import com.elikill58.negativity.common.events.player.PlayerDamageByEntityEvent;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Version;

public class Critical extends Cheat implements Listeners {
	
	public Critical() {
		super(CheatKeys.CRITICAL, false, Materials.FIREBALL, CheatCategory.COMBAT, true, "crit", "critic");
	}

	@EventListener
	public void onDamage(PlayerDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
		Player p = e.getEntity();

		if (p.isInsideVehicle())
			return;
		
		// because of new PvP, this detection but to be remade
		if(Version.getVersion().isNewerOrEquals(Version.V1_9))
			return;

		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if (!p.isOnGround() && !p.isFlying()) {
			if (p.getLocation().getY() % 1.0D == 0.0D) {
				boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, np.getAllWarn(this) > 5 ? 100 : 95, "");
				if(mayCancel && isSetBack())
					e.setCancelled(true);
			}
		}
	}
}
