package com.elikill58.negativity.universal.ban.support;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class AdvancedBanSupport extends BanPluginSupport {
	
	@Override
	public void ban(NegativityPlayer np, String reason, String banner, long time) {
		Bukkit.getScheduler().runTaskAsynchronously(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				if(UniversalUtils.isValidIP(np.getIP()))
					new Punishment(np.getIP(), np.getUUID().toString(), reason, banner, PunishmentType.TEMP_IP_BAN, System.currentTimeMillis(), time, "", -1).create();
				else
					new Punishment(np.getName(), np.getUUID().toString(), reason, banner, PunishmentType.TEMP_BAN, System.currentTimeMillis(), time, "", -1).create();
			}
		});
	}

	@Override
	public void banDef(NegativityPlayer np, String reason, String banner) {
		Bukkit.getScheduler().runTaskAsynchronously(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				if(UniversalUtils.isValidIP(np.getIP()))
					new Punishment(np.getIP(), np.getUUID().toString(), reason, banner, PunishmentType.IP_BAN, System.currentTimeMillis(), 0, "", -1).create();
				else
					new Punishment(np.getName(), np.getUUID().toString(), reason, banner, PunishmentType.BAN, System.currentTimeMillis(), 0, "", -1).create();
			}
		});
	}

	@Override
	public void kick(NegativityPlayer np, String reason) {
		Bukkit.getScheduler().runTaskAsynchronously(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				new Punishment(np.getName(), np.getUUID().toString(), reason, "", PunishmentType.KICK, System.currentTimeMillis(), 0, "", -1).create();
			}
		});
	}

	@Override
	public List<BanRequest> getBan(NegativityPlayer np) {
		final List<BanRequest> banRequest = new ArrayList<>();
		PunishmentManager.get().getWarns(np.getUUID().toString()).forEach((punish) -> {
			banRequest.add(new BanRequest(np.getAccount(), punish.getReason(), punish.getEnd(), false, BanType.UNKNOW, "unknow", punish.getOperator(), false));
		});
		return banRequest;
	}
}
