package com.league_enchant.client.hud;

import com.league_enchant.magic.MagicAttributes;
import com.league_enchant.magic.PlayerMagicData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ManaBarOverlay implements HudRenderCallback {
    public static void register() {
        HudRenderCallback.EVENT.register(new ManaBarOverlay());
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.options.hudHidden) {
            return;
        }

        ClientPlayerEntity player = client.player;
        if (!PlayerMagicData.hasEquippedSpellbook(player)) {
            return;
        }

        PlayerMagicData magicData = PlayerMagicData.get(player);

        float currentMana = magicData.getCurrentMana();
        float maxMana = (float) player.getAttributeValue(MagicAttributes.MAX_MANA);
        if (maxMana <= 0) maxMana = 100.0f;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int barWidth = 80;
        int barHeight = 8;
        int x = 10;
        int y = height - 25;

        // Draw background bar (Dark blue)
        drawContext.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000033);
        drawContext.fill(x, y, x + barWidth, y + barHeight, 0xFF001144);

        // Draw filled mana bar (Cyan / Light Blue)
        float ratio = Math.min(1.0f, Math.max(0.0f, currentMana / maxMana));
        int fillWidth = (int) (barWidth * ratio);
        if (fillWidth > 0) {
            drawContext.fill(x, y, x + fillWidth, y + barHeight, 0xFF00AAFF);
        }

        // Draw Mana text
        String manaText = (int) currentMana + " / " + (int) maxMana + " MP";
        drawContext.drawText(client.textRenderer, Text.literal(manaText), x + 4, y, 0xFFFFFFFF, true);

        // Draw Selected Spell Icon & Slot
        net.minecraft.item.ItemStack equippedBook = PlayerMagicData.getEquippedSpellbookStack(player);
        java.util.List<String> boundSpells = com.league_enchant.magic.SpellbookItem.getEquippedSpells(equippedBook);
        String selectedSpellId = magicData.getSelectedSpellId();

        if (!boundSpells.isEmpty() && !boundSpells.contains(selectedSpellId)) {
            selectedSpellId = boundSpells.get(0);
            magicData.setSelectedSpellId(selectedSpellId);
        }

        com.league_enchant.magic.AbstractSpell selectedSpell = com.league_enchant.magic.SpellRegistry.getSpell(selectedSpellId);
        if (selectedSpell != null) {
            int spellX = x + barWidth + 12;
            int spellY = y - 2;

            drawContext.fill(spellX - 2, spellY - 2, spellX + 85, spellY + 12, 0xAA000000);
            
            String cdText = "";
            if (magicData.isCooldownActive(selectedSpellId)) {
                int secs = (magicData.getCooldown(selectedSpellId) / 20) + 1;
                cdText = " (" + secs + "s)";
            }

            drawContext.drawText(client.textRenderer, Text.literal("✨ " + selectedSpell.getName() + cdText), spellX + 2, spellY + 2, 0xFFFF55, true);
        }
    }
}
