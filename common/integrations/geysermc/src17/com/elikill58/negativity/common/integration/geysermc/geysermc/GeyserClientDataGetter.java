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
		BedrockOs os = BedrockOs.valueOf(ada.hasPlugin("floodgate") ? FloodgateApi.getInstance().getPlayer(uuid).getDeviceOs().name() : data.getDeviceOs().name());
		
		return new BedrockClientData(data.getDeviceId(), data.getDeviceModel(), os);
	}
}
