package com.elvarg.launcher.util;

import java.io.*;
import java.util.Properties;

/**
 * Manages user preferences and settings
 */
public class SettingsManager {

    private static final String SETTINGS_FILE = "./launcher-settings.properties";
    private Properties properties;

    public SettingsManager() {
        properties = new Properties();
        loadSettings();
    }

    /**
     * Load settings from file
     */
    private void loadSettings() {
        try (FileInputStream fis = new FileInputStream(SETTINGS_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            // File doesn't exist yet, use defaults
            setDefaults();
        }
    }

    /**
     * Set default settings
     */
    private void setDefaults() {
        properties.setProperty("username", "");
        properties.setProperty("remember_username", "false");
        properties.setProperty("world", "World 1");
        properties.setProperty("graphics_mode", "High");
        properties.setProperty("sound_enabled", "true");
        properties.setProperty("music_enabled", "true");
        saveSettings();
    }

    /**
     * Save settings to file
     */
    public void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(fos, "Elvarg Launcher Settings");
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }

    // Getters and setters for common settings
    public String getUsername() {
        return properties.getProperty("username", "");
    }

    public void setUsername(String username) {
        properties.setProperty("username", username);
    }

    public boolean getRememberUsername() {
        return Boolean.parseBoolean(properties.getProperty("remember_username", "false"));
    }

    public void setRememberUsername(boolean remember) {
        properties.setProperty("remember_username", String.valueOf(remember));
    }

    public String getWorld() {
        return properties.getProperty("world", "World 1");
    }

    public void setWorld(String world) {
        properties.setProperty("world", world);
    }

    public String getGraphicsMode() {
        return properties.getProperty("graphics_mode", "High");
    }

    public void setGraphicsMode(String mode) {
        properties.setProperty("graphics_mode", mode);
    }

    public boolean isSoundEnabled() {
        return Boolean.parseBoolean(properties.getProperty("sound_enabled", "true"));
    }

    public void setSoundEnabled(boolean enabled) {
        properties.setProperty("sound_enabled", String.valueOf(enabled));
    }

    public boolean isMusicEnabled() {
        return Boolean.parseBoolean(properties.getProperty("music_enabled", "true"));
    }

    public void setMusicEnabled(boolean enabled) {
        properties.setProperty("music_enabled", String.valueOf(enabled));
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }
}
