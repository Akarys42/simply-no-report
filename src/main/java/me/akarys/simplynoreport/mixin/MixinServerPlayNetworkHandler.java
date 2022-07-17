package me.akarys.simplynoreport.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Packet;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
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
        if (!(packet instanceof ChatMessageS2CPacket chatPacket)) {
            return packet;
        }

        if (!this.server.getOverworld().getGameRules().getBoolean(DISABLE_CHAT_REPORT)) {
            return packet;
        }

        GameProfile target = this.player.getGameProfile();
        LOGGER.debug("Stripping signature for {}", target.getName());

        switch (this.server.getOverworld().getGameRules().get(DISABLE_CHAT_REPORT_STRATEGY).get()) {
            case STRIP_SIGNATURE -> {
                SignedMessage updatedMessage = new SignedMessage(
                        chatPacket.message().signedHeader(),
                        MessageSignatureData.EMPTY,
                        chatPacket.message().signedBody(),
                        chatPacket.message().unsignedContent()
                );

                return new ChatMessageS2CPacket(
                        updatedMessage,
                        chatPacket.serializedParameters()
                );
            }
            case CONVERT_TO_SERVER_MESSAGE -> {
                MutableText content;

                // Incoming whispers
                if (chatPacket.serializedParameters().typeId() == 2) {
                    content = MutableText.of(TextContent.EMPTY)
                        .append(chatPacket.serializedParameters().name())
                        .append(" whispers to you: ")
                        .append(chatPacket.message().getContent())
                        .setStyle(Style.EMPTY
                                .withColor(TextColor.parse("gray"))
                                .withItalic(true)
                        );
                // Outgoing whispers
                } else if (chatPacket.serializedParameters().typeId() == 3) {
                    content = MutableText.of(TextContent.EMPTY)
                        .append("You whisper to ")
                        .append(chatPacket.serializedParameters().targetName())
                        .append(": ")
                        .append(chatPacket.message().getContent())
                        .setStyle(Style.EMPTY
                                .withColor(TextColor.parse("gray"))
                                .withItalic(true)
                        );
                } else {
                    content = MutableText.of(TextContent.EMPTY)
                        .append("<")
                        .append(chatPacket.serializedParameters().name())
                        .append("> ")
                        .append(chatPacket.message().getContent());
                }

                return new GameMessageS2CPacket(
                    content,
                    false
                );
            }
        };
        return packet;
    }
}
