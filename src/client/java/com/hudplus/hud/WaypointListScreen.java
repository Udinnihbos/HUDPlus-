package com.hudplus.hud;

import com.hudplus.waypoint.Waypoint;
import com.hudplus.waypoint.WaypointManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class WaypointListScreen extends Screen {
    private int scrollOffset = 0;
    private static final int ENTRY_H = 24;
    private static final int MAX_VISIBLE = 8;

    public WaypointListScreen() {
        super(Text.literal("Daftar Waypoint"));
    }

    @Override
    protected void init() {
        int cx = width / 2;

        addDrawableChild(ButtonWidget.builder(Text.literal("§a+ Waypoint Baru"), b -> {
            if (client != null && client.player != null && client.world != null) {
                client.setScreen(new WaypointAddScreen(
                    client.player.getBlockPos(),
                    HudRenderer.getDimensionName(client.world)
                ));
            }
        }).dimensions(cx - 80, height - 30, 160, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("§cTutup"), b -> close())
            .dimensions(width - 55, height - 30, 50, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);

        List<Waypoint> wps = WaypointManager.getInstance().getWaypoints();
        int cx = width / 2;
        int startY = 30;

        ctx.drawCenteredTextWithShadow(textRenderer, "§6✦ Daftar Waypoint ✦", cx, 10, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "§7(" + wps.size() + " waypoint)", cx, 20, 0xAAAAAA);

        if (wps.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer,
                "§7Belum ada waypoint. Tekan §eN §7di dunia untuk tambah!", cx, height / 2, 0xAAAAAA);
        } else {
            int visibleCount = Math.min(MAX_VISIBLE, wps.size());
            for (int i = 0; i < visibleCount; i++) {
                int idx = i + scrollOffset;
                if (idx >= wps.size()) break;
                Waypoint wp = wps.get(idx);

                int ey = startY + i * ENTRY_H;
                int ex = cx - 150;

                ctx.fill(ex, ey, ex + 300, ey + ENTRY_H - 2,
                    wp.isVisible() ? 0xAA1A2A1A : 0xAA2A1A1A);

                float[] rgb = wp.getColorRGB();
                int argb = 0xFF000000
                    | ((int)(rgb[0]*255) << 16)
                    | ((int)(rgb[1]*255) << 8)
                    | (int)(rgb[2]*255);
                ctx.fill(ex + 3, ey + 7, ex + 10, ey + 15, argb);

                String status = wp.isVisible() ? "§a[ON]" : "§c[OFF]";
                ctx.drawTextWithShadow(textRenderer,
                    status + " §f" + wp.getName(), ex + 14, ey + 4, 0xFFFFFF);
                ctx.drawTextWithShadow(textRenderer,
                    String.format("§7%d, %d, %d §8(%s)",
                        wp.getPos().getX(), wp.getPos().getY(), wp.getPos().getZ(),
                        wp.getDimension()),
                    ex + 14, ey + 13, 0xAAAAAA);

                final String wpId = wp.getId();
                int btnX = ex + 240;
                addDrawableChild(ButtonWidget.builder(
                    Text.literal(wp.isVisible() ? "§cSembunyi" : "§aTampil"), b -> {
                        WaypointManager.getInstance().toggleWaypoint(wpId);
                        clearChildren();
                        init();
                    }).dimensions(btnX, ey + 4, 55, 14).build());

                addDrawableChild(ButtonWidget.builder(Text.literal("§4✖"), b -> {
                    WaypointManager.getInstance().removeWaypoint(wpId);
                    clearChildren();
                    init();
                }).dimensions(btnX - 17, ey + 4, 14, 14).build());
            }
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        List<Waypoint> wps = WaypointManager.getInstance().getWaypoints();
        scrollOffset = Math.max(0, Math.min(scrollOffset - (int)verticalAmount,
            Math.max(0, wps.size() - MAX_VISIBLE)));
        return true;
    }

    @Override
    public boolean shouldPause() { return false; }
}
