package me.akarys.simplynoreport.mixin;

import net.minecraft.network.message.MessageVerifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.akarys.simplynoreport.SimplyNoReport.DISABLE_CHAT_REPORT;
import static me.akarys.simplynoreport.SimplyNoReport.WORLD;

@Mixin(MessageVerifier.Impl.class)
abstract public class MixinMessageVerifierImpl {
    // For some reason that mixin isn't needed, although considering it seems like a bug.
    // (the server continues to check signatures, yet the client cannot properly chain messages)
    // I'm leaving this here for now.

    /* @Inject(at = @At("RETURN"), cancellable = true, method = "updateAndValidate")
    private void validateAndUpdate(CallbackInfoReturnable<MessageVerifier.class_7646> cir) {
        if (WORLD.getGameRules().getBoolean(DISABLE_CHAT_REPORT)) {
            cir.setReturnValue(MessageVerifier.class_7646.SECURE);
        }
    } */
}
