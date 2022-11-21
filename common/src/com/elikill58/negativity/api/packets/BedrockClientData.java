package com.elikill58.negativity.api.packets;

/**
 * This is a very lite version of client informations from bedrock players.
 * 
 */
public class BedrockClientData {

	private String deviceId;
	private String deviceModel;
	private BedrockOs bedrockOs;

	public BedrockClientData(String deviceId, String deviceModel, BedrockOs bedrockOs) {
		this.deviceId = deviceId;
		this.deviceModel = deviceModel;
		this.bedrockOs = bedrockOs;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public String getDeviceModel() {
		return deviceModel;
	}
	
	public BedrockOs getDeviceOs() {
		return bedrockOs;
	}

	public static enum BedrockOs {

		UNKNOWN("Unknown"),
		GOOGLE("Android", true),
		IOS("iOS", true),
		OSX("macOS"),
		AMAZON("Amazon"),
		GEARVR("Gear VR"),
		HOLOLENS("Hololens"),
		UWP("Windows 10"),
		WIN32("Windows x86"),
		DEDICATED("Dedicated"),
		TVOS("Apple TV"),
		PS4("PS4"),
		NX("Switch"),
		XBOX("Xbox One"),
		WINDOWS_PHONE("Windows Phone");

		private final String displayName;
		private final boolean phone;

		private BedrockOs(String displayName) {
			this(displayName, false);
		}

		private BedrockOs(String displayName, boolean phone) {
			this.displayName = displayName;
			this.phone = phone;
		}
		
		public boolean isPhone() {
			return phone;
		}
		
		public String getDisplayName() {
			return displayName;
		}

		/**
		 * @return friendly display name of platform.
		 */
		@Override
		public String toString() {
			return displayName;
		}
	}
}
