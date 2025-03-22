package com.faboslav.structurify.common;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.StructurifyConfigLoader;
import com.faboslav.structurify.common.config.client.gui.StructurifyConfigScreen;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.modcompat.ModChecker;
import com.faboslav.structurify.common.registry.StructurifyRegistryUpdater;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Structurify
{
	public static final String MOD_ID = "structurify";
	private static final Logger LOGGER = LoggerFactory.getLogger(Structurify.MOD_ID);
	private static final StructurifyConfig CONFIG = new StructurifyConfig();

	public static String makeStringID(String name) {
		return MOD_ID + ":" + name;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public static StructurifyConfig getConfig() {
		return CONFIG;
	}

	public static ResourceLocation makeId(String path) {
		/*? >=1.21 {*/
		/*return ResourceLocation.tryBuild(
			MOD_ID,
			path
		);
		 *//*?} else {*/
		return new ResourceLocation(
			MOD_ID,
			path
		);
		/*?}*/
	}

	public static ResourceLocation makeNamespacedId(String id) {
		/*? >=1.21 {*/
		/*return ResourceLocation.parse(
			id
		);
		*//*?} else {*/
		return new ResourceLocation(
			id
		);
		/*?}*/
	}

	public static void init() {
		Structurify.getConfig().create();
		ModChecker.setupModCompat();

		LoadConfigEvent.EVENT.addListener(StructurifyConfigLoader::loadConfig);
		UpdateRegistriesEvent.EVENT.addListener(StructurifyRegistryUpdater::updateRegistries);
	}
}