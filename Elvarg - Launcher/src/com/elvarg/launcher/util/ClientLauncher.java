package com.elvarg.launcher.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Launches the actual game client
 */
public class ClientLauncher {

    private SettingsManager settings;

    public ClientLauncher(SettingsManager settings) {
        this.settings = settings;
    }

    /**
     * Launch the client with the given parameters
     * @param clientJarPath Path to the client JAR file
     */
    public void launch(String clientJarPath) throws Exception {
        File clientJar = new File(clientJarPath);

        if (!clientJar.exists()) {
            throw new Exception("Client JAR not found: " + clientJarPath);
        }

        List<String> command = new ArrayList<>();
        command.add("java");

        // Add JVM arguments for better performance
        command.add("-Xmx512m"); // Max memory
        command.add("-Xms256m"); // Initial memory

        // Add the JAR
        command.add("-jar");
        command.add(clientJar.getAbsolutePath());

        // Add client arguments (your client can read these)
        String world = settings.getWorld();
        String graphicsMode = settings.getGraphicsMode();

        command.add("--world=" + world);
        command.add("--graphics=" + graphicsMode);
        command.add("--sound=" + settings.isSoundEnabled());
        command.add("--music=" + settings.isMusicEnabled());

        System.out.println("Launching client with command: " + String.join(" ", command));

        // Start the client process
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Optional: Monitor the client output
        new Thread(() -> {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[CLIENT] " + line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        System.out.println("Client launched successfully!");
    }
}
