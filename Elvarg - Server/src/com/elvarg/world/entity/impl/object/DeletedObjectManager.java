package com.elvarg.world.entity.impl.object;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import com.elvarg.world.model.Position;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Manages permanently deleted objects
 * Objects deleted by admins are stored here and won't respawn
 */
public class DeletedObjectManager {
    
    private static final String DELETED_OBJECTS_FILE = "./data/saves/deleted_objects.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<DeletedObject> deletedObjects = new HashSet<>();
    
    /**
     * Represents a deleted object
     */
    public static class DeletedObject {
        private final int id;
        private final int x;
        private final int y;
        private final int z;
        
        public DeletedObject(int id, Position position) {
            this.id = id;
            this.x = position.getX();
            this.y = position.getY();
            this.z = position.getZ();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DeletedObject)) {
                return false;
            }
            DeletedObject other = (DeletedObject) obj;
            return id == other.id && x == other.x && y == other.y && z == other.z;
        }
        
        @Override
        public int hashCode() {
            return id * 100000 + x * 1000 + y * 10 + z;
        }
        
        public int getId() {
            return id;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public int getZ() {
            return z;
        }
    }
    
    /**
     * Load deleted objects from file
     */
    public static void load() {
        File file = new File(DELETED_OBJECTS_FILE);
        if (!file.exists()) {
            System.out.println("[DeletedObjects] No deleted objects file found, starting fresh.");
            return;
        }
        
        try (FileReader reader = new FileReader(file)) {
            Set<DeletedObject> loaded = GSON.fromJson(reader, new TypeToken<Set<DeletedObject>>(){}.getType());
            if (loaded != null) {
                deletedObjects.addAll(loaded);
                System.out.println("[DeletedObjects] Loaded " + deletedObjects.size() + " deleted objects.");
                for (DeletedObject obj : deletedObjects) {
                    System.out.println("[DeletedObjects]   - Object " + obj.getId() + " at (" + obj.getX() + ", " + obj.getY() + ", " + obj.getZ() + ")");
                }
            }
        } catch (Exception e) {
            System.err.println("[DeletedObjects] Error loading deleted objects: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Save deleted objects to file
     */
    public static void save() {
        try {
            System.out.println("[DeletedObjects] Saving " + deletedObjects.size() + " objects to " + DELETED_OBJECTS_FILE);
            File file = new File(DELETED_OBJECTS_FILE);
            file.getParentFile().mkdirs();
            
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(deletedObjects, writer);
            }
            
            System.out.println("[DeletedObjects] Saved " + deletedObjects.size() + " deleted objects.");
        } catch (Exception e) {
            System.err.println("[DeletedObjects] Error saving deleted objects: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Add an object to the deleted list
     */
    public static void addDeletedObject(GameObject object) {
        DeletedObject deleted = new DeletedObject(object.getId(), object.getPosition());
        deletedObjects.add(deleted);
        System.out.println("[DeletedObjects] Added object " + object.getId() + " at " + object.getPosition() + ". Total deleted: " + deletedObjects.size());
        save();
    }
    
    /**
     * Check if an object should be deleted (not spawned)
     */
    public static boolean isDeleted(int id, Position position) {
        DeletedObject check = new DeletedObject(id, position);
        boolean result = deletedObjects.contains(check);
        if (result) {
            System.out.println("[DeletedObjects] Blocking spawn of deleted object " + id + " at (" + position.getX() + ", " + position.getY() + ", " + position.getZ() + ")");
        }
        return result;
    }
    
    /**
     * Get all deleted objects
     */
    public static Set<DeletedObject> getDeletedObjects() {
        return deletedObjects;
    }
}
