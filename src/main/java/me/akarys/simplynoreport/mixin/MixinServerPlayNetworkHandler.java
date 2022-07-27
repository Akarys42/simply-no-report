package me.akarys.simplynoreport.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Packet;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static me.akarys.simplynoreport.DisableChatReportStrategy.STRIP_SIGNATURE;
import static me.akarys.simplynoreport.SimplyNoReport.*;

@Mixin(ServerPlayNetworkHandler.class)
abstract public class MixinServerPlayNetworkHandler {
    @Shadow public ServerPlayerEntity player;

    @Shadow @Final private MinecraftServer server;

    private Packet handleChatMessage(ChatMessageS2CPacket chatPacket) {
        GameProfile target = this.player.getGameProfile();
        LOGGER.debug("Stripping signature for {}", target.getName());

        switch (this.server.getOverworld().getGameRules().get(DISABLE_CHAT_REPORT_STRATEGY).get()) {
            case STRIP_SIGNATURE -> {
                SignedMessage updatedMessage = new SignedMessage(
                        chatPacket.message().signedHeader(),
                        MessageSignatureData.EMPTY,
                        chatPacket.message().signedBody(),
                        chatPacket.message().unsignedContent(),
                        chatPacket.message().filterMask()
                );

                return new ChatMessageS2CPacket(
                        updatedMessage,
                        chatPacket.serializedParameters()
                );
            }
            case CONVERT_TO_SERVER_MESSAGE -> {
                Text content = chatPacket.serializedParameters()
                        .toParameters(this.server.getRegistryManager()).get()
                        .applyChatDecoration(chatPacket.message().getContent());

                return new GameMessageS2CPacket(
                        content,
                        false
                );
            }
        };
        return chatPacket;
    }

    private PlayerListS2CPacket handlePlayerList(PlayerListS2CPacket playerListPacket) {
        if (this.server.getOverworld().getGameRules().get(DISABLE_CHAT_REPORT_STRATEGY).get() == STRIP_SIGNATURE) {
            PlayerListS2CPacket newPacket = new PlayerListS2CPacket(playerListPacket.getAction());

            playerListPacket.getEntries().forEach(entry -> {
                newPacket.getEntries().add(new PlayerListS2CPacket.Entry(
                        entry.getProfile(),
                        entry.getLatency(),
                        entry.getGameMode(),
                        entry.getDisplayName(),
                        null
                ));
            });

            return newPacket;
        }
        return playerListPacket;
    }

    @ModifyVariable(at = @At("HEAD"), index = 1, method = "sendPacket*", argsOnly = true)
    private Packet sendPacket(Packet packet) {
        if (!this.server.getOverworld().getGameRules().getBoolean(DISABLE_CHAT_REPORT)) {
            return packet;
        }

        if (packet instanceof ChatMessageS2CPacket chatPacket) {
            return handleChatMessage(chatPacket);
        }

        if (packet instanceof PlayerListS2CPacket playerListPacket) {
            return handlePlayerList(playerListPacket);
        }

        return packet;
    }
}
