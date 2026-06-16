package com.hudplus.hud;

import com.hudplus.config.HudConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HudLayoutScreen extends Screen {

    // Representasi tiap elemen HUD yang bisa digeser
    private static class HudElement {
        String id;
        String label;
        int x, y;
        int w, h;
        boolean enabled;

        HudElement(String id, String label, int x, int y, boolean enabled) {
            this.id = id;
            this.label = label;
            this.x = x;
            this.y = y;
            this.w = 0; // dihitung saat render
            this.h = 13;
            this.enabled = enabled;
        }

        boolean contains(double mx, double my) {
            return mx >= x && mx <= x + w && my >= y && my <= y + h;
        }
    }

    private final List<HudElement> elements = new ArrayList<>();
    private HudElement dragging = null;
    private int dragOffX, dragOffY;
    private final HudConfig cfg;

    public HudLayoutScreen() {
        super(Text.literal("HUD Layout Editor"));
        this.cfg = HudConfig.get();
    }

    @Override
    protected void init() {
        cfg.initDefaultPositions(width);

        elements.clear();
        elements.add(new HudElement("days",     "☀ Day: 42",           cfg.daysX,    cfg.daysY,    cfg.showDays));
        elements.add(new HudElement("coords",   "X:100 Y:64 Z:-200",   cfg.coordsX,  cfg.coordsY,  cfg.showCoords));
        elements.add(new HudElement("fps",      "FPS: 60",             cfg.fpsX,     cfg.fpsY,     cfg.showFps));
        elements.add(new HudElement("clock",    "⏰ 14:30",             cfg.clockX,   cfg.clockY,   cfg.showClock));
        elements.add(new HudElement("compass",  "➤ N",                 cfg.compassX, cfg.compassY, cfg.showCompass));
        elements.add(new HudElement("waypoint", "● Home | 0,64,0 (5m)",cfg.waypointX,cfg.waypointY,cfg.showWaypointLabels));

        // Tombol bawah
        int cy = height - 28;
        addDrawableChild(ButtonWidget.builder(Text.literal("§aSimpan"), b -> saveAndClose())
            .dimensions(width / 2 - 82, cy, 78, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§cBatal"), b -> close())
            .dimensions(width / 2 + 4, cy, 78, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§eReset Default"), b -> resetDefaults())
            .dimensions(width / 2 - 40, cy - 24, 80, 18).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Background gelap transparan
        ctx.fill(0, 0, width, height, 0xAA000000);

        // Grid guide (opsional visual)
        for (int gx = 0; gx < width; gx += 40) {
            ctx.fill(gx, 0, gx + 1, height, 0x11FFFFFF);
        }
        for (int gy = 0; gy < height; gy += 40) {
            ctx.fill(0, gy, width, gy + 1, 0x11FFFFFF);
        }

        // Title
        ctx.drawCenteredTextWithShadow(textRenderer,
            "§6§l✦ HUD Layout Editor ✦", width / 2, 6, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer,
            "§7Drag elemen untuk pindahkan · Klik 2x untuk ON/OFF",
            width / 2, 17, 0xAAAAAA);

        // Render tiap elemen
        for (HudElement el : elements) {
            el.w = textRenderer.getWidth(el.label) + 10;

            int bgColor = el.enabled ? 0xCC1A3A1A : 0xCC3A1A1A;
            int borderColor = el.enabled ? 0xFF55FF55 : 0xFFFF5555;
            int textColor = el.enabled ? 0xFFFFFFFF : 0xFF888888;

            // Highlight kalau lagi di-drag
            if (el == dragging) {
                bgColor = 0xCC2A5A2A;
                borderColor = 0xFFFFFF55;
            }

            // Border
            ctx.fill(el.x - 1, el.y - 1, el.x + el.w + 1, el.y + el.h + 1, borderColor);
            // Background
            ctx.fill(el.x, el.y, el.x + el.w, el.y + el.h, bgColor);
            // Text
            ctx.drawTextWithShadow(textRenderer, el.label, el.x + 4, el.y + 3, textColor);

            // Label nama elemen (kecil di atas)
            ctx.drawTextWithShadow(textRenderer,
                "§8[" + el.id + "]", el.x, el.y - 9, 0x88FFFFFF);
        }

        // Info koordinat elemen yang lagi digeser
        if (dragging != null) {
            String info = String.format("§f%s §7→ §ex:%d §ey:%d", dragging.id, dragging.x, dragging.y);
            int iw = textRenderer.getWidth(info) + 8;
            ctx.fill(mouseX + 12, mouseY - 16, mouseX + 12 + iw, mouseY - 4, 0xDD000000);
            ctx.drawTextWithShadow(textRenderer, info, mouseX + 16, mouseY - 14, 0xFFFFFF);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Cek apakah klik di salah satu elemen
            for (int i = elements.size() - 1; i >= 0; i--) {
                HudElement el = elements.get(i);
                if (el.contains(mouseX, mouseY)) {
                    dragging = el;
                    dragOffX = (int)mouseX - el.x;
                    dragOffY = (int)mouseY - el.y;
                    return true;
                }
            }
        } else if (button == 1) {
            // Klik kanan = toggle ON/OFF
            for (HudElement el : elements) {
                if (el.contains(mouseX, mouseY)) {
                    el.enabled = !el.enabled;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging != null && button == 0) {
            int newX = (int)mouseX - dragOffX;
            int newY = (int)mouseY - dragOffY;
            // Clamp ke dalam layar
            newX = Math.max(0, Math.min(width - dragging.w, newX));
            newY = Math.max(0, Math.min(height - dragging.h - 50, newY));
            dragging.x = newX;
            dragging.y = newY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void saveAndClose() {
        for (HudElement el : elements) {
            switch (el.id) {
                case "days"     -> { cfg.daysX = el.x;     cfg.daysY = el.y;     cfg.showDays = el.enabled; }
                case "coords"   -> { cfg.coordsX = el.x;   cfg.coordsY = el.y;   cfg.showCoords = el.enabled; }
                case "fps"      -> { cfg.fpsX = el.x;      cfg.fpsY = el.y;      cfg.showFps = el.enabled; }
                case "clock"    -> { cfg.clockX = el.x;    cfg.clockY = el.y;    cfg.showClock = el.enabled; }
                case "compass"  -> { cfg.compassX = el.x;  cfg.compassY = el.y;  cfg.showCompass = el.enabled; }
                case "waypoint" -> { cfg.waypointX = el.x; cfg.waypointY = el.y; cfg.showWaypointLabels = el.enabled; }
            }
        }
        cfg.save();
        close();
    }

    private void resetDefaults() {
        // Reset ke posisi default bertumpuk di kiri atas
        int y = 4;
        for (HudElement el : elements) {
            el.x = 4;
            el.y = y;
            el.enabled = true;
            y += 14;
        }
        // Waypoint ke kanan
        for (HudElement el : elements) {
            if (el.id.equals("waypoint")) {
                el.x = width - 164;
                el.y = 4;
            }
        }
    }

    @Override
    public boolean shouldPause() { return false; }
}
