package com.elikill58.negativity.sponge.nms;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;
import com.elikill58.negativity.universal.Version;

public abstract class SpongeVersionAdapter extends VersionAdapter<ServerPlayer> {
	
	public SpongeVersionAdapter(String version) {
		super(version);
	}
	
	public String getParsedName(String name, String key) {
		return key + name.split(key)[1];
	}

	@Override
	public AbstractChannel getPlayerChannel(ServerPlayer p) {
		// TODO implement player channel
		return null;
	}
	
	private static SpongeVersionAdapter instance;
	
	public static SpongeVersionAdapter getVersionAdapter() {
		if (instance == null) {
			switch (Version.getVersion()) {
			case V1_16:
				try {
					return instance = (SpongeVersionAdapter) Class
							.forName("com.elikill58.negativity.sponge16.Sponge_1_16_5").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			case V1_18:
				try {
					return instance = (SpongeVersionAdapter) Class
							.forName("com.elikill58.negativity.sponge18.Sponge_1_18_2").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			case V1_19:
				try {
					return instance = (SpongeVersionAdapter) Class
							.forName("com.elikill58.negativity.sponge19.Sponge_1_19_2").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			default:
				return instance = new Sponge_UnknowVersion();
			}
		}
		return instance;
	}
}
