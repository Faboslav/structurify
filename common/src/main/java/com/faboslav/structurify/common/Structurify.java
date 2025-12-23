package com.faboslav.structurify.common;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.StructurifyConfigSerializer;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.modcompat.ModChecker;
import com.faboslav.structurify.common.registry.StructurifyRegistryUpdater;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"all", "removal"})
public final class Structurify
{
	public static final String MOD_ID = "structurify";
	private static final Logger LOGGER = LoggerFactory.getLogger(Structurify.MOD_ID);
	private static final StructurifyConfig CONFIG = new StructurifyConfig();

	public static StructurifyConfig getConfig() {
		return CONFIG;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public static Identifier makeId(String path) {
		//? if >=1.21 {
		return Identifier.tryBuild(
			MOD_ID,
			path
		);
		//?} else {
		/*return new ResourceLocation(
			MOD_ID,
			path
		);
		*///?}
	}

	public static Identifier makeNamespacedId(String id) {
		//? if >=1.21 {
		return Identifier.parse(
			id
		);
		//?} else {
		/*return new ResourceLocation(
			id
		);
		*///?}
	}

	public static String makeStringID(String name) {
		return MOD_ID + ":" + name;
	}

	public static void init() {
		Structurify.getConfig().create();
		ModChecker.setupModCompat();

		LoadConfigEvent.EVENT.addListener(StructurifyConfigSerializer::loadConfig);
		UpdateRegistriesEvent.EVENT.addListener(StructurifyRegistryUpdater::updateRegistries);
	}
}