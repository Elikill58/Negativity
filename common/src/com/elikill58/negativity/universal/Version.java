package com.elikill58.negativity.universal;

import java.util.Locale;
import java.util.concurrent.Callable;

import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.versions.Version1_10;
import com.elikill58.negativity.api.packets.nms.versions.Version1_11;
import com.elikill58.negativity.api.packets.nms.versions.Version1_12;
import com.elikill58.negativity.api.packets.nms.versions.Version1_12_2;
import com.elikill58.negativity.api.packets.nms.versions.Version1_13;
import com.elikill58.negativity.api.packets.nms.versions.Version1_14;
import com.elikill58.negativity.api.packets.nms.versions.Version1_14_4;
import com.elikill58.negativity.api.packets.nms.versions.Version1_15;
import com.elikill58.negativity.api.packets.nms.versions.Version1_16;
import com.elikill58.negativity.api.packets.nms.versions.Version1_17;
import com.elikill58.negativity.api.packets.nms.versions.Version1_18;
import com.elikill58.negativity.api.packets.nms.versions.Version1_19;
import com.elikill58.negativity.api.packets.nms.versions.Version1_19_2;
import com.elikill58.negativity.api.packets.nms.versions.Version1_19_3;
import com.elikill58.negativity.api.packets.nms.versions.Version1_19_4;
import com.elikill58.negativity.api.packets.nms.versions.Version1_20;
import com.elikill58.negativity.api.packets.nms.versions.Version1_20_2;
import com.elikill58.negativity.api.packets.nms.versions.Version1_20_4;
import com.elikill58.negativity.api.packets.nms.versions.Version1_20_5;
import com.elikill58.negativity.api.packets.nms.versions.Version1_8;
import com.elikill58.negativity.api.packets.nms.versions.Version1_9;
import com.elikill58.negativity.api.packets.nms.versions.VersionUnknown;

public enum Version {
	
	LOWER("lower", 0, VersionUnknown::new, 0, 5),
	V1_8("1.8.8", 8, Version1_8::new, 6, 47),
	V1_9("1.9", 9, Version1_9::new, 48, 110),
	V1_10("1.10", 10, Version1_10::new, 201, 210),
	V1_11("1.11", 11, Version1_11::new, 301, 316),
	V1_12("1.12", 12, Version1_12::new, 317, 335),
	V1_12_2("1.12.2", 12.2, Version1_12_2::new, 336, 340),
	V1_13("1.13", 13, Version1_13::new, 341, 404),
	V1_14("1.14", 14, Version1_14::new, 441, 489),
	V1_14_4("1.14.4", 14.4, Version1_14_4::new, 490, 500),
	V1_15("1.15", 15, Version1_15::new, 550, 578),
	V1_16("1.16", 16, Version1_16::new, 700, 754),
	V1_17("1.17", 17, Version1_17::new, 755, 756),
	V1_18("1.18", 18, Version1_18::new, 757, 758),
	V1_19("1.19", 19, Version1_19::new, 759),
	V1_19_2("1.19.2", 19.2, Version1_19_2::new, 760),
	V1_19_3("1.19.3", 19.3, Version1_19_3::new, 761),
	V1_19_4("1.19.4", 19.4, Version1_19_4::new, 762),
	V1_20("1.20", 20, Version1_20::new, 763),
	V1_20_2("1.20.2", 20.2, Version1_20_2::new, 764),
	V1_20_4("1.20.4", 20.4, Version1_20_4::new, 765),
	V1_20_5("1.20.5", 20.5, Version1_20_5::new, 766, 999),
	HIGHER("higher", 42, VersionUnknown::new, 1000);

	private final double power;
	private final int firstProtocolNumber, lastProtocolNumber;
	private final Callable<NamedVersion> versionCreator;
	private final String name;
	private NamedVersion version;

	Version(String name, double power, Callable<NamedVersion> versionCreator, int protocolNumber) {
		this(name, power, versionCreator, protocolNumber, protocolNumber);
	}
	
	Version(String name, double power, Callable<NamedVersion> versionCreator, int firstProtocolNumber, int lastProtocolNumber) {
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
	 * Get the power of the version (used to check newer/older)
	 * 
	 * @return the power version
	 */
	public double getPower() {
		return power;
	}

	/**
	 * Get if exist or create a {@link NamedVersion}.<br>
	 * If it create it, it will load all values required for the version
	 * 
	 * @return the named version or null if something gone wrong
	 */
	public NamedVersion getNamedVersion() {
		synchronized (versionCreator) {
			if(version == null) {
				try {
					this.version = versionCreator.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return version;
		}
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
		name = name.toLowerCase(Locale.ROOT).replace("R", "");
		for (Version v : Version.values())
			if (name.startsWith(v.getName().toLowerCase(Locale.ROOT)) || name.startsWith(v.name().toLowerCase(Locale.ROOT)))
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
}
