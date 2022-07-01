package me.akarys.simplynoreport;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimplyNoReport implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("simplynoreport");

	public static final GameRules.Key<GameRules.BooleanRule> DISABLE_CHAT_REPORT =
			GameRuleRegistry.register("disableChatReport", GameRules.Category.CHAT, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<EnumRule<DisableChatReportStrategy>> DISABLE_CHAT_REPORT_STRATEGY =
			GameRuleRegistry.register("disableChatReportStrategy", GameRules.Category.CHAT, GameRuleFactory.createEnumRule(DisableChatReportStrategy.STRIP_SIGNATURE));
	public static final GameRules.Key<GameRules.BooleanRule> SEND_SIGNATURES_TO_OPERATORS =
			GameRuleRegistry.register("sendSignaturesToOperators", GameRules.Category.CHAT, GameRuleFactory.createBooleanRule(true));


	@Override
	public void onInitialize() {}
}
