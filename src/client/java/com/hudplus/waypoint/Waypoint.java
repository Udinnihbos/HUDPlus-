package com.hudplus.waypoint;

import net.minecraft.util.math.BlockPos;
import java.util.UUID;

public class Waypoint {
    private String id;
    private String name;
    private int color; // ARGB
    private BlockPos pos;
    private boolean visible;
    private String dimension;

    public Waypoint(String name, int color, BlockPos pos, String dimension) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.color = color;
        this.pos = pos;
        this.visible = true;
        this.dimension = dimension;
    }

    // For loading from JSON
    public Waypoint(String id, String name, int color, BlockPos pos, boolean visible, String dimension) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.pos = pos;
        this.visible = visible;
        this.dimension = dimension;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public BlockPos getPos() { return pos; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public String getDimension() { return dimension; }

    public float[] getColorRGB() {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return new float[]{r / 255f, g / 255f, b / 255f};
    }

    public int getDistance(BlockPos playerPos) {
        return (int) Math.sqrt(playerPos.getSquaredDistance(pos));
    }
}
