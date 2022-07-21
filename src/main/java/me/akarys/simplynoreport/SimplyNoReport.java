package me.akarys.simplynoreport;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimplyNoReport implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("simplynoreport");

	public static World WORLD;

	public static final GameRules.Key<GameRules.BooleanRule> DISABLE_CHAT_REPORT =
			GameRuleRegistry.register("disableChatReport", GameRules.Category.CHAT, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<EnumRule<DisableChatReportStrategy>> DISABLE_CHAT_REPORT_STRATEGY =
			GameRuleRegistry.register("disableChatReportStrategy", GameRules.Category.CHAT, GameRuleFactory.createEnumRule(DisableChatReportStrategy.CONVERT_TO_SERVER_MESSAGE));

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register((server, world) -> {
			WORLD = world;
		});
	}

}
