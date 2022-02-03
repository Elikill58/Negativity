package com.elikill58.negativity.universal.multiproxy;

public class MultiProxyManager {

	private static MultiProxy multiProxy = null;
	public static MultiProxy getMultiProxy() {
		return multiProxy;
	}
	
	public static void setMultiProxy(MultiProxy multiProxy) {
		MultiProxyManager.multiProxy = multiProxy;
	}
	
	public static boolean isUsingMultiProxy() {
		return multiProxy != null && multiProxy.isMultiProxy();
	}
}
