package com.league_enchant.client.gui;

import com.league_enchant.magic.screen.ArcaneAnvilScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArcaneAnvilScreen extends HandledScreen<ArcaneAnvilScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/anvil.png");

    public ArcaneAnvilScreen(ArcaneAnvilScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        drawContext.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }
}
