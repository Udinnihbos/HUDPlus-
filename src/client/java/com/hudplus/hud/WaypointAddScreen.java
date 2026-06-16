package com.hudplus.hud;

import com.hudplus.waypoint.Waypoint;
import com.hudplus.waypoint.WaypointManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class WaypointAddScreen extends Screen {
    private TextFieldWidget nameField;
    private TextFieldWidget colorField;
    private final BlockPos playerPos;
    private final String dimension;

    // Preset colors
    private static final int[] COLORS = {
        0xFF5555, 0xFF55FF, 0xFFFF55, 0x55FF55,
        0x55FFFF, 0x5555FF, 0xFFFFFF, 0xFFAA00
    };
    private static final String[] COLOR_NAMES = {
        "Merah", "Pink", "Kuning", "Hijau",
        "Cyan", "Biru", "Putih", "Oranye"
    };
    private int selectedColor = 0xFF5555;

    public WaypointAddScreen(BlockPos pos, String dimension) {
        super(Text.literal("Tambah Waypoint"));
        this.playerPos = pos;
        this.dimension = dimension;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int cy = height / 2;

        // Name input
        nameField = new TextFieldWidget(textRenderer, cx - 75, cy - 50, 150, 20,
            Text.literal("Nama Waypoint"));
        nameField.setMaxLength(32);
        nameField.setPlaceholder(Text.literal("Nama Waypoint..."));
        addDrawableChild(nameField);

        // Color hex input
        colorField = new TextFieldWidget(textRenderer, cx - 75, cy - 20, 150, 20,
            Text.literal("Warna (hex)"));
        colorField.setMaxLength(8);
        colorField.setText(String.format("%06X", selectedColor));
        colorField.setPlaceholder(Text.literal("Warna hex, e.g. FF5555"));
        addDrawableChild(colorField);

        // Color preset buttons (2 rows of 4)
        for (int i = 0; i < COLORS.length; i++) {
            final int idx = i;
            int bx = cx - 76 + (i % 4) * 38;
            int by = cy + 10 + (i / 4) * 22;
            ButtonWidget btn = ButtonWidget.builder(Text.literal(COLOR_NAMES[i]), b -> {
                selectedColor = COLORS[idx];
                colorField.setText(String.format("%06X", COLORS[idx]));
            }).dimensions(bx, by, 36, 18).build();
            addDrawableChild(btn);
        }

        // Add button
        addDrawableChild(ButtonWidget.builder(Text.literal("✔ Tambah"), b -> addWaypoint())
            .dimensions(cx - 80, cy + 60, 75, 20).build());

        // Cancel button
        addDrawableChild(ButtonWidget.builder(Text.literal("✖ Batal"), b -> close())
            .dimensions(cx + 5, cy + 60, 75, 20).build());
    }

    private void addWaypoint() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) name = "Waypoint";

        int color = selectedColor;
        try {
            color = Integer.parseInt(colorField.getText().replace("#", ""), 16);
        } catch (NumberFormatException ignored) {}

        WaypointManager.getInstance().addWaypoint(
            new Waypoint(name, color, playerPos, dimension)
        );
        close();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);

        int cx = width / 2;
        int cy = height / 2;

        // Panel background
        ctx.fill(cx - 95, cy - 70, cx + 95, cy + 90, 0xCC000000);
        ctx.fill(cx - 94, cy - 69, cx + 94, cy + 89, 0xFF1A1A2E);

        ctx.drawCenteredTextWithShadow(textRenderer, "§6✦ Tambah Waypoint §6✦", cx, cy - 62, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "§7Nama:", cx - 75, cy - 58, 0xAAAAAA);
        ctx.drawTextWithShadow(textRenderer, "§7Warna (Hex):", cx - 75, cy - 28, 0xAAAAAA);
        ctx.drawTextWithShadow(textRenderer,
            String.format("§7Posisi: §f%d, %d, %d", playerPos.getX(), playerPos.getY(), playerPos.getZ()),
            cx - 75, cy - 64, 0xAAAAAA);

        super.render(ctx, mouseX, mouseY, delta);

        // Color preview box
        try {
            int previewColor = Integer.parseInt(colorField.getText().replace("#", ""), 16);
            ctx.fill(cx + 80, cy - 18, cx + 92, cy - 6, 0xFF000000 | previewColor);
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public boolean shouldPause() { return false; }
}
