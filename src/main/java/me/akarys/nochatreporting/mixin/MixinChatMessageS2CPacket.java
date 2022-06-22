package me.akarys.nochatreporting.mixin;

import me.akarys.nochatreporting.ChatReportingStrategy;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.Optional;

import static me.akarys.nochatreporting.NoChatReporting.*;

@Mixin(ChatMessageS2CPacket.class)
public class MixinChatMessageS2CPacket {
    @Mutable
    @Shadow @Final private NetworkEncryptionUtils.SignatureData saltSignature;

    @Inject(method = "<init>(Lnet/minecraft/text/Text;Ljava/util/Optional;ILnet/minecraft/network/message/MessageSender;Ljava/time/Instant;Lnet/minecraft/network/encryption/NetworkEncryptionUtils$SignatureData;)V", at = @At("TAIL"))
    private void constructor(Text text, Optional<Text> optional, int i, MessageSender messageSender, Instant instant, NetworkEncryptionUtils.SignatureData signatureData, CallbackInfo ci) {
        if (WORLD == null) {
            return;
        }

        if (!WORLD.getGameRules().getBoolean(DISABLE_CHAT_REPORTING)
                || WORLD.getGameRules().get(DISABLE_CHAT_REPORTING_STRATEGY).get() != ChatReportingStrategy.STRIP_SIGNATURE) {
            return;
        }

        this.saltSignature = NetworkEncryptionUtils.SignatureData.NONE;
    }
}
