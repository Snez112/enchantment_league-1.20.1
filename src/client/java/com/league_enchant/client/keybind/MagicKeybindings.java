package com.league_enchant.client.keybind;

import com.league_enchant.client.gui.SpellSelectionScreen;
import com.league_enchant.network.ModPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class MagicKeybindings {
    public static KeyBinding OPEN_SPELL_WHEEL_KEY;
    public static KeyBinding CAST_SPELL_KEY;

    public static void register() {
        OPEN_SPELL_WHEEL_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.league_enchantment.open_spell_wheel",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.league_enchantment.magic"
        ));

        CAST_SPELL_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.league_enchantment.cast_spell",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "category.league_enchantment.magic"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_SPELL_WHEEL_KEY.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    client.setScreen(new SpellSelectionScreen());
                }
            }

            while (CAST_SPELL_KEY.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    ClientPlayNetworking.send(ModPackets.CAST_SPELL_PACKET_ID, PacketByteBufs.create());
                }
            }
        });
    }
}
