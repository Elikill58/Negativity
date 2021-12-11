package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProxyPingMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 1;

	private int protocol = 0;
	private List<String> plugins = new ArrayList<>();

	public ProxyPingMessage() {
	}

	public ProxyPingMessage(int protocol) {
		this.protocol = protocol;
	}

	public ProxyPingMessage(int protocol, List<String> plugins) {
		this.protocol = protocol;
		this.plugins = plugins;
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		protocol = input.readInt();
		int nbPlugins = input.readInt();
		for(int i = 0; i < nbPlugins; i++)
			plugins.add(input.readUTF());
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeInt(protocol);
		output.writeInt(plugins.size());
		for(String pl : plugins)
			output.writeUTF(pl);
	}

	public int getProtocol() {
		return protocol;
	}

	/**
	 * Get all plugins on proxy
	 * 
	 * @return list of proxy plugins
	 */
	public List<String> getPlugins() {
		return plugins;
	}
}
