package com.league_enchant.magic;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMagicData {
    private static final Map<UUID, PlayerMagicData> PLAYER_MAGIC_MAP = new ConcurrentHashMap<>();

    private float currentMana = 100.0f;
    private final Map<String, Integer> spellCooldowns = new HashMap<>();
    private String selectedSpellId = "fireball";
    private String castingSpellId = null;
    private int castingTicksRemaining = 0;

    public static PlayerMagicData get(PlayerEntity player) {
        if (player == null) return new PlayerMagicData();
        return PLAYER_MAGIC_MAP.computeIfAbsent(player.getUuid(), uuid -> new PlayerMagicData());
    }

    public static boolean hasEquippedSpellbook(PlayerEntity player) {
        return !getEquippedSpellbookStack(player).isEmpty();
    }

    public static net.minecraft.item.ItemStack getEquippedSpellbookStack(PlayerEntity player) {
        if (player == null) return net.minecraft.item.ItemStack.EMPTY;
        if (player.getMainHandStack().getItem() instanceof SpellbookItem) return player.getMainHandStack();
        if (player.getOffHandStack().getItem() instanceof SpellbookItem) return player.getOffHandStack();

        // Trinkets check
        try {
            Class<?> trinketsApi = Class.forName("dev.emi.trinkets.api.TrinketsApi");
            java.lang.reflect.Method getTrinketComponent = trinketsApi.getMethod("getTrinketComponent", net.minecraft.entity.LivingEntity.class);
            Object componentOpt = getTrinketComponent.invoke(null, player);
            if (componentOpt instanceof java.util.Optional<?> opt && opt.isPresent()) {
                Object component = opt.get();
                java.lang.reflect.Method getEquipped = component.getClass().getMethod("getEquipped", java.util.function.Predicate.class);
                java.util.function.Predicate<net.minecraft.item.ItemStack> predicate = stack -> stack.getItem() instanceof SpellbookItem;
                Object listObj = getEquipped.invoke(component, predicate);
                if (listObj instanceof java.util.List<?> list && !list.isEmpty()) {
                    Object tuple = list.get(0);
                    java.lang.reflect.Method getRight = tuple.getClass().getMethod("getRight");
                    Object stackObj = getRight.invoke(tuple);
                    if (stackObj instanceof net.minecraft.item.ItemStack stack) {
                        return stack;
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        // Accessories API check
        try {
            Class<?> accessoriesCap = Class.forName("io.wispforest.accessories.api.AccessoriesCapability");
            java.lang.reflect.Method getMethod = accessoriesCap.getMethod("get", net.minecraft.entity.LivingEntity.class);
            Object capability = getMethod.invoke(null, player);
            if (capability != null) {
                java.lang.reflect.Method getEquipped = capability.getClass().getMethod("getEquipped", java.util.function.Predicate.class);
                java.util.function.Predicate<net.minecraft.item.ItemStack> predicate = stack -> stack.getItem() instanceof SpellbookItem;
                Object listObj = getEquipped.invoke(capability, predicate);
                if (listObj instanceof java.util.List<?> list && !list.isEmpty()) {
                    Object entry = list.get(0);
                    for (java.lang.reflect.Method m : entry.getClass().getMethods()) {
                        if (m.getReturnType() == net.minecraft.item.ItemStack.class && m.getParameterCount() == 0) {
                            net.minecraft.item.ItemStack res = (net.minecraft.item.ItemStack) m.invoke(entry);
                            if (res != null && !res.isEmpty()) return res;
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        for (int i = 0; i < player.getInventory().size(); i++) {
            net.minecraft.item.ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof SpellbookItem) {
                return stack;
            }
        }
        return net.minecraft.item.ItemStack.EMPTY;
    }

    public float getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(float mana) {
        this.currentMana = mana;
    }

    public float getMaxMana(PlayerEntity player) {
        if (player == null) return 100.0f;
        return (float) player.getAttributeValue(MagicAttributes.MAX_MANA);
    }

    public float getManaRegen(PlayerEntity player) {
        if (player == null) return 1.0f;
        return (float) player.getAttributeValue(MagicAttributes.MANA_REGEN);
    }

    public boolean consumeMana(PlayerEntity player, float amount) {
        if (currentMana >= amount) {
            currentMana -= amount;
            return true;
        }
        return false;
    }

    public void addMana(PlayerEntity player, float amount) {
        float max = getMaxMana(player);
        this.currentMana = Math.min(max, this.currentMana + amount);
    }

    public boolean isCooldownActive(String spellId) {
        return spellCooldowns.getOrDefault(spellId, 0) > 0;
    }

    public int getCooldown(String spellId) {
        return spellCooldowns.getOrDefault(spellId, 0);
    }

    public void setCooldown(String spellId, int ticks) {
        if (ticks > 0) {
            spellCooldowns.put(spellId, ticks);
        } else {
            spellCooldowns.remove(spellId);
        }
    }

    public void tickCooldowns() {
        if (spellCooldowns.isEmpty()) return;
        spellCooldowns.entrySet().removeIf(entry -> {
            int newTicks = entry.getValue() - 1;
            if (newTicks <= 0) return true;
            entry.setValue(newTicks);
            return false;
        });
    }

    public String getSelectedSpellId() {
        return selectedSpellId;
    }

    public void setSelectedSpellId(String spellId) {
        this.selectedSpellId = spellId;
    }

    public String getCastingSpellId() {
        return castingSpellId;
    }

    public int getCastingTicksRemaining() {
        return castingTicksRemaining;
    }

    public void startCasting(String spellId, int durationTicks) {
        this.castingSpellId = spellId;
        this.castingTicksRemaining = durationTicks;
    }

    public void stopCasting() {
        this.castingSpellId = null;
        this.castingTicksRemaining = 0;
    }
}
