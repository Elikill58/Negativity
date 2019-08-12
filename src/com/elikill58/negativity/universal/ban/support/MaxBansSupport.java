package com.elikill58.negativity.universal.ban.support;

import java.util.ArrayList;
import java.util.List;

import org.maxgamer.maxbans.MaxBans;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.ban.BanRequest;
import com.elikill58.negativity.universal.ban.BanRequest.BanType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class MaxBansSupport extends BanPluginSupport {
	
	@Override
	public void ban(NegativityPlayer np, String reason, String banner, long time) {
		if(UniversalUtils.isValidIP(np.getIP()))
			MaxBans.instance.getBanManager().tempipban(np.getIP(), reason, banner, time + System.currentTimeMillis());
		else
			MaxBans.instance.getBanManager().tempban(np.getName(), reason, banner, time + System.currentTimeMillis());
	}

	@Override
	public void banDef(NegativityPlayer np, String reason, String banner) {
		if(UniversalUtils.isValidIP(np.getIP()))
			MaxBans.instance.getBanManager().ipban(np.getIP(), reason, banner);
		else
			MaxBans.instance.getBanManager().ban(np.getName(), reason, banner);
	}

	@Override
	public void kick(NegativityPlayer np, String reason) {
		if(UniversalUtils.isValidIP(np.getIP()))
			MaxBans.instance.getBanManager().kickIP(np.getIP(), reason);
		else
			MaxBans.instance.getBanManager().kick(np.getName(), reason);
	}

	@Override
	public List<BanRequest> getBan(NegativityPlayer np) {
		final List<BanRequest> banRequest = new ArrayList<>();
		MaxBans.instance.getBanManager().getTempBans().forEach((s, tb) -> {
			banRequest.add(new BanRequest(np.getAccount(), tb.getReason(), tb.getExpires(), false, BanType.UNKNOW, "unknow", tb.getBanner(), false));
		});
		MaxBans.instance.getBanManager().getTempIPBans().forEach((s, tb) -> {
			banRequest.add(new BanRequest(np.getAccount(), tb.getReason(), tb.getExpires(), false, BanType.UNKNOW, "unknow", tb.getBanner(), false));
		});
		MaxBans.instance.getBanManager().getBans().forEach((s, tb) -> {
			banRequest.add(new BanRequest(np.getAccount(), tb.getReason(), 0, true, BanType.UNKNOW, "unknow", tb.getBanner(), false));
		});
		MaxBans.instance.getBanManager().getIPBans().forEach((s, tb) -> {
			banRequest.add(new BanRequest(np.getAccount(), tb.getReason(), 0, true, BanType.UNKNOW, "unknow", tb.getBanner(), false));
		});
		return banRequest;
	}
}
