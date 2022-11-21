package com.elikill58.negativity.universal.bedrock.data;

import com.elikill58.negativity.universal.Adapter;

public interface BedrockClientDataProvider {
	
	BedrockClientDataGetter create(Adapter adapter);
}
