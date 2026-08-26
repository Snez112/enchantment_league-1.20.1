package com.league_enchant.mixin;

import com.league_enchant.magic.MagicAttributes;
import com.league_enchant.registry.ModAttributes;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void injectCustomAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue()
            .add(ModAttributes.CRIT_DAMAGE)
            .add(MagicAttributes.MAX_MANA)
            .add(MagicAttributes.MANA_REGEN)
            .add(MagicAttributes.SPELL_POWER)
            .add(MagicAttributes.SPELL_RESIST)
            .add(MagicAttributes.COOLDOWN_REDUCTION)
            .add(MagicAttributes.CAST_TIME_REDUCTION);
    }
}
