package com.faboslav.structurized.common;

import com.faboslav.structurized.common.config.StructurizedConfig;
import com.faboslav.structurized.common.events.lifecycle.FinalSetupEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Structurized
{
	public static final String MOD_ID = "structurized";
	private static final Logger LOGGER = LoggerFactory.getLogger(Structurized.MOD_ID);
	private static final StructurizedConfig CONFIG = new StructurizedConfig();

	public static String makeStringID(String name) {
		return MOD_ID + ":" + name;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public static StructurizedConfig getConfig() {
		return CONFIG;
	}

	public static Identifier makeID(String path) {
		return new Identifier(
			MOD_ID,
			path
		);
	}

	public static void init() {
		FinalSetupEvent.EVENT.addListener(Structurized::onFinalSetup);
		/*
		List<String> enabledBugs = CONFIG.getBugFixes().entrySet()
			.stream()
			.filter(Map.Entry::getValue)
			.map(entry -> entry.getKey().bugId())
			.toList();*/
		// LOGGER.info("Enabled {} bug fixes: {}", enabledBugs.size(), enabledBugs);
		LOGGER.info("Successfully Debugify'd your game!");
	}

	private static void onFinalSetup(final FinalSetupEvent event) {
		event.enqueueWork(() -> {

		});
	}
}
