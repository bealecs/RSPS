package com.elvarg.launcher.util;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Handles downloading the client JAR
 */
public class DownloadManager {

    public interface DownloadListener {
        void onProgress(int percentage);
        void onComplete();
        void onError(String message);
    }

    // Change this to your actual client JAR path for testing
    private static final String CLIENT_JAR = "../Elvarg - Client/Elvarg-Client.jar";

    /**
     * Download the client from the given URL
     * @param downloadUrl The URL to download from
     * @param listener Progress listener
     */
    public void downloadClient(String downloadUrl, DownloadListener listener) {
        new Thread(() -> {
            try {
                URL url = new URL(downloadUrl);
                URLConnection connection = url.openConnection();
                int fileSize = connection.getContentLength();

                try (InputStream in = new BufferedInputStream(connection.getInputStream());
                     FileOutputStream out = new FileOutputStream(CLIENT_JAR)) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    int totalBytesRead = 0;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        // Calculate and report progress
                        if (fileSize > 0) {
                            int percentage = (int) ((totalBytesRead * 100L) / fileSize);
                            listener.onProgress(percentage);
                        }
                    }

                    System.out.println("Download complete: " + CLIENT_JAR);
                    listener.onComplete();
                }

            } catch (Exception e) {
                System.err.println("Download failed: " + e.getMessage());
                listener.onError(e.getMessage());
            }
        }).start();
    }

    /**
     * Check if client JAR exists locally
     */
    public boolean clientExists() {
        return new java.io.File(CLIENT_JAR).exists();
    }

    public String getClientPath() {
        return CLIENT_JAR;
    }
}
