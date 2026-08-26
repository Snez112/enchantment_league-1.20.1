package com.league_enchant.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Pseudo
@Mixin(targets = "dev.shadowsoffire.attributeslib.client.AttributesGui", remap = false)
public abstract class AttributesGuiMixin extends Screen {
    protected AttributesGuiMixin(Text title) {
        super(title);
    }

    @Unique
    private TextFieldWidget searchBox;

    @Inject(method = "init", at = @At("RETURN"), remap = false, require = 0)
    private void addSearchBox(CallbackInfo ci) {
        if (this.textRenderer != null) {
            this.searchBox = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 60,
                10,
                120,
                14,
                Text.literal("Search Attributes...")
            );
            this.searchBox.setPlaceholder(Text.literal("🔍 Tìm chỉ số..."));
            this.addSelectableChild(this.searchBox);
        }
    }

    @Inject(method = "render", at = @At("RETURN"), remap = false, require = 0)
    private void renderSearchBox(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.searchBox != null) {
            this.searchBox.render(context, mouseX, mouseY, delta);
        }
    }
}
