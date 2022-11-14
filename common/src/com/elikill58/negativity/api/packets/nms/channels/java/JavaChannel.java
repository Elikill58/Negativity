package com.elikill58.negativity.api.packets.nms.channels.java;

import java.nio.channels.SocketChannel;

import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;

import io.netty.buffer.ByteBuf;

public class JavaChannel extends AbstractChannel {

	private SocketChannel channel;
	
	public JavaChannel(SocketChannel channel) {
		this.channel = channel;
	}
	
	@Override
	public void write(ByteBuf buf) {
		try {
			channel.write(buf.nioBuffer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
