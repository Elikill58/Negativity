package com.elikill58.negativity.universal.ban.support;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanList;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class EssentialsBanSupport extends BanPluginSupport {
	
	private Essentials essentials = (Essentials) Essentials.getProvidingPlugin(Essentials.class);
	
	@Override
	public void ban(NegativityPlayer np, String reason, String banner, long time) {
		essentials.getServer().getBanList(BanList.Type.NAME).addBan(np.getName(), reason, new Date(time), banner).save();
        kick(np, Messages.getMessage((Player) np.getPlayer(), "kick.kicked", "%playername%", banner, "%reason%", reason));
	}

	@Override
	public void banDef(NegativityPlayer np, String reason, String banner) {
		if(UniversalUtils.isValidIP(np.getIP()))
			essentials.getServer().banIP(np.getIP());
	}

	@Override
	public void kick(NegativityPlayer np, String reason) {
		essentials.getUser(np.getUUID()).getBase().kickPlayer(reason);
	}

	@Override
	public List<BanRequest> getBan(NegativityPlayer np) {
		final List<BanRequest> banRequest = new ArrayList<>();
		essentials.getServer().getBanList(BanList.Type.NAME).getBanEntries().forEach((banEntry) -> {
			if(banEntry.getTarget().equalsIgnoreCase(np.getName())) {
				banRequest.add(new BanRequest(np.getAccount(), banEntry.getReason(), banEntry.getExpiration().getTime(), false, BanType.UNKNOW, "unknow", banEntry.getSource(), false));
			}
		});
		return banRequest;
	}
}
