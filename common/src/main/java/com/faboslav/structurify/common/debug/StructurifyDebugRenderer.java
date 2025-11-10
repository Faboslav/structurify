package com.faboslav.structurify.common.debug;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.config.data.DebugData;
import com.faboslav.structurify.common.debug.renderer.BiomeCheckDebugRenderer;
import com.faboslav.structurify.common.debug.renderer.FlatnessCheckDebugRenderer;
import com.faboslav.structurify.common.debug.renderer.StructureOverlapDebugRenderer;
import com.faboslav.structurify.world.level.structure.checks.StructureOverlapCheck;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureBiomeCheckOverview;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureBiomeCheckSample;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureFlatnessCheckOverview;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureFlatnessCheckSample;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class StructurifyDebugRenderer
{
	public void render(
		Minecraft minecraft,
		PoseStack poseStack,
		@Nullable MultiBufferSource bufferSource
	) {
		DebugData debugData = Structurify.getConfig().getDebugData();

		if (!debugData.isEnabled()) {
			return;
		}

		DebugData.DebugMode debugMode = debugData.getDebugMode();

		if (bufferSource == null) {
			bufferSource = minecraft.renderBuffers().bufferSource();
		}

		var singlePlayerServer = minecraft.getSingleplayerServer();
		int chunkRadius = Math.max(singlePlayerServer == null ? 8:singlePlayerServer.getPlayerList().getViewDistance(), 8);

		final Camera camera = minecraft.gameRenderer.getMainCamera();
		final var cameraPosition = camera.getPosition();
		final BlockPos cameraBlockPosition = BlockPos.containing(cameraPosition);
		final double camX = cameraPosition.x;
		final double camY = cameraPosition.y;
		final double camZ = cameraPosition.z;

		if (debugMode == DebugData.DebugMode.FLATNESS) {
			var structureFlatnessCheckOverviews = Structurify.getConfig().getDebugData().getStructureFlatnessCheckOverviews();
			synchronized (structureFlatnessCheckOverviews) {
				for (StructureFlatnessCheckOverview structureFlatnessCheckOverview : structureFlatnessCheckOverviews.values().stream().filter(o -> isWithinChunkRadius(cameraBlockPosition, o.structureBoundingBox().getCenter(), chunkRadius)).toList()) {
					FlatnessCheckDebugRenderer.renderStructureFlatnessCheckOverview(structureFlatnessCheckOverview, minecraft, poseStack, bufferSource, camX, camY, camZ);
				}
			}

			var structureFlatnessCheckSamples = Structurify.getConfig().getDebugData().getStructureFlatnessCheckSamples();
			synchronized (structureFlatnessCheckSamples) {
				for (StructureFlatnessCheckSample structureFlatnessCheckSample : structureFlatnessCheckSamples.values().stream().flatMap(Collection::stream).filter(o -> isWithinChunkRadius(cameraBlockPosition, o.x(), o.freeY(), o.z(), chunkRadius)).toList()) {
					FlatnessCheckDebugRenderer.renderStructureFlatnessCheckSample(structureFlatnessCheckSample, poseStack, bufferSource, camX, camY, camZ);
				}
			}
		} else if (debugMode == DebugData.DebugMode.BIOME) {
			var structureBiomeCheckOverviews = Structurify.getConfig().getDebugData().getStructureBiomeCheckOverviews();
			synchronized (structureBiomeCheckOverviews) {
				for (StructureBiomeCheckOverview structureBiomeCheckOverview : structureBiomeCheckOverviews.values().stream().filter(o -> isWithinChunkRadius(cameraBlockPosition, o.structureBoundingBox().getCenter(), chunkRadius)).toList()) {
					BiomeCheckDebugRenderer.renderStructureBiomeCheckOverview(structureBiomeCheckOverview, minecraft, poseStack, bufferSource, camX, camY, camZ);
				}
			}

			var structureBiomeCheckSamples = Structurify.getConfig().getDebugData().getStructureBiomeCheckSamples();
			synchronized (structureBiomeCheckSamples) {
				for (StructureBiomeCheckSample structureBiomeCheckSample : structureBiomeCheckSamples.values().stream().flatMap(Collection::stream).filter(o -> isWithinChunkRadius(cameraBlockPosition, o.x(), o.y(), o.z(), chunkRadius)).toList()) {
					BiomeCheckDebugRenderer.renderStructureBiomeCheckSample(structureBiomeCheckSample, poseStack, bufferSource, camX, camY, camZ);
				}
			}
		} else if (debugMode == DebugData.DebugMode.OVERLAP) {
			var level = minecraft.level;
			if (level != null && minecraft.getSingleplayerServer() != null) {
				var serverLevel = minecraft.getSingleplayerServer().getLevel(level.dimension());
				if (serverLevel != null) {
					var chunkGenerator = (StructurifyChunkGenerator) serverLevel.getChunkSource().getGenerator();
					var structureSectionClaims = chunkGenerator.structurify$getStructureSectionClaims();

					synchronized (structureSectionClaims) {
						for (var entry : structureSectionClaims.entrySet()) {
							var key = entry.getKey();
							var structureSectionClaim = entry.getValue();

							var pos = StructureOverlapCheck.unpackCell(
								key,
								StructureOverlapCheck.CELL_X,
								StructureOverlapCheck.CELL_Y,
								StructureOverlapCheck.CELL_Z
							);

							if (isWithinChunkRadius(cameraBlockPosition, pos.getX(), pos.getY(), pos.getZ(), chunkRadius)) {
								StructureOverlapDebugRenderer.renderStructureSectionClaim(structureSectionClaim, minecraft, poseStack, bufferSource, pos, camX, camY, camZ);
							}
						}
					}
				}
			}
		}
	}

	private static boolean isWithinChunkRadius(
		BlockPos cameraBlockPos,
		BlockPos targetBlockPos,
		int chunkRadius
	) {
		int camChunkX = SectionPos.blockToSectionCoord(cameraBlockPos.getX());
		int camChunkZ = SectionPos.blockToSectionCoord(cameraBlockPos.getZ());

		int tgtChunkX = SectionPos.blockToSectionCoord(targetBlockPos.getX());
		int tgtChunkZ = SectionPos.blockToSectionCoord(targetBlockPos.getZ());

		return Math.max(Math.abs(tgtChunkX - camChunkX), Math.abs(tgtChunkZ - camChunkZ)) <= chunkRadius;
	}

	private static boolean isWithinChunkRadius(
		BlockPos cameraBlockPos,
		int targetX,
		int targetY,
		int targetZ,
		int chunkRadius
	) {
		return isWithinChunkRadius(cameraBlockPos, new BlockPos(targetX, targetY, targetZ), chunkRadius);
	}
}