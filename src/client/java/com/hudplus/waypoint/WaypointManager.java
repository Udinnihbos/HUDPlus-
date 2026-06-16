package com.hudplus.waypoint;

import com.google.gson.*;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WaypointManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("HudPlus/WaypointManager");
    private static WaypointManager INSTANCE;
    private final List<Waypoint> waypoints = new ArrayList<>();
    private final Path saveFile;

    public static WaypointManager getInstance() {
        if (INSTANCE == null) INSTANCE = new WaypointManager();
        return INSTANCE;
    }

    private WaypointManager() {
        saveFile = Paths.get("config", "hudplus_waypoints.json");
        load();
    }

    public List<Waypoint> getWaypoints() {
        return Collections.unmodifiableList(waypoints);
    }

    public void addWaypoint(Waypoint wp) {
        waypoints.add(wp);
        save();
    }

    public void removeWaypoint(String id) {
        waypoints.removeIf(w -> w.getId().equals(id));
        save();
    }

    public void toggleWaypoint(String id) {
        waypoints.stream()
            .filter(w -> w.getId().equals(id))
            .findFirst()
            .ifPresent(w -> {
                w.setVisible(!w.isVisible());
                save();
            });
    }

    public void save() {
        try {
            Files.createDirectories(saveFile.getParent());
            JsonArray arr = new JsonArray();
            for (Waypoint wp : waypoints) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", wp.getId());
                obj.addProperty("name", wp.getName());
                obj.addProperty("color", wp.getColor());
                obj.addProperty("x", wp.getPos().getX());
                obj.addProperty("y", wp.getPos().getY());
                obj.addProperty("z", wp.getPos().getZ());
                obj.addProperty("visible", wp.isVisible());
                obj.addProperty("dimension", wp.getDimension());
                arr.add(obj);
            }
            Files.writeString(saveFile, new GsonBuilder().setPrettyPrinting().create().toJson(arr));
        } catch (IOException e) {
            LOGGER.error("Failed to save waypoints", e);
        }
    }

    public void load() {
        waypoints.clear();
        if (!Files.exists(saveFile)) return;
        try {
            String json = Files.readString(saveFile);
            JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String id = obj.get("id").getAsString();
                String name = obj.get("name").getAsString();
                int color = obj.get("color").getAsInt();
                int x = obj.get("x").getAsInt();
                int y = obj.get("y").getAsInt();
                int z = obj.get("z").getAsInt();
                boolean visible = obj.get("visible").getAsBoolean();
                String dim = obj.has("dimension") ? obj.get("dimension").getAsString() : "overworld";
                waypoints.add(new Waypoint(id, name, color, new BlockPos(x, y, z), visible, dim));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load waypoints", e);
        }
    }
}
