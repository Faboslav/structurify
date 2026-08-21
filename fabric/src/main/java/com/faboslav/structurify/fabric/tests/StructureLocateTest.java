package com.faboslav.structurify.fabric.tests;

import com.faboslav.structurify.common.Structurify;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public final class StructureLocateTest
{
	private static final BlockPos SEARCH_ORIGIN = BlockPos.ZERO;
	private static final int SEARCH_RADIUS_IN_CHUNKS = 1000;
	private static final int EXPECTED_VILLAGE_X = -1792;
	private static final int EXPECTED_VILLAGE_Z = -2016;

	@GameTest
	public void locatePlainsVillageStructure(GameTestHelper helper) {
		//? if >= 26.2 {
		ServerLevel level = helper.getLevel();
		Holder<Structure> village = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.VILLAGE_PLAINS);

		Pair<BlockPos, Holder<Structure>> nearestVillage = level.getChunkSource().getGenerator().findNearestMapStructure(
			level,
			HolderSet.direct(village),
			SEARCH_ORIGIN,
			SEARCH_RADIUS_IN_CHUNKS,
			false
		);

		assertTrue(
			helper,
			nearestVillage != null,
			"Failed to locate a plains village within " + SEARCH_RADIUS_IN_CHUNKS + " chunks of " + SEARCH_ORIGIN
			+ " on seed " + level.getSeed()
		);

		BlockPos villagePos = nearestVillage.getFirst();

		Structurify.getLogger().info("Located a plains village at {} on seed {}", villagePos, level.getSeed());

		assertTrue(
			helper,
			villagePos.getX() == EXPECTED_VILLAGE_X && villagePos.getZ() == EXPECTED_VILLAGE_Z,
			"Expected the nearest plains village to be at [" + EXPECTED_VILLAGE_X + ", ~, " + EXPECTED_VILLAGE_Z + "]"
			+ " but it is at [" + villagePos.getX() + ", ~, " + villagePos.getZ() + "] on seed " + level.getSeed()
		);
		//?}

		helper.succeed();
	}

	private static void assertTrue(GameTestHelper helper, boolean condition, String message) {
		helper.assertTrue(condition, message);
	}
}
