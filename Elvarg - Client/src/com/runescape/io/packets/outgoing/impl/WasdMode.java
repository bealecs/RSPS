package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.ByteBuffer;
import com.runescape.io.packets.outgoing.OutgoingPacket;

public class WasdMode implements OutgoingPacket {

	private boolean enabled;

	public WasdMode(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void buildPacket(ByteBuffer buf) {
		buf.putOpcode(241);
		buf.putByte(enabled ? 1 : 0);
	}
}
