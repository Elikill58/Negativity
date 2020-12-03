package com.elikill58.negativity.universal.multiVersion;

import com.elikill58.negativity.universal.Adapter;

public interface PlayerVersionFetcherProvider {
	
	PlayerVersionFetcher create(Adapter adapter);
}
