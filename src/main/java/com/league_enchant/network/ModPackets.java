package com.league_enchant.network;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import com.league_enchant.magic.SpellRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier CAST_SPELL_PACKET_ID = new Identifier("league_enchantment", "cast_spell");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(CAST_SPELL_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (!PlayerMagicData.hasEquippedSpellbook(player)) {
                    player.sendMessage(Text.literal("⚠️ Bạn chưa trang bị Sách Phép vào Accessories!").formatted(Formatting.RED), true);
                    return;
                }

                PlayerMagicData magicData = PlayerMagicData.get(player);
                net.minecraft.item.ItemStack equippedBook = PlayerMagicData.getEquippedSpellbookStack(player);
                java.util.List<String> boundSpells = com.league_enchant.magic.SpellbookItem.getEquippedSpells(equippedBook);

                String selectedSpellId = magicData.getSelectedSpellId();
                if (!boundSpells.isEmpty() && !boundSpells.contains(selectedSpellId)) {
                    selectedSpellId = boundSpells.get(0);
                    magicData.setSelectedSpellId(selectedSpellId);
                }

                AbstractSpell spell = SpellRegistry.getSpell(selectedSpellId);
                if (spell == null) {
                    spell = SpellRegistry.FIREBALL;
                }

                if (magicData.isCooldownActive(spell.getSpellId())) {
                    int cdSeconds = (magicData.getCooldown(spell.getSpellId()) / 20) + 1;
                    player.sendMessage(Text.literal("⏳ " + spell.getName() + " đang hồi chiêu! (" + cdSeconds + "s)").formatted(Formatting.RED), true);
                    return;
                }

                float manaCost = spell.getManaCost(1);
                if (!magicData.consumeMana(player, manaCost)) {
                    player.sendMessage(Text.literal("💧 Không đủ Mana! (Cần " + (int) manaCost + " MP)").formatted(Formatting.BLUE), true);
                    return;
                }

                boolean success = spell.cast(player.getServerWorld(), player, magicData, 1);
                if (success) {
                    magicData.setCooldown(spell.getSpellId(), spell.getCooldownTicks(1));
                    player.sendMessage(Text.literal("✨ Đã thi triển: " + spell.getName()).formatted(Formatting.LIGHT_PURPLE), true);
                }
            });
        });
    }
}
