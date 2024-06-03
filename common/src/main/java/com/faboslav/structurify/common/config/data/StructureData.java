package com.faboslav.structurify.common.config.data;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;

public class StructureData
{
	private boolean isDisabled;
	private final Set<String> biomes;
	private List<String> blacklistedBiomes;
	private BiomeBlacklistType biomeBlacklistType = BiomeBlacklistType.CENTER_PART;

		public StructureData(boolean isDisabled, Set<String> biomes, List<String> blacklistedBiomes) {
		this.isDisabled = isDisabled;
		this.biomes = biomes;
		this.blacklistedBiomes = blacklistedBiomes;
	}

	public boolean isDisabled() {
		return this.isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Set<String> getBiomes() {
		return this.biomes;
	}

	public BiomeBlacklistType getBiomeBlacklistType() {
		return this.biomeBlacklistType;
	}

	public void setBiomeBlacklistType(BiomeBlacklistType biomeBlacklistType) {
			this.biomeBlacklistType = biomeBlacklistType;
	}

	public List<String> getBlacklistedBiomes() {
		return this.blacklistedBiomes;
	}

	public void setBlacklistedBiomes(List<String> blacklistedBiomes) {
		this.blacklistedBiomes = blacklistedBiomes;
	}

	public enum BiomeBlacklistType implements NameableEnum
	{
		CENTER_PART,
		ALL_PARTS;

		@Override
		public Text getDisplayName() {
			return Text.translatable("gui.structurized.structure." + name().toLowerCase());
		}
	}
}