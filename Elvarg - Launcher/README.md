# Elvarg Launcher

A modern game launcher for Elvarg RSPS with auto-update capabilities.

## Features

- ✅ Auto-update system (checks for new client versions)
- ✅ User login with saved credentials
- ✅ World selection
- ✅ Graphics and audio settings
- ✅ Progress tracking for downloads
- ✅ Modern JavaFX GUI

## Setup Instructions

### 1. Install Dependencies

You need:
- **Java 8+** (with JavaFX included, or add JavaFX separately for Java 11+)
- **GSON library** for JSON parsing

Download GSON:
```
https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar
```

Create a `lib` folder and place `gson-2.8.9.jar` inside.

### 2. Update Configuration

Edit `VersionManager.java`:
```java
private static final String VERSION_URL = "http://yourserver.com/version.json";
```

Replace with your actual server URL.

### 3. Build the Launcher

**Windows:**
```bash
build.bat
```

**Linux/Mac:**
```bash
javac -d bin -cp "lib/*" src/com/elvarg/launcher/**/*.java
cd bin
jar cvfe Elvarg-Launcher.jar com.elvarg.launcher.ui.LauncherGUI com/elvarg/launcher/**/*.class
```

### 4. Host Your Files

Upload to your server:
- `version.json` - version information
- `Elvarg-Client.jar` - your game client

Example `version.json`:
```json
{
  "version": "1.0.0",
  "url": "http://yourserver.com/downloads/Elvarg-Client.jar"
}
```

### 5. Run the Launcher

```bash
java -jar Elvarg-Launcher.jar
```

## How It Works

1. **Launcher starts** → Checks `version.txt` locally
2. **Fetches** `version.json` from server
3. **Compares versions** → Downloads client if needed
4. **User logs in** → Saves preferences
5. **Play button** → Launches `Elvarg-Client.jar` with settings

## File Structure

```
Elvarg - Launcher/
├── src/
│   └── com/elvarg/launcher/
│       ├── ui/
│       │   └── LauncherGUI.java        (Main GUI)
│       └── util/
│           ├── VersionManager.java     (Update checker)
│           ├── DownloadManager.java    (Client downloader)
│           ├── SettingsManager.java    (User preferences)
│           └── ClientLauncher.java     (Game starter)
├── lib/
│   └── gson-2.8.9.jar                 (JSON library)
├── resources/
│   └── version.json                    (Example)
└── build.bat                           (Build script)
```

## Customization

### Change Launcher Appearance

Edit `LauncherGUI.java`:
```java
root.setStyle("-fx-background-color: #2C2F33;"); // Background color
titleLabel.setStyle("-fx-text-fill: #7289DA;");  // Title color
```

### Add More Worlds

Edit `createSettingsSection()`:
```java
worldSelector.getItems().addAll("World 1", "World 2", "World 3", "PvP World");
```

### Modify JVM Arguments

Edit `ClientLauncher.java`:
```java
command.add("-Xmx1024m"); // Increase max memory
command.add("-Xms512m");  // Increase initial memory
```

## Troubleshooting

**"Client JAR not found"**
- Ensure `version.json` URL is correct
- Check internet connection
- Verify server is hosting the client JAR

**"Failed to check for updates"**
- Update `VERSION_URL` in `VersionManager.java`
- Ensure `version.json` is valid JSON
- Check server permissions

**JavaFX not found**
- Java 8: JavaFX is included
- Java 11+: Add JavaFX modules separately

## License

This launcher is for use with Elvarg RSPS.
