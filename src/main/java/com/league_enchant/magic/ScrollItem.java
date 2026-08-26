package com.league_enchant.magic;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScrollItem extends Item {
    public ScrollItem(Settings settings) {
        super(settings);
    }

    public static ItemStack createScroll(AbstractSpell spell, int level) {
        ItemStack stack = new ItemStack(com.league_enchant.registry.ModItems.SCROLL);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString("SpellId", spell.getSpellId());
        nbt.putInt("SpellLevel", level);
        return stack;
    }

    public static String getSpellId(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("SpellId")) {
            return stack.getNbt().getString("SpellId");
        }
        String path = net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).getPath();
        if (path.startsWith("scroll_")) {
            return path.substring("scroll_".length());
        }
        return "fireball";
    }

    public static int getSpellLevel(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("SpellLevel")) {
            return stack.getNbt().getInt("SpellLevel");
        }
        return 1;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient() && user instanceof ServerPlayerEntity player) {
            ServerWorld serverWorld = (ServerWorld) world;
            PlayerMagicData magicData = PlayerMagicData.get(player);

            String spellId = getSpellId(stack);
            int spellLevel = getSpellLevel(stack);
            AbstractSpell spell = SpellRegistry.getSpell(spellId);

            if (spell != null) {
                float manaCost = spell.getManaCost(spellLevel);
                if (!magicData.consumeMana(player, manaCost)) {
                    player.sendMessage(Text.literal("💧 Không đủ Mana! (Cần " + (int) manaCost + " Mana)").formatted(Formatting.BLUE), true);
                    return TypedActionResult.fail(stack);
                }

                boolean success = spell.cast(serverWorld, player, magicData, spellLevel);
                if (success) {
                    player.sendMessage(Text.literal("📜 Đã đọc Cuộn Phép: " + spell.getName()).formatted(Formatting.GOLD), true);
                    if (!user.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
                }
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String spellId = getSpellId(stack);
        int spellLevel = getSpellLevel(stack);
        AbstractSpell spell = SpellRegistry.getSpell(spellId);

        if (spell != null) {
            tooltip.add(Text.literal("✨ Phép: " + spell.getName() + " (Cấp " + spellLevel + ")").formatted(Formatting.YELLOW));
            tooltip.add(Text.literal("🔮 Trường Phép: " + spell.getSchool().getDisplayName()).formatted(Formatting.LIGHT_PURPLE));
            tooltip.add(Text.literal("💧 Tiêu hao Mana: " + (int) spell.getManaCost(spellLevel) + " MP").formatted(Formatting.AQUA));
        } else {
            tooltip.add(Text.literal("📜 Cuộn Phép Bí Thuật").formatted(Formatting.GRAY));
        }
    }
}
