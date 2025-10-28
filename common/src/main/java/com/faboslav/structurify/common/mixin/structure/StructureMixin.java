package com.faboslav.structurify.common.mixin.structure;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import com.faboslav.structurify.common.util.BiomeUtil;
import com.faboslav.structurify.world.level.structure.checks.StructureChecker;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Predicate;

//? if >= 1.21.4 {
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
//?}

@Mixin(Structure.class)
public abstract class StructureMixin implements StructurifyStructure
{
	@Unique
	@Nullable
	public ResourceLocation structurify$structureIdentifier = null;

	public void structurify$setStructureIdentifier(ResourceLocation structureSetIdentifier) {
		this.structurify$structureIdentifier = structureSetIdentifier;
		this.structurify$globalStructureNamespaceData = null;
		this.structurify$structureNamespaceData = null;
		this.structurify$structureData = null;
	}

	@Nullable
	public ResourceLocation structurify$getStructureIdentifier() {
		return this.structurify$structureIdentifier;
	}

	@Unique
	@Nullable
	private StructureNamespaceData structurify$globalStructureNamespaceData = null;

	@Unique
	public StructureNamespaceData structurify$getGlobalStructureNamespaceData() {
		if (this.structurify$globalStructureNamespaceData == null) {
			this.structurify$globalStructureNamespaceData = Structurify.getConfig().getStructureNamespaceData().get(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER);
		}

		return this.structurify$globalStructureNamespaceData;
	}

	@Unique
	@Nullable
	private StructureNamespaceData structurify$structureNamespaceData = null;

	@Unique
	@Nullable
	public StructureNamespaceData structurify$getStructureNamespaceData() {
		return this.structurify$getStructureNamespaceData(this.structurify$getStructureIdentifier());
	}

	@Unique
	@Nullable
	public StructureNamespaceData structurify$getStructureNamespaceData(@Nullable ResourceLocation structureIdentifier) {
		if (this.structurify$structureNamespaceData == null) {
			if(structureIdentifier == null) {
				structureIdentifier = this.structurify$getStructureIdentifier();
			}

			if (structureIdentifier != null) {
				this.structurify$structureNamespaceData = Structurify.getConfig().getStructureNamespaceData().get(structureIdentifier.getNamespace());
			}
		}

		return this.structurify$structureNamespaceData;
	}

	@Unique
	@Nullable
	private StructureData structurify$structureData = null;

	@Unique
	@Nullable
	public StructureData structurify$getStructureData() {
		if (this.structurify$structureData == null) {
			var structureIdentifier = this.structurify$getStructureIdentifier();

			if (structureIdentifier != null) {
				this.structurify$structureData = Structurify.getConfig().getStructureData().get(structureIdentifier.toString());
			}
		}

		return this.structurify$structureData;
	}

	@Unique
	@Nullable
	public StructureData structurify$getStructureData(@Nullable ResourceLocation structureIdentifier) {
		if (this.structurify$structureData == null) {
			if(structureIdentifier == null) {
				structureIdentifier = this.structurify$getStructureIdentifier();
			}

			if (structureIdentifier != null) {
				this.structurify$structureData = Structurify.getConfig().getStructureData().get(structureIdentifier.toString());
			}
		}

		return this.structurify$structureData;
	}

	@Unique
	@Nullable
	public HolderSet<Biome> structurify$structureBiomes = null;

	public void structurify$setStructureBiomes(@Nullable HolderSet<Biome> biomeHolderSet) {
		this.structurify$structureBiomes = biomeHolderSet;
	}

	@Nullable
	public HolderSet<Biome> structurify$getStructureBiomes() {
		return this.structurify$structureBiomes;
	}

	@Unique
	@Nullable
	public HolderSet<Biome> structurify$structureBlacklistedBiomes = null;

	public void structurify$setStructureBlacklistedBiomes(@Nullable HolderSet<Biome> biomeHolderSet) {
		this.structurify$structureBlacklistedBiomes = biomeHolderSet;
	}

	@Nullable
	public HolderSet<Biome> structurify$getStructureBlacklistedBiomes() {
		if (this.structurify$structureBlacklistedBiomes == null) {
			var blacklistedBiomeHolderSet = BiomeUtil.getBlacklistedBiomes(structurify$getStructureIdentifier());
			this.structurify$setStructureBlacklistedBiomes(blacklistedBiomeHolderSet);
		}

		return this.structurify$structureBlacklistedBiomes;
	}

	@WrapMethod(
		method = "biomes"
	)
	private HolderSet<Biome> structurify$biomes(
		Operation<HolderSet<Biome>> original
	) {
		if (this.structurify$structureBiomes == null) {
			var biomeHolderSet = BiomeUtil.getBiomes(structurify$getStructureIdentifier(), original.call());
			this.structurify$setStructureBiomes(biomeHolderSet);
		}

		return this.structurify$getStructureBiomes();
	}

	@WrapMethod(
		method = "step"
	)
	private GenerationStep.Decoration structurify$step(
		Operation<GenerationStep.Decoration> original
	) {
		var structureData = this.structurify$getStructureData();

		if (structureData == null) {
			return original.call();
		}

		return structureData.getStep();
	}

	@WrapMethod(
		method = "terrainAdaptation"
	)
	private TerrainAdjustment structurify$terrainAdaptation(
		Operation<TerrainAdjustment> original
	) {
		var structureData = this.structurify$getStructureData();

		if (structureData == null) {
			return original.call();
		}

		return structureData.getTerrainAdaptation();
	}

	@WrapMethod(
		method = "generate"
	)
	private StructureStart structurify$generate(
		//? if >= 1.21.4 {
		Holder<Structure> structure,
		ResourceKey<Level> level,
		//?}
		RegistryAccess registryAccess,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		RandomState randomState,
		StructureTemplateManager structureTemplateManager,
		long seed,
		ChunkPos chunkPos,
		int references,
		LevelHeightAccessor heightAccessor,
		Predicate<Holder<Biome>> validBiome,
		Operation<StructureStart> original
	) {
		//? if >= 1.21.4 {
		var structureStart = original.call(structure, level, registryAccess, chunkGenerator, biomeSource, randomState, structureTemplateManager, seed, chunkPos, references, heightAccessor, validBiome);
		 //?} else {
		/*var structureStart = original.call(registryAccess, chunkGenerator, biomeSource, randomState, structureTemplateManager, seed, chunkPos, references, heightAccessor, validBiome);
		*///?}
		ResourceLocation structureId = null;

		//? if >= 1.21.4 {
		var possibleStructureId = structure.unwrapKey();
		structureId = possibleStructureId.map(ResourceKey::location).orElseGet(this::structurify$getStructureIdentifier);
		//?} else {
		/*structureId = this.structurify$getStructureIdentifier();
		*///?}

		if (structureStart == StructureStart.INVALID_START) {
			return structureStart;
		}

		var structureCheckResult = StructureChecker.checkStructure(structureStart, structureId, this, chunkGenerator, heightAccessor, randomState, biomeSource);

		if (!structureCheckResult) {
			return StructureStart.INVALID_START;
		}

		return structureStart;
	}
}
