package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.ByteBuffer;
import com.runescape.io.packets.outgoing.OutgoingPacket;

public class HotkeyMapping implements OutgoingPacket {

	private int hotkeyIndex;
	private int actionId;

	public HotkeyMapping(int hotkeyIndex, int actionId) {
		this.hotkeyIndex = hotkeyIndex;
		this.actionId = actionId;
	}

	@Override
	public void buildPacket(ByteBuffer buf) {
		buf.putOpcode(242);
		buf.putByte(hotkeyIndex);
		buf.putByte(actionId);
	}
}
