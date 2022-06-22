package me.akarys.simplynoreport.mixin;

import com.mojang.authlib.GameProfile;
import me.akarys.simplynoreport.DisableChatReportStrategy;
import net.minecraft.network.Packet;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static me.akarys.simplynoreport.SimplyNoReport.*;

@Mixin(ServerPlayNetworkHandler.class)
abstract public class MixinServerPlayNetworkHandler {
    @Shadow @Final private static Logger LOGGER;

    @Shadow public ServerPlayerEntity player;

    @ModifyVariable(at = @At("HEAD"), index = 1, method = "sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", argsOnly = true)
    private Packet sendPacket(Packet packet) {
        if (!(packet instanceof ChatMessageS2CPacket chatPacket)) {
            return packet;
        }

        if (WORLD == null || SERVER == null) {
            return packet;
        }

        if (!WORLD.getGameRules().getBoolean(DISABLE_CHAT_REPORT)
                || WORLD.getGameRules().get(DISABLE_CHAT_REPORT_STRATEGY).get() != DisableChatReportStrategy.STRIP_SIGNATURE) {
            return packet;
        }

        // We don't want to strip the signature if the player is OP and the gamerule is set to true
        GameProfile target = this.player.getGameProfile();

        if (target != null && SERVER.getPlayerManager().isOperator(target) && WORLD.getGameRules().getBoolean(SEND_SIGNATURES_TO_OPERATORS)) {
            LOGGER.debug("Not stripping signature for {}", target.getName());
            return packet;
        }

        LOGGER.debug("Stripping signature for {}", target.getName());
        return new ChatMessageS2CPacket(
                chatPacket.signedContent(),
                chatPacket.unsignedContent(),
                chatPacket.typeId(),
                chatPacket.sender(),
                chatPacket.timestamp(),
                NetworkEncryptionUtils.SignatureData.NONE
        );
    }
}
