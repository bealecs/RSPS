package com.elvarg.launcher.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Manages version checking and updates
 */
public class VersionManager {

    private static final String VERSION_FILE = "./version.txt";
    private static final String VERSION_URL = "http://yourserver.com/version.json"; // Change this to your server

    private String localVersion = "0.0.0";
    private String remoteVersion = "0.0.0";
    private String downloadUrl = "";

    public VersionManager() {
        loadLocalVersion();
    }

    /**
     * Load the local version from file
     */
    private void loadLocalVersion() {
        try {
            if (Files.exists(Paths.get(VERSION_FILE))) {
                localVersion = new String(Files.readAllBytes(Paths.get(VERSION_FILE))).trim();
            }
        } catch (IOException e) {
            System.out.println("No local version file found, assuming first launch.");
        }
    }

    /**
     * Check for updates from the server
     * @return true if update is available
     */
    public boolean checkForUpdate() {
        try {
            URL url = new URL(VERSION_URL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            // Parse JSON response
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(json.toString()).getAsJsonObject();

            remoteVersion = obj.get("version").getAsString();
            downloadUrl = obj.get("url").getAsString();

            System.out.println("Local version: " + localVersion);
            System.out.println("Remote version: " + remoteVersion);

            return !localVersion.equals(remoteVersion);

        } catch (Exception e) {
            System.err.println("Failed to check for updates: " + e.getMessage());
            return false;
        }
    }

    /**
     * Save the new version to local file
     */
    public void saveLocalVersion(String version) {
        try {
            Files.write(Paths.get(VERSION_FILE), version.getBytes());
            this.localVersion = version;
        } catch (IOException e) {
            System.err.println("Failed to save version: " + e.getMessage());
        }
    }

    public String getLocalVersion() {
        return localVersion;
    }

    public String getRemoteVersion() {
        return remoteVersion;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
