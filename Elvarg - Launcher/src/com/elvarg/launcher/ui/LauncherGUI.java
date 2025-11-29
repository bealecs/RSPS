package com.elvarg.launcher.ui;

import com.elvarg.launcher.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main launcher GUI using JavaFX
 */
public class LauncherGUI extends Application {

    private VersionManager versionManager;
    private DownloadManager downloadManager;
    private SettingsManager settingsManager;
    private ClientLauncher clientLauncher;

    private TextField usernameField;
    private PasswordField passwordField;
    private CheckBox rememberCheckBox;
    private ComboBox<String> worldSelector;
    private ComboBox<String> graphicsSelector;
    private CheckBox soundCheckBox;
    private CheckBox musicCheckBox;
    private Button playButton;
    private ProgressBar progressBar;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        // Initialize managers
        versionManager = new VersionManager();
        downloadManager = new DownloadManager();
        settingsManager = new SettingsManager();
        clientLauncher = new ClientLauncher(settingsManager);

        // Create main layout
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2C2F33;");

        // Title
        Label titleLabel = new Label("ELVARG");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #7289DA;");

        // Version label
        Label versionLabel = new Label("Version: " + versionManager.getLocalVersion());
        versionLabel.setStyle("-fx-text-fill: #99AAB5;");

        // Login section
        VBox loginBox = createLoginSection();

        // Settings section
        VBox settingsBox = createSettingsSection();

        // Progress section
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setVisible(false);

        statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #99AAB5;");

        // Play button
        playButton = new Button("PLAY NOW");
        playButton.setPrefSize(300, 50);
        playButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        playButton.setOnAction(e -> onPlayButtonClicked());

        // Add all components
        root.getChildren().addAll(
                titleLabel,
                versionLabel,
                loginBox,
                settingsBox,
                progressBar,
                statusLabel,
                playButton
        );

        // Create scene
        Scene scene = new Scene(root, 400, 600);
        primaryStage.setTitle("Elvarg Launcher");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Check for updates on startup
        checkForUpdates();
    }

    private VBox createLoginSection() {
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);

        Label loginLabel = new Label("Login");
        loginLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        loginLabel.setStyle("-fx-text-fill: white;");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(300);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(300);

        rememberCheckBox = new CheckBox("Remember Username");
        rememberCheckBox.setStyle("-fx-text-fill: white;");

        // Load saved username if remember is enabled
        if (settingsManager.getRememberUsername()) {
            usernameField.setText(settingsManager.getUsername());
            rememberCheckBox.setSelected(true);
        }

        loginBox.getChildren().addAll(loginLabel, usernameField, passwordField, rememberCheckBox);
        return loginBox;
    }

    private VBox createSettingsSection() {
        VBox settingsBox = new VBox(10);
        settingsBox.setAlignment(Pos.CENTER);

        Label settingsLabel = new Label("Settings");
        settingsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        settingsLabel.setStyle("-fx-text-fill: white;");

        // World selector
        HBox worldBox = new HBox(10);
        worldBox.setAlignment(Pos.CENTER);
        Label worldLabel = new Label("World:");
        worldLabel.setStyle("-fx-text-fill: white;");
        worldSelector = new ComboBox<>();
        worldSelector.getItems().addAll("World 1", "World 2", "World 3");
        worldSelector.setValue(settingsManager.getWorld());
        worldBox.getChildren().addAll(worldLabel, worldSelector);

        // Graphics selector
        HBox graphicsBox = new HBox(10);
        graphicsBox.setAlignment(Pos.CENTER);
        Label graphicsLabel = new Label("Graphics:");
        graphicsLabel.setStyle("-fx-text-fill: white;");
        graphicsSelector = new ComboBox<>();
        graphicsSelector.getItems().addAll("Low", "Medium", "High");
        graphicsSelector.setValue(settingsManager.getGraphicsMode());
        graphicsBox.getChildren().addAll(graphicsLabel, graphicsSelector);

        // Sound options
        soundCheckBox = new CheckBox("Sound Effects");
        soundCheckBox.setSelected(settingsManager.isSoundEnabled());
        soundCheckBox.setStyle("-fx-text-fill: white;");

        musicCheckBox = new CheckBox("Music");
        musicCheckBox.setSelected(settingsManager.isMusicEnabled());
        musicCheckBox.setStyle("-fx-text-fill: white;");

        settingsBox.getChildren().addAll(settingsLabel, worldBox, graphicsBox, soundCheckBox, musicCheckBox);
        return settingsBox;
    }

    private void checkForUpdates() {
        statusLabel.setText("Checking for updates...");

        new Thread(() -> {
            boolean updateAvailable = versionManager.checkForUpdate();

            Platform.runLater(() -> {
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
        playButton.setDisable(true);

        downloadManager.downloadClient(versionManager.getDownloadUrl(), new DownloadManager.DownloadListener() {
            @Override
            public void onProgress(int percentage) {
                Platform.runLater(() -> {
                    progressBar.setProgress(percentage / 100.0);
                    statusLabel.setText("Downloading... " + percentage + "%");
                });
            }

            @Override
            public void onComplete() {
                Platform.runLater(() -> {
                    versionManager.saveLocalVersion(versionManager.getRemoteVersion());
                    progressBar.setVisible(false);
                    statusLabel.setText("Download complete!");
                    playButton.setDisable(false);
                });
            }

            @Override
            public void onError(String message) {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    statusLabel.setText("Error: " + message);
                    playButton.setDisable(false);
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

        settingsManager.setWorld(worldSelector.getValue());
        settingsManager.setGraphicsMode(graphicsSelector.getValue());
        settingsManager.setSoundEnabled(soundCheckBox.isSelected());
        settingsManager.setMusicEnabled(musicCheckBox.isSelected());
        settingsManager.saveSettings();

        // Validate login
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            statusLabel.setText("Please enter username and password!");
            return;
        }

        // Launch client
        try {
            statusLabel.setText("Launching client...");
            clientLauncher.launch(downloadManager.getClientPath());

            // Close launcher after successful launch
            Platform.runLater(() -> {
                try {
                    Thread.sleep(2000); // Give client time to start
                    Platform.exit();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            statusLabel.setText("Failed to launch: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
