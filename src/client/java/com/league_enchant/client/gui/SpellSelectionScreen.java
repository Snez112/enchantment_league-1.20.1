package com.league_enchant.client.gui;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import com.league_enchant.magic.SpellRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SpellSelectionScreen extends Screen {
    private final List<AbstractSpell> spellList = new ArrayList<>();
    private int selectedIndex = 0;

    public SpellSelectionScreen() {
        super(Text.literal("Chọn Phép Thuật"));
    }

    @Override
    protected void init() {
        super.init();
        this.spellList.clear();

        if (client != null && client.player != null) {
            net.minecraft.item.ItemStack equippedBook = com.league_enchant.magic.PlayerMagicData.getEquippedSpellbookStack(client.player);
            List<String> boundSpells = com.league_enchant.magic.SpellbookItem.getEquippedSpells(equippedBook);

            for (String spellId : boundSpells) {
                AbstractSpell spell = SpellRegistry.getSpell(spellId);
                if (spell != null) {
                    this.spellList.add(spell);
                }
            }

            if (this.spellList.isEmpty()) {
                this.spellList.add(SpellRegistry.FIREBALL);
            }

            PlayerMagicData magicData = PlayerMagicData.get(client.player);
            String currentId = magicData.getSelectedSpellId();
            for (int i = 0; i < spellList.size(); i++) {
                if (spellList.get(i).getSpellId().equals(currentId)) {
                    selectedIndex = i;
                    break;
                }
            }
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int radius = 70;

        if (spellList.isEmpty()) return;

        double angleStep = (2 * Math.PI) / spellList.size();

        // Calculate selected index based on mouse angle
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        if (Math.hypot(dx, dy) > 15) {
            double angle = Math.atan2(dy, dx) + Math.PI / 2;
            if (angle < 0) angle += 2 * Math.PI;
            selectedIndex = (int) Math.floor(angle / angleStep) % spellList.size();
        }

        // Draw radial spell slots
        for (int i = 0; i < spellList.size(); i++) {
            AbstractSpell spell = spellList.get(i);
            double itemAngle = i * angleStep - Math.PI / 2;
            int x = (int) (centerX + radius * Math.cos(itemAngle));
            int y = (int) (centerY + radius * Math.sin(itemAngle));

            boolean isSelected = (i == selectedIndex);
            int color = isSelected ? 0xFF55FF55 : 0xFFFFFFFF;
            int bgColor = isSelected ? 0xAA00AA00 : 0xAA222222;

            drawContext.fill(x - 30, y - 10, x + 30, y + 10, bgColor);
            drawContext.drawText(this.textRenderer, spell.getName().substring(0, Math.min(spell.getName().length(), 10)), x - 25, y - 4, color, true);
        }

        // Draw center selected spell details
        if (selectedIndex >= 0 && selectedIndex < spellList.size()) {
            AbstractSpell currentSpell = spellList.get(selectedIndex);
            drawContext.drawCenteredTextWithShadow(this.textRenderer, Text.literal("✨ " + currentSpell.getName()).formatted(Formatting.GOLD), centerX, centerY - 15, 0xFFFFFF);
            drawContext.drawCenteredTextWithShadow(this.textRenderer, Text.literal("🔮 " + currentSpell.getSchool().getDisplayName() + " | 💧 " + (int) currentSpell.getManaCost(1) + " MP").formatted(Formatting.AQUA), centerX, centerY, 0xFFFFFF);
        }

        super.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (client != null && client.player != null && selectedIndex >= 0 && selectedIndex < spellList.size()) {
            AbstractSpell chosen = spellList.get(selectedIndex);
            PlayerMagicData.get(client.player).setSelectedSpellId(chosen.getSpellId());
            client.player.sendMessage(Text.literal("🎯 Đã chọn Phép: " + chosen.getName()).formatted(Formatting.GREEN), true);
            this.close();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
