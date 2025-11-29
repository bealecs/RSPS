package com.elvarg.launcher.ui;

import com.elvarg.launcher.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Swing-based launcher GUI (works with any Java installation)
 */
public class SwingLauncher extends JFrame {

    private VersionManager versionManager;
    private DownloadManager downloadManager;
    private SettingsManager settingsManager;
    private ClientLauncher clientLauncher;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberCheckBox;
    private JComboBox<String> worldSelector;
    private JComboBox<String> graphicsSelector;
    private JCheckBox soundCheckBox;
    private JCheckBox musicCheckBox;
    private JButton playButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public SwingLauncher() {
        // Initialize managers
        versionManager = new VersionManager();
        downloadManager = new DownloadManager();
        settingsManager = new SettingsManager();
        clientLauncher = new ClientLauncher(settingsManager);

        // Setup window
        setTitle("Elvarg Launcher");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // Create UI
        initComponents();

        // Check for updates on startup (commented out for local testing)
        // checkForUpdates();

        // For testing: just check if client exists locally
        if (!downloadManager.clientExists()) {
            statusLabel.setText("Client not found! Check path in DownloadManager.java");
        } else {
            statusLabel.setText("Ready to play! (Local client found)");
        }
    }

    private void initComponents() {
        // Main panel with dark background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(44, 47, 51)); // #2C2F33
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("ELVARG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(114, 137, 218)); // #7289DA
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Version
        JLabel versionLabel = new JLabel("Version: " + versionManager.getLocalVersion());
        versionLabel.setForeground(new Color(153, 170, 181)); // #99AAB5
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(versionLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Login section
        mainPanel.add(createLoginPanel());
        mainPanel.add(Box.createVerticalStrut(20));

        // Settings section
        mainPanel.add(createSettingsPanel());
        mainPanel.add(Box.createVerticalStrut(20));

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setMaximumSize(new Dimension(350, 25));
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(progressBar);
        mainPanel.add(Box.createVerticalStrut(10));

        // Status label
        statusLabel = new JLabel("Ready to play!");
        statusLabel.setForeground(new Color(153, 170, 181));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Play button
        playButton = new JButton("PLAY NOW");
        playButton.setMaximumSize(new Dimension(350, 50));
        playButton.setFont(new Font("Arial", Font.BOLD, 18));
        playButton.setBackground(new Color(114, 137, 218));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(e -> onPlayButtonClicked());
        mainPanel.add(playButton);

        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(44, 47, 51));
        panel.setMaximumSize(new Dimension(350, 150));

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(300, 30));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        rememberCheckBox = new JCheckBox("Remember Username");
        rememberCheckBox.setBackground(new Color(44, 47, 51));
        rememberCheckBox.setForeground(Color.WHITE);
        rememberCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load saved settings
        if (settingsManager.getRememberUsername()) {
            usernameField.setText(settingsManager.getUsername());
            rememberCheckBox.setSelected(true);
        }

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(5));
        panel.add(rememberCheckBox);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(44, 47, 51));
        panel.setMaximumSize(new Dimension(350, 200));

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // World selector
        JPanel worldPanel = new JPanel(new FlowLayout());
        worldPanel.setBackground(new Color(44, 47, 51));
        JLabel worldLabel = new JLabel("World:");
        worldLabel.setForeground(Color.WHITE);
        worldSelector = new JComboBox<>(new String[]{"World 1", "World 2", "World 3"});
        worldSelector.setSelectedItem(settingsManager.getWorld());
        worldPanel.add(worldLabel);
        worldPanel.add(worldSelector);

        // Graphics selector
        JPanel graphicsPanel = new JPanel(new FlowLayout());
        graphicsPanel.setBackground(new Color(44, 47, 51));
        JLabel graphicsLabel = new JLabel("Graphics:");
        graphicsLabel.setForeground(Color.WHITE);
        graphicsSelector = new JComboBox<>(new String[]{"Low", "Medium", "High"});
        graphicsSelector.setSelectedItem(settingsManager.getGraphicsMode());
        graphicsPanel.add(graphicsLabel);
        graphicsPanel.add(graphicsSelector);

        // Sound options
        soundCheckBox = new JCheckBox("Sound Effects");
        soundCheckBox.setSelected(settingsManager.isSoundEnabled());
        soundCheckBox.setBackground(new Color(44, 47, 51));
        soundCheckBox.setForeground(Color.WHITE);
        soundCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        musicCheckBox = new JCheckBox("Music");
        musicCheckBox.setSelected(settingsManager.isMusicEnabled());
        musicCheckBox.setBackground(new Color(44, 47, 51));
        musicCheckBox.setForeground(Color.WHITE);
        musicCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(worldPanel);
        panel.add(graphicsPanel);
        panel.add(soundCheckBox);
        panel.add(musicCheckBox);

        return panel;
    }

    private void checkForUpdates() {
        statusLabel.setText("Checking for updates...");

        new Thread(() -> {
            boolean updateAvailable = versionManager.checkForUpdate();

            SwingUtilities.invokeLater(() -> {
                if (updateAvailable) {
                    statusLabel.setText("Update available: " + versionManager.getRemoteVersion());
                    downloadUpdate();
                } else if (!downloadManager.clientExists()) {
                    statusLabel.setText("Client not found. Downloading...");
                    downloadUpdate();
                } else {
                    statusLabel.setText("You're up to date!");
                }
            });
        }).start();
    }

    private void downloadUpdate() {
        progressBar.setVisible(true);
        playButton.setEnabled(false);

        downloadManager.downloadClient(versionManager.getDownloadUrl(), new DownloadManager.DownloadListener() {
            @Override
            public void onProgress(int percentage) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(percentage);
                    statusLabel.setText("Downloading... " + percentage + "%");
                });
            }

            @Override
            public void onComplete() {
                SwingUtilities.invokeLater(() -> {
                    versionManager.saveLocalVersion(versionManager.getRemoteVersion());
                    progressBar.setVisible(false);
                    statusLabel.setText("Download complete!");
                    playButton.setEnabled(true);
                });
            }

            @Override
            public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    statusLabel.setText("Error: " + message);
                    playButton.setEnabled(true);
                });
            }
        });
    }

    private void onPlayButtonClicked() {
        // Save settings
        if (rememberCheckBox.isSelected()) {
            settingsManager.setUsername(usernameField.getText());
            settingsManager.setRememberUsername(true);
        } else {
            settingsManager.setUsername("");
            settingsManager.setRememberUsername(false);
        }

        settingsManager.setWorld((String) worldSelector.getSelectedItem());
        settingsManager.setGraphicsMode((String) graphicsSelector.getSelectedItem());
        settingsManager.setSoundEnabled(soundCheckBox.isSelected());
        settingsManager.setMusicEnabled(musicCheckBox.isSelected());
        settingsManager.saveSettings();

        // Validate login
        if (usernameField.getText().isEmpty() || passwordField.getPassword().length == 0) {
            statusLabel.setText("Please enter username and password!");
            return;
        }

        // Launch client
        try {
            statusLabel.setText("Launching client...");
            clientLauncher.launch(downloadManager.getClientPath());

            // Close launcher after successful launch
            Timer timer = new Timer(2000, e -> System.exit(0));
            timer.setRepeats(false);
            timer.start();

        } catch (Exception e) {
            statusLabel.setText("Failed to launch: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show GUI
        SwingUtilities.invokeLater(() -> {
            SwingLauncher launcher = new SwingLauncher();
            launcher.setVisible(true);
        });
    }
}
