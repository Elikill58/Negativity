package com.elikill58.negativity.api.packets.nms.channels.netty;

import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class NettyChannel extends AbstractChannel {

	private Channel channel;
	
	public NettyChannel(Channel channel) {
		this.channel = channel;
	}
	
	@Override
	public void write(ByteBuf buf) {
		channel.writeAndFlush(buf);
	}
}
