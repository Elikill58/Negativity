package com.elikill58.negativity.common.integration.geysermc.geysermc;

import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.session.GeyserSession;

import com.elikill58.negativity.api.packets.BedrockClientData;
import com.elikill58.negativity.api.packets.BedrockClientData.BedrockOs;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.bedrock.data.BedrockClientDataGetter;

public class GeyserClientDataGetter implements BedrockClientDataGetter {
	
	@Override
	public @Nullable BedrockClientData getClientData(UUID uuid) {
		GeyserSession session = GeyserImpl.getInstance().connectionByUuid(uuid);
		if(session == null || session.getClientData() == null)
			return null;
		org.geysermc.geyser.session.auth.BedrockClientData data = session.getClientData();
		Adapter ada = Adapter.getAdapter();
		String osName = null;
		if(ada.hasPlugin("floodgate"))
			osName = FloodgateApi.getInstance().getPlayer(uuid).getDeviceOs().name();
		else {
			try {
				osName = data.getDeviceOs().name();
			} catch (LinkageError e) {
				ada.getLogger().warn("LinkageError between GeyserMC and Floodgate. Trying to fix ...");
			}
		}
		if(osName == null) {
			ada.getLogger().warn("Failed to find valid Device OS from GeyserMC");
			return null;
		}
		BedrockOs os = BedrockOs.valueOf(osName);
		
		return new BedrockClientData(data.getDeviceId(), data.getDeviceModel(), os);
	}
}
