package com.elikill58.negativity.universal.bedrock.data;

import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.packets.BedrockClientData;

public interface BedrockClientDataGetter {

	@Nullable BedrockClientData getClientData(UUID uuid);
}
