package com.league_enchant.magic.screen;

import com.league_enchant.magic.ScrollItem;
import com.league_enchant.magic.SpellbookItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ArcaneAnvilScreenHandler extends ScreenHandler {
    private final Inventory input = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            ArcaneAnvilScreenHandler.this.onContentChanged(this);
        }
    };
    private final CraftingResultInventory result = new CraftingResultInventory();

    public ArcaneAnvilScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.ARCANE_ANVIL, syncId);

        // Slot 0: Spellbook input
        this.addSlot(new Slot(this.input, 0, 27, 47));
        // Slot 1: Scroll input
        this.addSlot(new Slot(this.input, 1, 76, 47));
        // Slot 2: Output slot
        this.addSlot(new Slot(this.result, 2, 134, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                input.setStack(0, ItemStack.EMPTY);
                ItemStack scrollStack = input.getStack(1);
                scrollStack.decrement(1);
                input.setStack(1, scrollStack);
                player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        });

        // Player Inventory Slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack spellbook = this.input.getStack(0);
        ItemStack scroll = this.input.getStack(1);

        if (spellbook.getItem() instanceof SpellbookItem && scroll.getItem() instanceof ScrollItem) {
            ItemStack output = spellbook.copy();
            String spellId = ScrollItem.getSpellId(scroll);

            SpellbookItem.addSpell(output, spellId);
            this.result.setStack(0, output);
        } else {
            this.result.setStack(0, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot == 2) {
                if (!this.insertItem(originalStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onTakeItem(player, originalStack);
            } else if (invSlot != 0 && invSlot != 1) {
                if (originalStack.getItem() instanceof SpellbookItem) {
                    if (!this.insertItem(originalStack, 0, 1, false)) return ItemStack.EMPTY;
                } else if (originalStack.getItem() instanceof ScrollItem) {
                    if (!this.insertItem(originalStack, 1, 2, false)) return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }
}
