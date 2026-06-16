package com.hudplus;

import com.hudplus.config.HudConfig;
import com.hudplus.hud.HudLayoutScreen;
import com.hudplus.hud.HudRenderer;
import com.hudplus.hud.WaypointAddScreen;
import com.hudplus.hud.WaypointListScreen;
import com.hudplus.waypoint.WaypointManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HudPlusClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("HudPlus");

    private static KeyBinding keyToggleHud;
    private static KeyBinding keyAddWaypoint;
    private static KeyBinding keyOpenWaypoints;
    private static KeyBinding keyToggleCoords;
    private static KeyBinding keyToggleFps;
    private static KeyBinding keyOpenLayout;   // ← BARU: buka layout editor

    @Override
    public void onInitializeClient() {
        LOGGER.info("HUD Plus loaded! Selamat bermain~");

        HudConfig.get();
        WaypointManager.getInstance();

        // ─── Keybinds ───
        keyToggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hudplus.toggle_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "category.hudplus"));

        keyAddWaypoint = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hudplus.add_waypoint", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "category.hudplus"));

        keyOpenWaypoints = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hudplus.open_waypoints", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "category.hudplus"));

        keyToggleCoords = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hudplus.toggle_coords", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F6, "category.hudplus"));

        keyToggleFps = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hudplus.toggle_fps", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F7, "category.hudplus"));

        keyOpenLayout = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hudplus.open_layout", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F8, "category.hudplus"));

        // ─── HUD Render ───
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            HudRenderer.render(ctx, tickCounter.getTickDelta(true), MinecraftClient.getInstance());
        });

        // ─── World Render (beacon waypoint) ───
        WorldRenderEvents.END.register(HudRenderer::renderWorldWaypoints);

        // ─── Tick - handle keybinds ───
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (keyToggleHud.wasPressed()) {
                HudConfig cfg = HudConfig.get();
                cfg.hudVisible = !cfg.hudVisible;
                cfg.save();
                client.player.sendMessage(
                    Text.literal(cfg.hudVisible ? "§aHUD §fdinyalakan" : "§cHUD §fdimatikan"), true);
            }

            if (keyAddWaypoint.wasPressed() && client.currentScreen == null) {
                client.setScreen(new WaypointAddScreen(
                    client.player.getBlockPos(),
                    HudRenderer.getDimensionName(client.world)
                ));
            }

            if (keyOpenWaypoints.wasPressed() && client.currentScreen == null) {
                client.setScreen(new WaypointListScreen());
            }

            if (keyToggleCoords.wasPressed()) {
                HudConfig cfg = HudConfig.get();
                cfg.showCoords = !cfg.showCoords;
                cfg.save();
                client.player.sendMessage(
                    Text.literal(cfg.showCoords ? "§aKoordinat §fdinyalakan" : "§cKoordinat §fdimatikan"), true);
            }

            if (keyToggleFps.wasPressed()) {
                HudConfig cfg = HudConfig.get();
                cfg.showFps = !cfg.showFps;
                cfg.save();
                client.player.sendMessage(
                    Text.literal(cfg.showFps ? "§aFPS §fdinyalakan" : "§cFPS §fdimatikan"), true);
            }

            // F8 - Buka Layout Editor
            if (keyOpenLayout.wasPressed() && client.currentScreen == null) {
                client.setScreen(new HudLayoutScreen());
            }
        });
    }
}
