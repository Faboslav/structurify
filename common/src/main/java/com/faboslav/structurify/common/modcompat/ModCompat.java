package com.faboslav.structurify.common.modcompat;

import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Related code is based on The Bumblezone/Resourceful Lib mods with permissions from the authors
 *
 * @author TelepathicGrunt
 * <a href="https://github.com/TelepathicGrunt/Bumblezone">https://github.com/TelepathicGrunt/Bumblezone</a>
 * @author ThatGravyBoat
 * <a href="https://github.com/Team-Resourceful/ResourcefulLib">https://github.com/Team-Resourceful/ResourcefulLib</a>
 */
public interface ModCompat
{
	default EnumSet<Type> compatTypes() {
		return EnumSet.noneOf(Type.class);
	}

	default ArrayList<RepositorySource> getResourcePackProviders() {
		return new ArrayList<>();
	}

	enum Type
	{
		CUSTOM_RESOURCE_PACK_PROVIDERS,
		MOD
	}
}
