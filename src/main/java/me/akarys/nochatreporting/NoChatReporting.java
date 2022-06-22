package me.akarys.nochatreporting;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoChatReporting implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("nochatreporting");

	public static final GameRules.Key<GameRules.BooleanRule> DISABLE_CHAT_REPORTING =
			GameRuleRegistry.register("disableChatReporting", GameRules.Category.CHAT, GameRuleFactory.createBooleanRule(false));

	@Override
	public void onInitialize() {
		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, typeKey) -> {
			ServerWorld world = sender.getWorld();
			if (world.getGameRules().getBoolean(DISABLE_CHAT_REPORTING)) {
				LOGGER.info("Proxying chat message from {}: {}", sender.getName().getString(), message.raw().getContent().getString());

				MinecraftServer server = sender.getServer();
				Text formattedMessage = Text.of("<" + sender.getName().getString() + "> " + message.raw().getContent().getString());
				server.getPlayerManager().broadcast(formattedMessage, MessageType.SYSTEM);

				return false;
			} else {
				return true;
			}
		});
	}
}
