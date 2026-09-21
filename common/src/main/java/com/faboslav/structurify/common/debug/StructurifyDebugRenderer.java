package com.faboslav.structurify.common.debug;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.config.data.DebugData;
import com.faboslav.structurify.common.debug.renderer.BiomeCheckDebugRenderer;
import com.faboslav.structurify.common.debug.renderer.FlatnessCheckDebugRenderer;
import com.faboslav.structurify.common.debug.renderer.StructureOverlapDebugRenderer;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureBiomeCheckOverview;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureBiomeCheckSample;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureFlatnessCheckOverview;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureFlatnessCheckSample;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

//? if < 1.21.11 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;
*///?}

public final class StructurifyDebugRenderer
{
	public void render(
		Minecraft minecraft,
		//? if < 1.21.11 {
		//@Nullable PoseStack poseStack,
		//?}
		Vec3 cameraPosition
		//? if < 1.21.11 {
		//, @Nullable MultiBufferSource bufferSource
		//?}
	) {
		DebugData debugData = Structurify.getConfig().getDebugData();

		if (!debugData.isEnabled()) {
			return;
		}

		//? if < 1.21.11 {
		/*if (poseStack == null) {
			poseStack = new PoseStack();
		}

		MultiBufferSource.BufferSource fallbackBufferSource = null;

		if (bufferSource == null) {
			fallbackBufferSource = minecraft.renderBuffers().bufferSource();
			bufferSource = fallbackBufferSource;
		}
		*///?}

		DebugData.DebugMode debugMode = debugData.getDebugMode();

		var singlePlayerServer = minecraft.getSingleplayerServer();
		int chunkRadius = Math.max(singlePlayerServer == null ? 6 : singlePlayerServer.getPlayerList().getViewDistance(), 6);

		final BlockPos cameraBlockPosition = BlockPos.containing(cameraPosition);
		final double camX = cameraPosition.x;
		final double camY = cameraPosition.y;
		final double camZ = cameraPosition.z;

		if (debugMode == DebugData.DebugMode.FLATNESS) {
			var structureFlatnessCheckOverviews = debugData.getStructureFlatnessCheckOverviews();

			synchronized (structureFlatnessCheckOverviews) {
				for (StructureFlatnessCheckOverview structureFlatnessCheckOverview : structureFlatnessCheckOverviews.values().stream().filter(o -> isWithinChunkRadius(cameraBlockPosition, o.structureBoundingBox().getCenter(), chunkRadius)).toList()) {
					FlatnessCheckDebugRenderer.renderStructureFlatnessCheckOverview(
						structureFlatnessCheckOverview
						//? if < 1.21.11 {
						/*, minecraft,
						poseStack,
						bufferSource,
						camX,
						camY,
						camZ
						*///?}
					);
				}
			}

			var structureFlatnessCheckSamples = debugData.getStructureFlatnessCheckSamples();

			synchronized (structureFlatnessCheckSamples) {
				for (StructureFlatnessCheckSample structureFlatnessCheckSample : structureFlatnessCheckSamples.values().stream().flatMap(Collection::stream).filter(o -> isWithinChunkRadius(cameraBlockPosition, o.x(), o.freeY(), o.z(), chunkRadius)).toList()) {
					FlatnessCheckDebugRenderer.renderStructureFlatnessCheckSample(
						structureFlatnessCheckSample
						//? if < 1.21.11 {
						/*, poseStack,
						bufferSource,
						camX,
						camY,
						camZ
						*///?}
					);
				}
			}
		} else if (debugMode == DebugData.DebugMode.BIOME) {
			var structureBiomeCheckOverviews = debugData.getStructureBiomeCheckOverviews();

			synchronized (structureBiomeCheckOverviews) {
				for (StructureBiomeCheckOverview structureBiomeCheckOverview : structureBiomeCheckOverviews.values().stream().filter(o -> isWithinChunkRadius(cameraBlockPosition, o.structureBoundingBox().getCenter(), chunkRadius)).toList()) {
					BiomeCheckDebugRenderer.renderStructureBiomeCheckOverview(
						structureBiomeCheckOverview
						//? if < 1.21.11 {
						/*, minecraft,
						poseStack,
						bufferSource,
						camX,
						camY,
						camZ
						*///?}
					);
				}
			}

			var structureBiomeCheckSamples = debugData.getStructureBiomeCheckSamples();

			synchronized (structureBiomeCheckSamples) {
				for (StructureBiomeCheckSample structureBiomeCheckSample : structureBiomeCheckSamples.values().stream().flatMap(Collection::stream).filter(o -> isWithinChunkRadius(cameraBlockPosition, o.x(), o.y(), o.z(), chunkRadius)).toList()) {
					BiomeCheckDebugRenderer.renderStructureBiomeCheckSample(
						structureBiomeCheckSample
						//? if < 1.21.11 {
						/*, poseStack,
						bufferSource,
						camX,
						camY,
						camZ
						*///?}
					);
				}
			}
		} else if (debugMode == DebugData.DebugMode.OVERLAP) {
			var level = minecraft.level;

			if (level != null && singlePlayerServer != null) {
				var serverLevel = singlePlayerServer.getLevel(level.dimension());

				if (serverLevel != null) {
					var chunkGenerator = (StructurifyChunkGenerator)serverLevel.getChunkSource().getGenerator();
					var structureSectionClaims = chunkGenerator.structurify$getStructureSectionClaims();

					synchronized (structureSectionClaims) {
						for (var entry : structureSectionClaims.entrySet()) {
							var sectionKey = entry.getKey();
							var structureSectionClaim = entry.getValue();
							var sectionPos = SectionPos.of(sectionKey);
							var pos = sectionPos.origin();

							if (isWithinChunkRadius(cameraBlockPosition, pos.getX(), pos.getY(), pos.getZ(), chunkRadius)) {
								StructureOverlapDebugRenderer.renderStructureSectionClaim(
									structureSectionClaim,
									pos
									//? if < 1.21.11 {
									/*, minecraft,
									poseStack,
									bufferSource,
									camX,
									camY,
									camZ
									*///?}
								);
							}
						}
					}
				}
			}
		}

		//? if < 1.21.11 {
		/*if (fallbackBufferSource != null) {
			fallbackBufferSource.endBatch();
		}
		*///?}
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
