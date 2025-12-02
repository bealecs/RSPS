package com.runescape.io.packets.outgoing.impl;

import com.runescape.io.ByteBuffer;
import com.runescape.io.packets.outgoing.OutgoingPacket;

/**
 * Packet for deleting objects from the game world
 */
public class DeleteObject implements OutgoingPacket {

	int objectId;
	int x;
	int y;
	
	public DeleteObject(int x, int objectId, int y) {
		this.objectId = objectId;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void buildPacket(ByteBuffer buf) {
		buf.putOpcode(200); // Custom opcode for delete object
		buf.writeSignedBigEndian(x);
		buf.putShort(objectId);
		buf.writeUnsignedWordA(y);
	}
}
