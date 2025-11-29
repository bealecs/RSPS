package com.elvarg.launcher;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Simple launcher test - no dependencies needed
 */
public class LauncherTest extends Application {

    @Override
    public void start(Stage primaryStage) {
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
        Label versionLabel = new Label("Version: 1.0.0 (TEST MODE)");
        versionLabel.setStyle("-fx-text-fill: #99AAB5;");

        // Login section
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);

        Label loginLabel = new Label("Login");
        loginLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        loginLabel.setStyle("-fx-text-fill: white;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(300);

        CheckBox rememberCheckBox = new CheckBox("Remember Username");
        rememberCheckBox.setStyle("-fx-text-fill: white;");

        loginBox.getChildren().addAll(loginLabel, usernameField, passwordField, rememberCheckBox);

        // Settings section
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
        ComboBox<String> worldSelector = new ComboBox<>();
        worldSelector.getItems().addAll("World 1", "World 2", "World 3");
        worldSelector.setValue("World 1");
        worldBox.getChildren().addAll(worldLabel, worldSelector);

        // Graphics selector
        HBox graphicsBox = new HBox(10);
        graphicsBox.setAlignment(Pos.CENTER);
        Label graphicsLabel = new Label("Graphics:");
        graphicsLabel.setStyle("-fx-text-fill: white;");
        ComboBox<String> graphicsSelector = new ComboBox<>();
        graphicsSelector.getItems().addAll("Low", "Medium", "High");
        graphicsSelector.setValue("High");
        graphicsBox.getChildren().addAll(graphicsLabel, graphicsSelector);

        // Sound options
        CheckBox soundCheckBox = new CheckBox("Sound Effects");
        soundCheckBox.setSelected(true);
        soundCheckBox.setStyle("-fx-text-fill: white;");

        CheckBox musicCheckBox = new CheckBox("Music");
        musicCheckBox.setSelected(true);
        musicCheckBox.setStyle("-fx-text-fill: white;");

        settingsBox.getChildren().addAll(settingsLabel, worldBox, graphicsBox, soundCheckBox, musicCheckBox);

        // Progress bar
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setVisible(false);

        Label statusLabel = new Label("Ready to play!");
        statusLabel.setStyle("-fx-text-fill: #99AAB5;");

        // Play button
        Button playButton = new Button("PLAY NOW");
        playButton.setPrefSize(300, 50);
        playButton.setStyle("-fx-background-color: #7289DA; -fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        playButton.setOnAction(e -> {
            if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                statusLabel.setText("Please enter username and password!");
            } else {
                statusLabel.setText("Would launch client with: " + usernameField.getText() + " on " + worldSelector.getValue());
            }
        });

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
        primaryStage.setTitle("Elvarg Launcher (Test)");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
