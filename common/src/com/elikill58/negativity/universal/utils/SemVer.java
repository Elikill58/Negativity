package com.elikill58.negativity.universal.utils;

import java.util.Locale;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents the simplified SemVer schema Negativity uses for its plugin versions.
 */
public final class SemVer implements Comparable<SemVer> {
	
	private final int major;
	private final int minor;
	private final int patch;
	
	private final @Nullable Suffix suffix;
	
	public SemVer(int major, int minor, int patch, @Nullable Suffix suffix) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.suffix = suffix;
	}
	
	public boolean isNewerThan(SemVer other) {
		return this.compareTo(other) > 0;
	}
	
	public boolean isOlderThan(SemVer other) {
		return this.compareTo(other) < 0;
	}
	
	/**
	 * Slightly different from {@link #equals} in that this method will return
	 * true if the suffix has the same {@link Suffix#getWeight() weight}
	 * but not the same {@link Suffix#getText()} text
	 * 
	 * @param other the version to compare with
	 * @return true if version are equivalent
	 */
	public boolean isEquivalentTo(SemVer other) {
		return this.compareTo(other) == 0;
	}
	
	public int getMajor() {
		return major;
	}
	
	public int getMinor() {
		return minor;
	}
	
	public int getPatch() {
		return patch;
	}
	
	public @Nullable Suffix getSuffix() {
		return suffix;
	}
	
	@Override
	public int compareTo(@NonNull SemVer o) {
		int majorDelta = Integer.compare(this.major, o.major);
		if (majorDelta != 0) {
			return majorDelta;
		}
		int minorDelta = Integer.compare(this.minor, o.minor);
		if (minorDelta != 0) {
			return minorDelta;
		}
		int patchDelta = Integer.compare(this.patch, o.patch);
		if (patchDelta != 0) {
			return patchDelta;
		}
		if (this.suffix != null) {
			return o.suffix != null ? this.suffix.compareTo(o.suffix) : -1;
		}
		if (o.suffix != null) {
			return 1;
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SemVer semVer = (SemVer) o;
		return major == semVer.major && minor == semVer.minor && patch == semVer.patch && Objects.equals(suffix, semVer.suffix);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(major, minor, patch, suffix);
	}
	
	@Override
	public String toString() {
		return "SemVer{major=" + major + ", minor=" + minor + ", patch=" + patch + ", suffix=" + suffix + '}';
	}
	
	public String toFormattedString() {
		if (suffix == null) {
			if(patch > 0)
				return major + "." + minor + (patch > 0 ? "." + patch : "");
		}
		return major + "." + minor + (patch > 0 ? "." + patch : "") + (suffix == null ? "" : "-" + suffix.getText());
	}
	
	public static @Nullable SemVer parse(@NonNull String text) {
		if (text == null || text.isEmpty()) {
			return null;
		}
		
		String[] suffixSplit = text.split("-", 2);
		Suffix suffix = null;
		if (suffixSplit.length > 1) {
			suffix = Suffix.of(suffixSplit[1]);
		}
		
		String[] parts = suffixSplit[0].split("\\.");
		int major;
		try {
			major = Integer.parseInt(parts[0]);
		} catch (NumberFormatException e) {
			return null;
		}
		int minor = 0;
		if (parts.length > 1) {
			try {
				minor = Integer.parseInt(parts[1]);
			} catch (NumberFormatException ignore) {
			}
		}
		int patch = 0;
		if (parts.length > 2) {
			try {
				patch = Integer.parseInt(parts[2]);
			} catch (NumberFormatException ignore) {
			}
		}
		return new SemVer(major, minor, patch, suffix);
	}
	
	public static final class Suffix implements Comparable<Suffix> {
		
		public static final Suffix SNAPSHOT = new Suffix(0, "SNAPSHOT");
		public static final Suffix ALPHA = new Suffix(1, "ALPHA");
		public static final Suffix BETA = new Suffix(2, "BETA");
		public static final Suffix GAMMA = new Suffix(3, "GAMMA");
		public static final Suffix DELTA = new Suffix(4, "DELTA");
		
		private final int weight;
		private final String text;
		
		public Suffix(int weight, String text) {
			this.weight = weight;
			this.text = text;
		}
		
		public int getWeight() {
			return weight;
		}
		
		public String getText() {
			return text;
		}
		
		@Override
		public int compareTo(@NonNull Suffix o) {
			return Integer.compare(this.weight, o.weight);
		}
		
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Suffix suffix = (Suffix) o;
			return weight == suffix.weight && text.equals(suffix.text);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(weight, text);
		}
		
		@Override
		public String toString() {
			return "Suffix{weight=" + weight + ", text='" + text + "'}";
		}
		
		public static Suffix of(String text) {
			String uppercaseText = text.toUpperCase(Locale.ROOT);
			switch (uppercaseText) {
			case "SNAPSHOT":
				return SNAPSHOT;
			case "ALPHA":
				return ALPHA;
			case "BETA":
				return BETA;
			case "GAMMA":
				return GAMMA;
			case "DELTA":
				return DELTA;
			default:
				return new Suffix(-1, uppercaseText);
			}
		}
	}
}
