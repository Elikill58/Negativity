package com.elikill58.negativity.common;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.universal.utils.UniversalUtils;

public abstract class Protocol {

	private static final List<Protocol> PROTOCOLS = new ArrayList<>();
	
	public static void loadProtocol() {
		try {
			String dir = Protocol.class.getProtectionDomain().getCodeSource().getLocation().getFile().replaceAll("%20", " ");
			if (dir.endsWith(".class"))
				dir = dir.substring(0, dir.lastIndexOf('!'));

			if (dir.startsWith("file:/"))
				dir = dir.substring(UniversalUtils.getOs() == UniversalUtils.OS.LINUX ? 5 : 6);

			for (Object classDir : UniversalUtils.getClasseNamesInPackage(dir, "com.elikill58.implement.protocols")) {
				try {
					Protocol prot = (Protocol) Class.forName(classDir.toString().replaceAll(".class", "")).newInstance();
					PROTOCOLS.add(prot);
				} catch (Exception temp) {
					// on ignore
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
