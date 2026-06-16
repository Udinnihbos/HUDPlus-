package com.hudplus.hud;

import com.hudplus.config.HudConfig;
import com.hudplus.waypoint.Waypoint;
import com.hudplus.waypoint.WaypointManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class HudRenderer {

    public static final int BG_COLOR = 0x88000000;
    public static final int TEXT_WHITE = 0xFFFFFFFF;
    public static final int ELEMENT_H = 11;
    public static final int ELEMENT_PAD = 3;

    public static void render(DrawContext ctx, float tickDelta, MinecraftClient client) {
        HudConfig cfg = HudConfig.get();
        if (!cfg.hudVisible) return;
        if (client.player == null || client.world == null) return;

        ClientPlayerEntity player = client.player;
        ClientWorld world = client.world;
        TextRenderer font = client.textRenderer;
        int screenW = client.getWindow().getScaledWidth();

        cfg.initDefaultPositions(screenW);

        // ─── DAYS ───
        if (cfg.showDays) {
            long day = world.getTimeOfDay() / 24000L;
            String text = "§e☀ Day: §f" + day;
            int w = font.getWidth(text) + ELEMENT_PAD * 2;
            ctx.fill(cfg.daysX, cfg.daysY, cfg.daysX + w, cfg.daysY + ELEMENT_H, BG_COLOR);
            ctx.drawTextWithShadow(font, text, cfg.daysX + ELEMENT_PAD, cfg.daysY + 2, TEXT_WHITE);
        }

        // ─── COORDS ───
        if (cfg.showCoords) {
            BlockPos pos = player.getBlockPos();
            String text = String.format("§bX:§f%d §bY:§f%d §bZ:§f%d", pos.getX(), pos.getY(), pos.getZ());
            int w = font.getWidth(text) + ELEMENT_PAD * 2;
            ctx.fill(cfg.coordsX, cfg.coordsY, cfg.coordsX + w, cfg.coordsY + ELEMENT_H, BG_COLOR);
            ctx.drawTextWithShadow(font, text, cfg.coordsX + ELEMENT_PAD, cfg.coordsY + 2, TEXT_WHITE);
        }

        // ─── FPS ───
        if (cfg.showFps) {
            int fps = client.getCurrentFps();
            String fpsColor = fps >= 60 ? "§a" : fps >= 30 ? "§e" : "§c";
            String text = fpsColor + "FPS: §f" + fps;
            int w = font.getWidth(text) + ELEMENT_PAD * 2;
            ctx.fill(cfg.fpsX, cfg.fpsY, cfg.fpsX + w, cfg.fpsY + ELEMENT_H, BG_COLOR);
            ctx.drawTextWithShadow(font, text, cfg.fpsX + ELEMENT_PAD, cfg.fpsY + 2, TEXT_WHITE);
        }

        // ─── CLOCK ───
        if (cfg.showClock) {
            long ticks = world.getTimeOfDay() % 24000L;
            long totalMinutes = (ticks * 1440L / 24000L + 360L) % 1440L;
            int hours = (int)(totalMinutes / 60);
            int mins = (int)(totalMinutes % 60);
            String text = String.format("§d⏰ %02d:%02d", hours, mins);
            int w = font.getWidth(text) + ELEMENT_PAD * 2;
            ctx.fill(cfg.clockX, cfg.clockY, cfg.clockX + w, cfg.clockY + ELEMENT_H, BG_COLOR);
            ctx.drawTextWithShadow(font, text, cfg.clockX + ELEMENT_PAD, cfg.clockY + 2, TEXT_WHITE);
        }

        // ─── COMPASS ───
        if (cfg.showCompass) {
            String dir = getCompassDirection(player.getYaw(tickDelta));
            String text = "§6➤ " + dir;
            int w = font.getWidth(text) + ELEMENT_PAD * 2;
            ctx.fill(cfg.compassX, cfg.compassY, cfg.compassX + w, cfg.compassY + ELEMENT_H, BG_COLOR);
            ctx.drawTextWithShadow(font, text, cfg.compassX + ELEMENT_PAD, cfg.compassY + 2, TEXT_WHITE);
        }

        // ─── WAYPOINT LIST ───
        if (cfg.showWaypointLabels) {
            renderWaypointList(ctx, font, player, world, cfg);
        }
    }

    private static void renderWaypointList(DrawContext ctx, TextRenderer font,
            ClientPlayerEntity player, ClientWorld world, HudConfig cfg) {
        List<Waypoint> wps = WaypointManager.getInstance().getWaypoints();
        if (wps.isEmpty()) return;

        String currentDim = getDimensionName(world);
        BlockPos playerPos = player.getBlockPos();

        int x = cfg.waypointX;
        int y = cfg.waypointY;

        for (Waypoint wp : wps) {
            if (!wp.isVisible() || !wp.getDimension().equals(currentDim)) continue;
            int dist = wp.getDistance(playerPos);
            float[] rgb = wp.getColorRGB();
            int argb = 0xFF000000
                | ((int)(rgb[0] * 255) << 16)
                | ((int)(rgb[1] * 255) << 8)
                | (int)(rgb[2] * 255);

            String label = String.format("§f%s §7| §f%d,%d,%d §8(%dm)",
                wp.getName(), wp.getPos().getX(), wp.getPos().getY(), wp.getPos().getZ(), dist);

            int w = font.getWidth("● " + label) + ELEMENT_PAD * 2;
            ctx.fill(x, y, x + w, y + ELEMENT_H, BG_COLOR);
            ctx.drawTextWithShadow(font, "● ", x + ELEMENT_PAD, y + 2, argb);
            ctx.drawTextWithShadow(font, label, x + ELEMENT_PAD + 8, y + 2, TEXT_WHITE);
            y += ELEMENT_H + 1;
        }
    }

    public static void renderWorldWaypoints(WorldRenderContext context) {
        HudConfig cfg = HudConfig.get();
        if (!cfg.hudVisible || !cfg.showWaypointBeacons) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        String currentDim = getDimensionName(client.world);
        List<Waypoint> wps = WaypointManager.getInstance().getWaypoints();

        Camera camera = context.camera();
        double camX = camera.getPos().x;
        double camY = camera.getPos().y;
        double camZ = camera.getPos().z;

        VertexConsumerProvider.Immediate immediate =
            client.getBufferBuilders().getEntityVertexConsumers();

        for (Waypoint wp : wps) {
            if (!wp.isVisible() || !wp.getDimension().equals(currentDim)) continue;

            BlockPos pos = wp.getPos();
            double dx = pos.getX() + 0.5 - camX;
            double dy = pos.getY() + 0.5 - camY;
            double dz = pos.getZ() + 0.5 - camZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            float[] rgb = wp.getColorRGB();
            RenderLayer layer = RenderLayer.getDebugLineStrip(2.0);

            MatrixStack matrices = context.matrixStack();
            if (matrices == null) continue;
            matrices.push();
            matrices.translate(dx, dy, dz);

            VertexConsumer vc = immediate.getBuffer(layer);
            org.joml.Matrix4f matrix = matrices.peek().getPositionMatrix();
            for (float t = 0; t <= 64; t += 1f) {
                vc.vertex(matrix, 0f, t, 0f)
                  .color(rgb[0], rgb[1], rgb[2], Math.max(0f, 1f - (float)dist / 512f))
                  .normal(0, 1, 0);
            }

            immediate.draw(layer);
            matrices.pop();
        }
    }

    public static String getCompassDirection(float yaw) {
        yaw = ((yaw % 360) + 360) % 360;
        String[] dirs = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
        return dirs[Math.round(yaw / 45f) % 8];
    }

    public static String getDimensionName(ClientWorld world) {
        String key = world.getRegistryKey().getValue().toString();
        if (key.contains("nether")) return "nether";
        if (key.contains("end")) return "end";
        return "overworld";
    }
}
