package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.entity.impl.player.Player;

/**
 * Handles the WASD camera mode toggle packet from the client.
 */
public class WasdModePacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		boolean enabled = packet.readByte() == 1;
		player.setWasdMode(enabled);
		player.getPacketSender().sendMessage("WASD Camera control is now " + (enabled ? "enabled" : "disabled") + ".");
	}
}
