package com.league_enchant.magic;

import com.league_enchant.registry.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpellbookItem extends Item {
    public SpellbookItem(Settings settings) {
        super(settings);
    }

    public static int getSpellSlots(ItemStack stack) {
        Item item = stack.getItem();
        if (item == ModItems.WIMPY_SPELL_BOOK) return 0;
        if (item == ModItems.COPPER_SPELL_BOOK) return 5;
        if (item == ModItems.IRON_SPELL_BOOK) return 6;
        if (item == ModItems.GOLD_SPELL_BOOK) return 8;
        if (item == ModItems.ROTTEN_SPELL_BOOK) return 8;
        if (item == ModItems.DIAMOND_SPELL_BOOK) return 10;
        if (item == ModItems.BLAZE_SPELL_BOOK) return 10;
        if (item == ModItems.DRUIDIC_SPELL_BOOK) return 10;
        if (item == ModItems.VILLAGER_SPELL_BOOK) return 10;
        if (item == ModItems.EVOKER_SPELL_BOOK) return 10;
        if (item == ModItems.CURSED_DOLL_SPELLBOOK) return 10;
        if (item == ModItems.NETHERITE_SPELL_BOOK) return 12;
        if (item == ModItems.LEGENDARY_SPELL_BOOK) return 12;
        if (item == ModItems.DRAGONSKIN_SPELL_BOOK) return 12;
        if (item == ModItems.ICE_SPELL_BOOK) return 12;
        if (item == ModItems.NECRONOMICON_SPELL_BOOK) return 12;
        return 8;
    }

    public static List<String> getEquippedSpells(ItemStack stack) {
        List<String> list = new ArrayList<>();
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();
            if (nbt.contains("Spells", NbtElement.LIST_TYPE)) {
                NbtList nbtList = nbt.getList("Spells", NbtElement.STRING_TYPE);
                for (int i = 0; i < nbtList.size(); i++) {
                    list.add(nbtList.getString(i));
                }
            } else if (nbt.contains("EquippedSpell")) {
                list.add(nbt.getString("EquippedSpell"));
            }
        }
        return list;
    }

    public static boolean addSpell(ItemStack stack, String spellId) {
        List<String> current = getEquippedSpells(stack);
        int maxSlots = getSpellSlots(stack);

        if (current.size() >= maxSlots && maxSlots > 0) {
            // Replaced last spell if slots full, or append if space
            current.set(current.size() - 1, spellId);
        } else if (!current.contains(spellId)) {
            current.add(spellId);
        }

        NbtList nbtList = new NbtList();
        for (String id : current) {
            nbtList.add(NbtString.of(id));
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.put("Spells", nbtList);
        nbt.putString("EquippedSpell", spellId); // Set latest spell as active
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient() && user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = (ServerWorld) world;
            PlayerMagicData magicData = PlayerMagicData.get(player);

            List<String> bookSpells = getEquippedSpells(stack);
            String selectedSpellId = magicData.getSelectedSpellId();

            if (!bookSpells.contains(selectedSpellId) && !bookSpells.isEmpty()) {
                selectedSpellId = bookSpells.get(0);
                magicData.setSelectedSpellId(selectedSpellId);
            }

            AbstractSpell spell = SpellRegistry.getSpell(selectedSpellId);

            if (spell == null) {
                spell = SpellRegistry.FIREBALL;
            }

            if (magicData.isCooldownActive(spell.getSpellId())) {
                int cdSeconds = (magicData.getCooldown(spell.getSpellId()) / 20) + 1;
                player.sendMessage(Text.literal("⏳ " + spell.getName() + " đang hồi chiêu! (" + cdSeconds + "s)").formatted(Formatting.RED), true);
                return TypedActionResult.fail(stack);
            }

            float manaCost = spell.getManaCost(1);
            if (!magicData.consumeMana(player, manaCost)) {
                player.sendMessage(Text.literal("💧 Không đủ Mana! (Cần " + (int) manaCost + " Mana)").formatted(Formatting.BLUE), true);
                return TypedActionResult.fail(stack);
            }

            boolean success = spell.cast(serverWorld, player, magicData, 1);
            if (success) {
                magicData.setCooldown(spell.getSpellId(), spell.getCooldownTicks(1));
                player.sendMessage(Text.literal("✨ Đã thi triển: " + spell.getName()).formatted(Formatting.LIGHT_PURPLE), true);
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int maxSlots = getSpellSlots(stack);
        List<String> spells = getEquippedSpells(stack);

        tooltip.add(Text.literal("📖 Ô Phép: " + spells.size() + " / " + maxSlots + " Ô").formatted(Formatting.GOLD));

        if (!spells.isEmpty()) {
            tooltip.add(Text.literal("✨ Các Phép Đã Ép:").formatted(Formatting.LIGHT_PURPLE));
            for (String spellId : spells) {
                AbstractSpell spell = SpellRegistry.getSpell(spellId);
                if (spell != null) {
                    tooltip.add(Text.literal("  • " + spell.getName()).formatted(Formatting.YELLOW));
                }
            }
        } else {
            tooltip.add(Text.literal("✨ Đặt vào Đế Đe Arcane Anvil + Cuộn Phép để ép phép").formatted(Formatting.GRAY));
        }

        Item item = stack.getItem();
        if (item == ModItems.GOLD_SPELL_BOOK) {
            tooltip.add(Text.literal("💧 +50 Max Mana").formatted(Formatting.DARK_AQUA));
            tooltip.add(Text.literal("⚡ +15% Tốc độ niệm phép").formatted(Formatting.GREEN));
        } else if (item == ModItems.DIAMOND_SPELL_BOOK) {
            tooltip.add(Text.literal("💧 +100 Max Mana").formatted(Formatting.DARK_AQUA));
        } else if (item == ModItems.NETHERITE_SPELL_BOOK) {
            tooltip.add(Text.literal("💧 +200 Max Mana").formatted(Formatting.DARK_AQUA));
            tooltip.add(Text.literal("⏳ +20% Giảm hồi chiêu").formatted(Formatting.GREEN));
        } else if (item == ModItems.BLAZE_SPELL_BOOK) {
            tooltip.add(Text.literal("💧 +200 Max Mana").formatted(Formatting.DARK_AQUA));
            tooltip.add(Text.literal("🔥 +10% Sức Mạnh Hỏa Phép").formatted(Formatting.RED));
        } else if (item == ModItems.ICE_SPELL_BOOK) {
            tooltip.add(Text.literal("💧 +200 Max Mana").formatted(Formatting.DARK_AQUA));
            tooltip.add(Text.literal("❄️ +10% Sức Mạnh Băng Phép").formatted(Formatting.AQUA));
        } else if (item == ModItems.DRAGONSKIN_SPELL_BOOK) {
            tooltip.add(Text.literal("💧 +200 Max Mana").formatted(Formatting.DARK_AQUA));
            tooltip.add(Text.literal("🌌 +10% Sức Mạnh Phép Hư Không").formatted(Formatting.DARK_PURPLE));
        } else if (item == ModItems.DRUIDIC_SPELL_BOOK) {
            tooltip.add(Text.literal("💧 +200 Max Mana").formatted(Formatting.DARK_AQUA));
            tooltip.add(Text.literal("🌿 +10% Sức Mạnh Phép Tự Nhiên").formatted(Formatting.DARK_GREEN));
        } else if (item == ModItems.VILLAGER_SPELL_BOOK) {
            tooltip.add(Text.literal("💧 +200 Max Mana").formatted(Formatting.DARK_AQUA));
            tooltip.add(Text.literal("✝️ +10% Sức Mạnh Thánh Phép").formatted(Formatting.YELLOW));
        } else if (item == ModItems.ROTTEN_SPELL_BOOK) {
            tooltip.add(Text.literal("💧 +100 Max Mana").formatted(Formatting.DARK_AQUA));
            tooltip.add(Text.literal("☣️ -15% Kháng Phép").formatted(Formatting.DARK_RED));
        }

        super.appendTooltip(stack, world, tooltip, context);
    }
}
