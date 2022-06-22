package me.akarys.simplynoreport;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimplyNoReport implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("nochatreporting");

	public static final GameRules.Key<GameRules.BooleanRule> DISABLE_CHAT_REPORT =
			GameRuleRegistry.register("disableChatReport", GameRules.Category.CHAT, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<EnumRule<DisableChatReportStrategy>> DISABLE_CHAT_REPORT_STRATEGY =
			GameRuleRegistry.register("disableChatReportStrategy", GameRules.Category.CHAT, GameRuleFactory.createEnumRule(DisableChatReportStrategy.STRIP_SIGNATURE));

	public static World WORLD;


	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register((server, world) -> {
			WORLD = world;
		});

		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, typeKey) -> {
			ServerWorld world = sender.getWorld();

			if (!world.getGameRules().getBoolean(DISABLE_CHAT_REPORT)
					|| world.getGameRules().get(DISABLE_CHAT_REPORT_STRATEGY).get() != DisableChatReportStrategy.CONVERT_TO_SERVER_MESSAGE) {
				return true;
			}

			LOGGER.debug("Proxying chat message from {}: {}", sender.getName().getString(), message.raw().getContent().getString());

			MinecraftServer server = sender.getServer();
			Text formattedMessage = Text.of("<" + sender.getName().getString() + "> " + message.raw().getContent().getString());
			server.getPlayerManager().broadcast(formattedMessage, MessageType.SYSTEM);

			return false;
		});
	}
}
