package com.faboslav.structurify.common.config.data.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BiomeCheckData
{
	public static final boolean OVERRIDE_GLOBAL_BIOME_CHECK_DEFAULT_VALUE = false;
	public final static boolean IS_ENABLED_DEFAULT_VALUE = false;
	public final static BiomeCheckMode MODE_DEFAULT_VALUE = BiomeCheckMode.BLACKLIST;
	//? if >= 1.21.1 {
	public final static List<String> BLACKLISTED_BIOMES_DEFAULT_VALUE = List.of("#minecraft:is_river", "#c:is_river", "#minecraft:is_ocean", "#c:is_ocean");
	//?} else {
	/*public final static List<String> BLACKLISTED_BIOMES_DEFAULT_VALUE = List.of("#minecraft:is_river", "#c:river", "#minecraft:is_ocean", "#c:ocean");
	*///?}

	private boolean overrideGlobalBiomeCheck = OVERRIDE_GLOBAL_BIOME_CHECK_DEFAULT_VALUE;
	private boolean isEnabled = IS_ENABLED_DEFAULT_VALUE;
	private BiomeCheckMode mode = MODE_DEFAULT_VALUE;
	private List<String> blacklistedBiomes = BLACKLISTED_BIOMES_DEFAULT_VALUE;

	public BiomeCheckData() {
	}

	public boolean isUsingDefaultValues() {
		var blacklistedBiomes = new ArrayList<>(this.blacklistedBiomes);
		var defaultBlacklistedBiomes = new ArrayList<>(BLACKLISTED_BIOMES_DEFAULT_VALUE);

		Collections.sort(blacklistedBiomes);
		Collections.sort(defaultBlacklistedBiomes);

		return
			this.overrideGlobalBiomeCheck == OVERRIDE_GLOBAL_BIOME_CHECK_DEFAULT_VALUE
			&& this.isEnabled == IS_ENABLED_DEFAULT_VALUE
			&& this.mode == MODE_DEFAULT_VALUE
			&& blacklistedBiomes.equals(defaultBlacklistedBiomes);
	}

	public boolean isOverridingGlobalBiomeCheck() {
		return this.overrideGlobalBiomeCheck;
	}

	public void overrideGlobalBiomeCheck(boolean overrideGlobalBiomeCheck) {
		this.overrideGlobalBiomeCheck = overrideGlobalBiomeCheck;
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void enable(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public BiomeCheckMode getMode() {
		return this.mode;
	}

	public void setMode(BiomeCheckMode mode) {
		this.mode = mode;
	}

	public List<String> getBlacklistedBiomes() {
		return this.blacklistedBiomes;
	}

	public void setBlacklistedBiomes(List<String> blacklistedBiomes) {
		this.blacklistedBiomes = blacklistedBiomes;
	}

	public enum BiomeCheckMode
	{
		STRICT,
		BLACKLIST
	}
}
