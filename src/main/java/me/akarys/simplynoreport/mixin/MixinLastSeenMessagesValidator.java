package me.akarys.simplynoreport.mixin;

import net.minecraft.network.chat.LastSeenMessagesValidator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

import static me.akarys.simplynoreport.SimplyNoReport.DISABLE_CHAT_REPORT;
import static me.akarys.simplynoreport.SimplyNoReport.WORLD;

@Mixin(LastSeenMessagesValidator.class)
abstract public class MixinLastSeenMessagesValidator {
    @Inject(at = @At("RETURN"), cancellable = true, method = "validateAndUpdate")
    private void validateAndUpdate(CallbackInfoReturnable<Set<LastSeenMessagesValidator.ErrorCondition>> cir) {
        if (WORLD.getGameRules().getBoolean(DISABLE_CHAT_REPORT)) {
            cir.setReturnValue(new HashSet<>());
        }
    }
}
