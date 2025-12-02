package com.elvarg.net.packet.impl;

import com.elvarg.net.packet.Packet;
import com.elvarg.net.packet.PacketListener;
import com.elvarg.world.collision.region.RegionClipping;
import com.elvarg.world.entity.impl.object.DeletedObjectManager;
import com.elvarg.world.entity.impl.object.GameObject;
import com.elvarg.world.entity.impl.object.ObjectHandler;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.grounditems.GroundItemManager;
import com.elvarg.world.model.GroundItem;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.PlayerRights;
import com.elvarg.world.model.Position;

/**
 * This packet listener handles deletion of objects and ground items by admins.
 * Used for clearing areas to create new home zones.
 * 
 * @author AI Assistant
 */
public class DeletePacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		// Only admins can delete objects
		if (player.getRights().ordinal() < PlayerRights.ADMINISTRATOR.ordinal()) {
			player.getPacketSender().sendMessage("You don't have permission to delete objects.");
			return;
		}
		
		int opcode = packet.getOpcode();
		
		// Delete Object (opcode 200)
		if (opcode == 200) {
			handleDeleteObject(player, packet);
		}
		// Delete Ground Item (opcode 201)
		else if (opcode == 201) {
			handleDeleteGroundItem(player, packet);
		}
	}
	
	private void handleDeleteObject(Player player, Packet packet) {
		final int x = packet.readLEShortA();
		final int id = packet.readUnsignedShort();
		final int y = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		
		// Create the game object to delete
		GameObject objectToDelete = new GameObject(id, position);
		
		// Remove the object globally
		ObjectHandler.despawnGlobalObject(objectToDelete);
		
		// Add to permanent deletion list
		DeletedObjectManager.addDeletedObject(objectToDelete);
		
		player.getPacketSender().sendMessage("Permanently deleted object " + id + " at position " + position.toString());
		
		// Log the deletion
		System.out.println("[DELETE] " + player.getUsername() + " permanently deleted object " + id + 
				" at position " + position.toString());
	}
	
	private void handleDeleteGroundItem(Player player, Packet packet) {
		final int y = packet.readUnsignedShort();
		final int itemId = packet.readUnsignedShort();
		final int x = packet.readUnsignedShort();
		final Position position = new Position(x, y, player.getPosition().getZ());
		
		// Find and remove the ground item
		Item item = new Item(itemId);
		GroundItem groundItem = GroundItemManager.getGroundItem(player, item, position);
		
		if (groundItem != null) {
			// Remove the item
			GroundItemManager.remove(groundItem, true);
			
			player.getPacketSender().sendMessage("Deleted ground item " + itemId + " at position " + position.toString());
			
			// Log the deletion
			System.out.println("[DELETE] " + player.getUsername() + " deleted ground item " + itemId + 
					" at position " + position.toString());
		} else {
			player.getPacketSender().sendMessage("Ground item not found at this location.");
		}
	}
}
