package me.akarys.simplynoreport.mixin;

import com.mojang.authlib.GameProfile;
import me.akarys.simplynoreport.DisableChatReportStrategy;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
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

import static me.akarys.simplynoreport.SimplyNoReport.*;

@Mixin(ChatMessageS2CPacket.class)
public class MixinChatMessageS2CPacket {
    @Mutable
    @Shadow @Final private NetworkEncryptionUtils.SignatureData saltSignature;

    @Inject(method = "<init>(Lnet/minecraft/text/Text;Ljava/util/Optional;ILnet/minecraft/network/message/MessageSender;Ljava/time/Instant;Lnet/minecraft/network/encryption/NetworkEncryptionUtils$SignatureData;)V", at = @At("TAIL"))
    private void constructor(Text text, Optional<Text> optional, int i, MessageSender messageSender, Instant instant, NetworkEncryptionUtils.SignatureData signatureData, CallbackInfo ci) {
        if (WORLD == null || SERVER == null) {
            return;
        }

        if (!WORLD.getGameRules().getBoolean(DISABLE_CHAT_REPORT)
                || WORLD.getGameRules().get(DISABLE_CHAT_REPORT_STRATEGY).get() != DisableChatReportStrategy.STRIP_SIGNATURE) {
            return;
        }

        // We don't want to strip the signature if the player is OP and the gamerule is set to true
        GameProfile player = SERVER.getPlayerManager().getPlayer(messageSender.uuid()).getGameProfile();
        if (player != null && SERVER.getPlayerManager().isOperator(player) && WORLD.getGameRules().getBoolean(SEND_SIGNATURES_TO_OPERATORS)) {
            return;
        }

        this.saltSignature = NetworkEncryptionUtils.SignatureData.NONE;
    }
}
