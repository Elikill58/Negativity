package com.elikill58.negativity.universal;

public class UpdateCheckResult {

	private final String latestVersion;
	private final boolean isNewerVersion;
	private final String latestVersionDownloadUrl;

	public UpdateCheckResult(String latestVersion, boolean isNewerVersion, String latestVersionDownloadUrl) {

		this.latestVersion = latestVersion;
		this.isNewerVersion = isNewerVersion;
		this.latestVersionDownloadUrl = latestVersionDownloadUrl;
	}

	/**
	 * @return the latest version found by the update checker that returned this result
	 */
	public String getVersionString() {
		return latestVersion;
	}

	/**
	 * @return {@code true} if this result points to a newer version than the current one, {@code false} otherwise
	 */
	public boolean isNewerVersion() {
		return isNewerVersion;
	}

	/**
	 * @return the HTTP URL string representation of the download link for the {@link #getVersionString() pointed version}
	 */
	public String getDownloadUrl() {
		return latestVersionDownloadUrl;
	}
}
