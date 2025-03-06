package com.faboslav.structurify.common.modcompat;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.util.Platform;
import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Related code is based on The Bumblezone/Resourceful Lib mods with permissions from the authors
 *
 * @author TelepathicGrunt
 * <a href="https://github.com/TelepathicGrunt/Bumblezone">https://github.com/TelepathicGrunt/Bumblezone</a>
 * @author ThatGravyBoat
 * <a href="https://github.com/Team-Resourceful/ResourcefulLib">https://github.com/Team-Resourceful/ResourcefulLib</a>
 */
public final class ModChecker
{
	public static final List<ModCompat> CUSTOM_RESOURCE_PACK_PROVIDER_COMPATS = new ArrayList<>();

	public static void setupModCompat() {
		String modId = "";

		try {
			//? global_packs: >0 {
			loadModCompat("globalpacks", () -> new GlobalPacksCompat());
			//?}

			//? open_loader: >0 {
			loadModCompat("openloader", () -> new OpenLoaderCompat());
			//?}

			setupPlatformModCompat();
		} catch (Throwable e) {
			Structurify.getLogger().error("Failed to setup mod compats");
			e.printStackTrace();
		}
	}

	@ExpectPlatform
	public static void setupPlatformModCompat() {
		throw new AssertionError();
	}

	public static void loadModCompat(String modId, Supplier<ModCompat> loader) {
		try {
			if (Platform.isModLoaded(modId)) {
				ModCompat compat = loader.get();
				if (compat.compatTypes().contains(ModCompat.Type.CUSTOM_RESOURCE_PACK_PROVIDERS)) {
					CUSTOM_RESOURCE_PACK_PROVIDER_COMPATS.add(compat);
				}
			}
		} catch (Throwable e) {
			Structurify.getLogger().error("Failed to load compat with " + modId);
			e.printStackTrace();
		}
	}
}
