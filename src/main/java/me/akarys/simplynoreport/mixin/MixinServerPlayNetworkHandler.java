package me.akarys.simplynoreport.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Packet;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static me.akarys.simplynoreport.SimplyNoReport.*;

@Mixin(ServerPlayNetworkHandler.class)
abstract public class MixinServerPlayNetworkHandler {
    @Shadow public ServerPlayerEntity player;

    @Shadow @Final private MinecraftServer server;

    @ModifyVariable(at = @At("HEAD"), index = 1, method = "sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", argsOnly = true)
    private Packet sendPacket(Packet packet) {
        if (packet instanceof  GameMessageS2CPacket) {
            LOGGER.debug("GameMessageS2CPacket: {}", (packet));
        }

        if (!(packet instanceof ChatMessageS2CPacket chatPacket)) {
            return packet;
        }

        if (!this.server.getOverworld().getGameRules().getBoolean(DISABLE_CHAT_REPORT)) {
            return packet;
        }

        // We don't want to strip the signature if the player is OP and the gamerule is set to true
        GameProfile target = this.player.getGameProfile();

        if (target != null && this.server.getPlayerManager().isOperator(target) && this.server.getOverworld().getGameRules().getBoolean(SEND_SIGNATURES_TO_OPERATORS)) {
            LOGGER.debug("Not stripping signature for {}", target.getName());
            return packet;
        }

        LOGGER.debug("Stripping signature for {}", target.getName());
        return switch (this.server.getOverworld().getGameRules().get(DISABLE_CHAT_REPORT_STRATEGY).get()) {
            case STRIP_SIGNATURE -> new ChatMessageS2CPacket(
                chatPacket.signedContent(),
                chatPacket.unsignedContent(),
                chatPacket.typeId(),
                chatPacket.sender(),
                chatPacket.timestamp(),
                NetworkEncryptionUtils.SignatureData.NONE
            );
            case CONVERT_TO_SERVER_MESSAGE -> new GameMessageS2CPacket(
                MutableText.of(TextContent.EMPTY)
                    .append("<")
                    .append(chatPacket.sender().name())
                    .append("> ")
                    .append(chatPacket.unsignedContent().orElse(chatPacket.signedContent())),
                1
            );
        };
    }
}
