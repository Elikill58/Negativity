package com.elikill58.negativity.universal.detections.keys;

import java.nio.file.Path;
import java.util.Locale;

import com.elikill58.negativity.universal.Version;

public interface IDetectionKeys<T extends IDetectionKeys<T>> extends Comparable<T> {

	public Path getFolder();
	
	public String getPathBundle();
	
	public String getKey();
	
	public Version getMinVersion();
	
	public default String getLowerKey() {
		return getKey().toLowerCase(Locale.ROOT);
	}
}
