package com.elikill58.negativity.api.packets.nms.versions;

import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

public class VersionUnknown extends NamedVersion {

	public VersionUnknown() {
		Adapter.getAdapter().getLogger().error("The version " + Version.getVersion() + " isn't supported yet. Please report the issue.");
	}
}
