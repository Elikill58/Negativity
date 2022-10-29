package com.elikill58.negativity.api.packets.nms.channels;

import io.netty.buffer.ByteBuf;

public abstract class AbstractChannel {
	
	public abstract void write(ByteBuf buf);
	
}
