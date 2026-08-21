package com.faboslav.structurify.fabric.tests.parity;

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

//? if >= 1.21.5 {
import net.fabricmc.fabric.api.gametest.v1.GameTest;
//?} else {
/*import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
*///?}

public final class StructureLocateTest
{
	private static final BlockPos SEARCH_ORIGIN = BlockPos.ZERO;
	private static final int SEARCH_RADIUS_IN_CHUNKS = 200;
	private static final int EXPECTED_VILLAGE_X = -1792;
	private static final int EXPECTED_VILLAGE_Z = -2016;

	//? if >= 1.21.5 {
	@GameTest
	//?} else {
	/*@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	*///?}
	public void locatesVillage(GameTestHelper helper) {
		// The expected village position is only verified for 26.2, every other version passes without checking
		//? if >= 26.2 {
		ServerLevel level = helper.getLevel();

		//? if > 1.21.1 {
		Holder<Structure> village = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.VILLAGE_PLAINS);
		//?} else {
		/*Holder<Structure> village = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(BuiltinStructures.VILLAGE_PLAINS);
		 *///?}

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
		//?} else {
		/*helper.succeed();
		*///?}
	}

	private static void assertTrue(GameTestHelper helper, boolean condition, String message) {
		//? if >= 1.21.11 {
		helper.assertTrue(condition, message);
		//?} else if >= 1.21.5 {
		/*helper.assertTrue(condition, net.minecraft.network.chat.Component.literal(message));
		*///?} else {
		/*helper.assertTrue(condition, message);
		*///?}
	}
}
