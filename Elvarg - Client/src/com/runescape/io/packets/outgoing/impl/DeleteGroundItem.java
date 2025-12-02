package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.ByteBuffer;
import com.runescape.io.packets.outgoing.OutgoingPacket;

/**
 * Packet for deleting ground items from the game world
 */
public class DeleteGroundItem implements OutgoingPacket {

	int itemId;
	int x;
	int y;
	
	public DeleteGroundItem(int y, int itemId, int x) {
		this.itemId = itemId;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void buildPacket(ByteBuffer buf) {
		buf.putOpcode(201); // Custom opcode for delete ground item
		buf.putShort(y);
		buf.putShort(itemId);
		buf.putShort(x);
	}
}
