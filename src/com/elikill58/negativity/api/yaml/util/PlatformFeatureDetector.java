package com.elikill58.negativity.api.yaml.util;

public class PlatformFeatureDetector {
	private Boolean isRunningOnAndroid;

	public PlatformFeatureDetector() {
		this.isRunningOnAndroid = null;
	}

	public boolean isRunningOnAndroid() {
		if (this.isRunningOnAndroid == null) {
			this.isRunningOnAndroid = System.getProperty("java.runtime.name").startsWith("Android Runtime");
		}
		return this.isRunningOnAndroid;
	}
}
