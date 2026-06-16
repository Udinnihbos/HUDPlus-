package com.hudplus.config;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;

public class HudConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("HudPlus/Config");
    private static HudConfig INSTANCE;
    private final Path file = Paths.get("config", "hudplus_config.json");

    // Toggles
    public boolean showCoords = true;
    public boolean showFps = true;
    public boolean showClock = true;
    public boolean showCompass = true;
    public boolean showDays = true;
    public boolean showWaypointLabels = true;
    public boolean showWaypointBeacons = true;
    public boolean hudVisible = true;

    // Positions (x, y) untuk tiap elemen - default values
    public int coordsX = 4;       public int coordsY = 4;
    public int fpsX = 4;          public int fpsY = 14;
    public int clockX = 4;        public int clockY = 24;
    public int compassX = 4;      public int compassY = 34;
    public int daysX = 4;         public int daysY = 44;
    public int waypointX = -1;    public int waypointY = 4; // -1 = kanan layar

    public static HudConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new HudConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    public void initDefaultPositions(int screenW) {
        // Set waypoint default ke kanan layar jika masih -1
        if (waypointX == -1) waypointX = screenW - 164;
    }

    public void save() {
        try {
            Files.createDirectories(file.getParent());
            JsonObject obj = new JsonObject();
            obj.addProperty("showCoords", showCoords);
            obj.addProperty("showFps", showFps);
            obj.addProperty("showClock", showClock);
            obj.addProperty("showCompass", showCompass);
            obj.addProperty("showDays", showDays);
            obj.addProperty("showWaypointLabels", showWaypointLabels);
            obj.addProperty("showWaypointBeacons", showWaypointBeacons);
            obj.addProperty("hudVisible", hudVisible);
            // Positions
            obj.addProperty("coordsX", coordsX); obj.addProperty("coordsY", coordsY);
            obj.addProperty("fpsX", fpsX); obj.addProperty("fpsY", fpsY);
            obj.addProperty("clockX", clockX); obj.addProperty("clockY", clockY);
            obj.addProperty("compassX", compassX); obj.addProperty("compassY", compassY);
            obj.addProperty("daysX", daysX); obj.addProperty("daysY", daysY);
            obj.addProperty("waypointX", waypointX); obj.addProperty("waypointY", waypointY);
            Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(obj));
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    public void load() {
        if (!Files.exists(file)) return;
        try {
            JsonObject obj = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
            if (obj.has("showCoords")) showCoords = obj.get("showCoords").getAsBoolean();
            if (obj.has("showFps")) showFps = obj.get("showFps").getAsBoolean();
            if (obj.has("showClock")) showClock = obj.get("showClock").getAsBoolean();
            if (obj.has("showCompass")) showCompass = obj.get("showCompass").getAsBoolean();
            if (obj.has("showDays")) showDays = obj.get("showDays").getAsBoolean();
            if (obj.has("showWaypointLabels")) showWaypointLabels = obj.get("showWaypointLabels").getAsBoolean();
            if (obj.has("showWaypointBeacons")) showWaypointBeacons = obj.get("showWaypointBeacons").getAsBoolean();
            if (obj.has("hudVisible")) hudVisible = obj.get("hudVisible").getAsBoolean();
            // Positions
            if (obj.has("coordsX")) coordsX = obj.get("coordsX").getAsInt();
            if (obj.has("coordsY")) coordsY = obj.get("coordsY").getAsInt();
            if (obj.has("fpsX")) fpsX = obj.get("fpsX").getAsInt();
            if (obj.has("fpsY")) fpsY = obj.get("fpsY").getAsInt();
            if (obj.has("clockX")) clockX = obj.get("clockX").getAsInt();
            if (obj.has("clockY")) clockY = obj.get("clockY").getAsInt();
            if (obj.has("compassX")) compassX = obj.get("compassX").getAsInt();
            if (obj.has("compassY")) compassY = obj.get("compassY").getAsInt();
            if (obj.has("daysX")) daysX = obj.get("daysX").getAsInt();
            if (obj.has("daysY")) daysY = obj.get("daysY").getAsInt();
            if (obj.has("waypointX")) waypointX = obj.get("waypointX").getAsInt();
            if (obj.has("waypointY")) waypointY = obj.get("waypointY").getAsInt();
        } catch (Exception e) {
            LOGGER.error("Failed to load config", e);
        }
    }
}
