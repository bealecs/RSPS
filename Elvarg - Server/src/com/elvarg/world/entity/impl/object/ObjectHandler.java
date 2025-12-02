package com.elvarg.world.entity.impl.object;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.elvarg.world.World;
import com.elvarg.world.collision.region.RegionClipping;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Position;

public class ObjectHandler {

	/** All of the registered Objects. */
	private static List<GameObject> objects = new CopyOnWriteArrayList<GameObject>();

	/**
	 * Processes all game objects Deletes global objects after their timer ends
	 * etc.
	 */
	public static void process() {

		List<GameObject> toRemove = new ArrayList<GameObject>();
		for (GameObject obj : objects) {
			if (obj == null) {
				continue;
			}

			// Despawn global objects by adding them to a list
			if (obj.getTimer() != null && obj.getTimer().finished()) {
				toRemove.add(obj);
			}

		}

		// Go through the despawn list and remove the objects one by one.
		for (GameObject obj : toRemove) {
			despawnGlobalObject(obj);
		}
	}

	/**
	 * Spawns a global object for everyone on the server.
	 * 
	 * @param object
	 */
	public static void spawnGlobalObject(GameObject object) {
		// Check if this object has been permanently deleted
		if (DeletedObjectManager.isDeleted(object.getId(), object.getPosition())) {
			return; // Don't spawn deleted objects
		}
		
		objects.add(object);
		RegionClipping.addObject(object);

		// Spawn the object for all nearby players
		for (Player p : World.getPlayers()) {

			// Check that the player is in proper range
			if (p == null || !p.getPosition().isWithinDistance(object.getPosition(), DISTANCE_SPAWN)) {
				continue;
			}

			// Spawn the object
			spawnPersonalObject(p, object);
		}
	}

	/**
	 * Despawns (removes) a global object for everyone on the server.
	 * 
	 * @param object
	 */
	public static void despawnGlobalObject(GameObject object) {
		objects.remove(object);
		RegionClipping.removeObject(object);

		// Despawn the object for all nearby players
		for (Player p : World.getPlayers()) {

			// Check that the player is in proper range
			if (p == null || !p.getPosition().isWithinDistance(object.getPosition(), DISTANCE_SPAWN)) {
				continue;
			}

			// Despawn the object
			despawnPersonalObject(p, object);
		}
	}

	/**
	 * Spawns a personal object for one player on the server.
	 * 
	 * @param player
	 * @param object
	 */
	public static void spawnPersonalObject(Player player, GameObject object) {
		// Check if this object has been permanently deleted
		if (DeletedObjectManager.isDeleted(object.getId(), object.getPosition())) {
			return; // Don't spawn deleted objects
		}
		
		player.getPacketSender().sendObject(object);

		// Also add to regionclipping so they can interact with it
		if (!RegionClipping.objectExists(object)) {
			RegionClipping.addObject(object);
		}
	}

	/**
	 * Despawns (removes) a personal object for one player on the server.
	 * 
	 * @param player
	 * @param object
	 */
	public static void despawnPersonalObject(Player player, GameObject object) {
		player.getPacketSender().sendObjectRemoval(object);

		// Also remove from regionclipping so they cant interact with it
		if (!RegionClipping.objectExists(object)) {
			RegionClipping.removeObject(object);
		}
	}

	/**
	 * Spawns all objects in an area for a player.
	 * Also removes any deleted objects from the client's cache.
	 * 
	 * @param player
	 */
	public static void onRegionChange(Player player) {
		// First, send removal packets for all deleted objects in the area
		for (DeletedObjectManager.DeletedObject deleted : DeletedObjectManager.getDeletedObjects()) {
			Position pos = new Position(deleted.getX(), deleted.getY(), deleted.getZ());
			if (player.getPosition().isWithinDistance(pos, DISTANCE_SPAWN)) {
				GameObject obj = new GameObject(deleted.getId(), pos);
				player.getPacketSender().sendObjectRemoval(obj);
			}
		}
		
		// Then spawn the non-deleted objects
		for (GameObject obj : objects) {
			if (obj == null) {
				continue;
			}

			if (player.getPosition().isWithinDistance(obj.getPosition(), DISTANCE_SPAWN)) {
				spawnPersonalObject(player, obj);
			}
		}
	}

	/**
	 * Returns the list of all game objects.
	 * @return The objects list
	 */
	public static List<GameObject> getObjects() {
		return objects;
	}

	/***
	 * Spawn or despawn objects for entities within 70 squares of distance
	 */
	private static final int DISTANCE_SPAWN = 70;
}
