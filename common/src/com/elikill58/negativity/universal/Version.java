package com.elikill58.negativity.universal;

import java.util.Locale;
import java.util.concurrent.Callable;

import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.versions.Version1_8;
import com.elikill58.negativity.api.packets.nms.versions.VersionUnknown;

public enum Version {
	
	LOWER("lower", 0, VersionUnknown::new, 0, 5),
	V1_8("1.8", 8, Version1_8::new, 6, 47),
	V1_9("1.9", 9, VersionUnknown::new, 48, 110),
	V1_10("1.10", 10, VersionUnknown::new, 201, 210),
	V1_11("1.11", 11, VersionUnknown::new, 301, 316),
	V1_12("1.12", 12, VersionUnknown::new, 317, 340),
	V1_13("1.13", 13, VersionUnknown::new, 341, 404),
	V1_14("1.14", 14, VersionUnknown::new, 441, 500),
	V1_15("1.15", 15, VersionUnknown::new, 550, 578),
	V1_16("1.16", 16, VersionUnknown::new, 700, 754),
	V1_17("1.17", 17, VersionUnknown::new, 755, 756),
	V1_18("1.18", 18, VersionUnknown::new, 757, 758),
	V1_19("1.19", 19, VersionUnknown::new, 759, 1000),
	HIGHER("higher", 42, VersionUnknown::new, 1000, 1000);

	private final int power, firstProtocolNumber, lastProtocolNumber;
	private final String name;
	private final Callable<NamedVersion> versionCreator;
	
	Version(String name, int power, Callable<NamedVersion> versionCreator, int firstProtocolNumber, int lastProtocolNumber) {
		this.name = name;
		this.power = power;
		this.versionCreator = versionCreator;
		this.firstProtocolNumber = firstProtocolNumber;
		this.lastProtocolNumber = lastProtocolNumber;
	}
	
	/**
	 * Get the name of the version
	 * 
	 * @return the version name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the first protocol number of this version.
	 * 
	 * This value is used as default protocol version of a version.
	 * 
	 * @return the protocol version
	 */
	public int getFirstProtocolNumber() {
		return firstProtocolNumber;
	}

	/**
	 * Get the last protocol number of this version.
	 * 
	 * This value is used as default protocol version of a version.
	 * 
	 * @return the protocol version
	 */
	public int getLastProtocolNumber() {
		return lastProtocolNumber;
	}
	
	/**
	 * Check if this version is strictly newer than the given one
	 * 
	 * @param other the version which will be compared
	 * @return true is this version is newer than the given
	 */
	public boolean isNewerThan(Version other) {
		return power > other.getPower();
	}

	/**
	 * Check if this version is newer or equals than the given one
	 * 
	 * @param other the version which will be compared
	 * @return true is this version is newer or equals than the given
	 */
	public boolean isNewerOrEquals(Version other) {
		return power >= other.getPower();
	}

	/**
	 * Get the power of the version
	 * (used to check newer/older)
	 * 
	 * @return the power version
	 */
	public int getPower() {
		return power;
	}

	public NamedVersion createNamedVersion() {
		try {
			return versionCreator.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * The list of all protocol number in this version
	 * 
	 * @param protocolId ID of protocol used
	 * @return list of all version number
	 */
	public boolean hasProtocolNumber(int protocolId) {
		return protocolId >= firstProtocolNumber && protocolId <= lastProtocolNumber;
	}
	
	/**
	 * Get the version thanks to it's name
	 * 
	 * @param name the name of the version
	 * @return the founded version or {@link #HIGHER}
	 */
	public static Version getVersionByName(String name) {
		for (Version v : Version.values())
			if (name.startsWith(v.getName()))
				return v;
		return HIGHER;
	}
	
	/**
	 * Get the version thanks to the protocol ID
	 * 
	 * @param id the id of the version
	 * @return the founded version or {@link #HIGHER} or {@link #LOWER}
	 */
	public static Version getVersionByProtocolID(int id) {
		for (Version v : Version.values())
			if (v.hasProtocolNumber(id))
				return v;
		return id <= LOWER.getLastProtocolNumber() ? LOWER : HIGHER;
	}
	
	/**
	 * Get the version thanks to the adapter version name
	 * 
	 * @return the founded version or {@link #HIGHER}
	 */
	public static Version getVersion() {
		return Adapter.getAdapter().getServerVersion();
	}
	
	/**
	 * Get the version thanks to it's name
	 * 
	 * @param version the name of the version
	 * @return the founded version or {@link #HIGHER}
	 */
	public static Version getVersion(String version) {
		for (Version v : Version.values())
			if (version.toLowerCase(Locale.ROOT).startsWith(v.name().toLowerCase(Locale.ROOT)))
				return v;
		return HIGHER;
	}
}
