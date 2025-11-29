package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.entity.impl.player.Player;

public class HotkeyMappingPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int hotkeyIndex = packet.readByte();
		int actionId = packet.readByte();

		if (hotkeyIndex >= 0 && hotkeyIndex < 9) {
			player.getHotkeyMappings()[hotkeyIndex] = actionId;
		}
	}
}
