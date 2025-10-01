package com.faboslav.structurify.common.debug;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.DebugData;
import com.faboslav.structurify.common.mixin.structure.pools.SinglePoolElementAccessor;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureBiomeCheckOverview;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureBiomeCheckSample;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureFlatnessCheckOverview;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureFlatnessCheckSample;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//? >= 1.21.3 {
import net.minecraft.client.renderer.ShapeRenderer;
//?} else {
/*import net.minecraft.client.renderer.LevelRenderer;
*///?}

public final class StructurifyDebugRenderer
{
	public final Map<Long, StructureFlatnessCheckOverview> structureFlatnessCheckOverviews = new ConcurrentHashMap<>();
	public final Map<Long, Set<StructureFlatnessCheckSample>> structureFlatnessCheckSamples = new ConcurrentHashMap<>();

	public final Map<Long, StructureBiomeCheckOverview> structureBiomeCheckOverviews= new ConcurrentHashMap<>();
	public final Map<Long, Set<StructureBiomeCheckSample>> structureBiomeCheckSamples = new ConcurrentHashMap<>();

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

		if(bufferSource == null) {
			bufferSource = minecraft.renderBuffers().bufferSource();
		}

		int viewDistance = minecraft.getSingleplayerServer().getPlayerList().getViewDistance();
		int chunkRadius = Math.max(1, viewDistance);

		final Camera camera = minecraft.gameRenderer.getMainCamera();
		final var cameraPosition = camera.getPosition();
		final BlockPos cameraBlockPosition =  BlockPos.containing(cameraPosition);
		final double camX = cameraPosition.x;
		final double camY = cameraPosition.y;
		final double camZ = cameraPosition.z;

		if(debugMode == DebugData.DebugMode.FLATNESS) {
			synchronized (this.structureFlatnessCheckOverviews) {
				for (StructureFlatnessCheckOverview structureFlatnessCheckOverview : this.structureFlatnessCheckOverviews.values().stream().filter(o -> isWithinChunkRadius(cameraBlockPosition, o.structureBoundingBox().getCenter(), chunkRadius)).toList()) {
					this.renderStructureFlatnessCheckOverview(structureFlatnessCheckOverview, minecraft, poseStack, bufferSource, camX, camY, camZ);
				}
			}

			synchronized (this.structureFlatnessCheckSamples) {
				for (StructureFlatnessCheckSample structureFlatnessCheckSample : this.structureFlatnessCheckSamples.values().stream().flatMap(Collection::stream).filter(o -> isWithinChunkRadius(cameraBlockPosition, o.x(), o.freeY(), o.z(), chunkRadius)).toList()) {
					this.renderStructureFlatnessCheckSample(structureFlatnessCheckSample, poseStack, bufferSource, camX, camY, camZ);
				}
			}
		} else if(debugMode == DebugData.DebugMode.BIOME) {
			synchronized (this.structureBiomeCheckOverviews) {
				for (StructureBiomeCheckOverview structureBiomeCheckOverview : this.structureBiomeCheckOverviews.values().stream().filter(o -> isWithinChunkRadius(cameraBlockPosition, o.structureBoundingBox().getCenter(), chunkRadius)).toList()) {
					this.renderStructureBiomeCheckOverview(structureBiomeCheckOverview, minecraft, poseStack, bufferSource, camX, camY, camZ);
				}
			}

			synchronized (this.structureBiomeCheckSamples) {
				for (StructureBiomeCheckSample structureBiomeCheckSample : structureBiomeCheckSamples.values().stream().flatMap(Collection::stream).filter(o -> isWithinChunkRadius(cameraBlockPosition, o.x(), o.y(), o.z(), chunkRadius)).toList()) {
					this.renderStructureBiomeCheckSample(structureBiomeCheckSample, poseStack, bufferSource, camX, camY, camZ);
				}
			}
		}
	}

	private void renderStructureFlatnessCheckOverview(
		StructureFlatnessCheckOverview structureFlatnessCheckOverview,
		Minecraft minecraft,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
	) {
		BoundingBox structureBoundingBox = structureFlatnessCheckOverview.structureBoundingBox();
		renderBoundingBox(structureBoundingBox, poseStack, bufferSource.getBuffer(RenderType.lines()), camX, camY, camZ, 0.8f);
		renderLabel(structureBoundingBox, structureFlatnessCheckOverview.toString(), minecraft, poseStack, bufferSource, camX, camY, camZ);

		this.renderStructurePiecesForOverview(structureBoundingBox, structureFlatnessCheckOverview.structurePieces(), minecraft, poseStack, bufferSource, camX, camY, camZ);
	}

	private void renderStructureBiomeCheckOverview(
		StructureBiomeCheckOverview structureBiomeCheckOverview,
		Minecraft minecraft,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
	) {
		BoundingBox structureBoundingBox = structureBiomeCheckOverview.structureBoundingBox();
		renderBoundingBox(structureBoundingBox, poseStack, bufferSource.getBuffer(RenderType.lines()), camX, camY, camZ, 0.8f);
		renderLabel(structureBoundingBox, structureBiomeCheckOverview.toString(), minecraft, poseStack, bufferSource, camX, camY, camZ);

		this.renderStructurePiecesForOverview(structureBoundingBox, structureBiomeCheckOverview.structurePieces(), minecraft, poseStack, bufferSource, camX, camY, camZ);
	}

	private void renderStructurePiecesForOverview(
		BoundingBox structureBoundingBox,
		List<StructurePiece> structurePieces,
		Minecraft minecraft,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
	) {
		for (var structurePiece : structurePieces) {
			var structurePieceBoundingBox = structurePiece.getBoundingBox();

			if (structurePieceBoundingBox.equals(structureBoundingBox)) {
				continue;
			}

			renderBoundingBox(structurePieceBoundingBox, poseStack, bufferSource.getBuffer(RenderType.lines()), camX, camY, camZ, 0.2f);

			String pieceName = "";

			if (structurePiece instanceof PoolElementStructurePiece poolPiece) {
				StructurePoolElement element = poolPiece.getElement();

				if (element instanceof SinglePoolElement single) {
					ResourceLocation template = ((SinglePoolElementAccessor) single).getTemplate().left().orElse(null);

					if (template != null) {
						pieceName = template.toString();
					}
				} else {
					pieceName = "";
				}
			}

			renderLabel(structurePieceBoundingBox, pieceName, minecraft, poseStack, bufferSource, camX, camY, camZ);
		}
	}

	private void renderStructureFlatnessCheckSample(
		StructureFlatnessCheckSample structureFlatnessCheckSample,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
	) {
		final int x = structureFlatnessCheckSample.x();
		final int z = structureFlatnessCheckSample.z();
		final int occY = structureFlatnessCheckSample.occY();
		final int freeY = Math.max(structureFlatnessCheckSample.freeY(), occY + 1);

		final boolean solid = structureFlatnessCheckSample.isSolid();
		final float r = solid ? 0.2f:1.0f;
		final float g = solid ? 1.0f:0.2f;
		final float b = 0.2f;
		final float a = 0.95f;

		AABB col = new AABB(x, occY, z, x + 1, freeY, z + 1).move(-camX, -camY, -camZ);

		renderLineBox(
			poseStack,
			bufferSource.getBuffer(RenderType.lines()),
			col,
			r, g, b, a
		);
	}

	private void renderStructureBiomeCheckSample(
		StructureBiomeCheckSample structureBiomeCheckSample,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
	) {
		final int x = structureBiomeCheckSample.x();
		final int y = structureBiomeCheckSample.y();
		final int z = structureBiomeCheckSample.z();
		final boolean result = structureBiomeCheckSample.result();

		final float r = result ? 0.2f:1.0f;
		final float g = result ? 1.0f:0.2f;
		final float b = 0.2f;
		final float a = 0.95f;

		AABB col = new AABB(x, y, z, x + 1, y + 1, z + 1).move(-camX, -camY, -camZ);
	}

	public void addStructureFlatnessCheckSample(Long structureKey, StructureFlatnessCheckSample structureFlatnessCheckSample) {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureFlatnessCheckSamples) {
			if(!this.structureFlatnessCheckSamples.containsKey(structureKey)) {
				this.structureFlatnessCheckSamples.put(structureKey, ConcurrentHashMap.newKeySet());
			}

			this.structureFlatnessCheckSamples.get(structureKey).add(structureFlatnessCheckSample);
		}
	}

	public void removeStructureFlatnessCheckSamples(Long structureKey) {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		this.structureFlatnessCheckSamples.remove(structureKey);
	}

	public void clearStructureFlatnessCheckSamples() {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureFlatnessCheckSamples) {
			this.structureFlatnessCheckSamples.clear();
		}
	}

	public void clearStructureFlatnessCheckOverviews() {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureFlatnessCheckOverviews) {
			this.structureFlatnessCheckOverviews.clear();
		}
	}

	public void addStructureFlatnessCheckInfo(Long structureKey, StructureFlatnessCheckOverview structureFlatnessCheckInfo) {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureFlatnessCheckOverviews) {
			this.structureFlatnessCheckOverviews.putIfAbsent(structureKey, structureFlatnessCheckInfo);
		}
	}

	public void removeStructureFlatnessCheckInfo(Long structureKey) {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		this.structureFlatnessCheckOverviews.remove(structureKey);
	}

	public void addStructureBiomeCheckOverview(Long structureKey, StructureBiomeCheckOverview structureBiomeCheckOverview) {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureBiomeCheckOverviews) {
			this.structureBiomeCheckOverviews.putIfAbsent(structureKey, structureBiomeCheckOverview);
		}
	}

	public void removeStructureBiomeCheckOverview(Long structureKey) {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		this.structureBiomeCheckOverviews.remove(structureKey);
	}

	public void clearStructureBiomeCheckOverviews() {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureBiomeCheckOverviews) {
			this.structureBiomeCheckOverviews.clear();
		}
	}

	public void addStructureBiomeCheckSample(Long structureKey, StructureBiomeCheckSample structureBiomeCheckSample) {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureBiomeCheckSamples) {
			if(!this.structureBiomeCheckSamples.containsKey(structureKey)) {
				this.structureBiomeCheckSamples.put(structureKey, ConcurrentHashMap.newKeySet());
			}

			this.structureBiomeCheckSamples.get(structureKey).add(structureBiomeCheckSample);
		}
	}

	public void removeStructureBiomeCheckSamples(Long structureKey) {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		this.structureBiomeCheckSamples.remove(structureKey);
	}

	public void clearStructureBiomeCheckSamples() {
		if(!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureBiomeCheckSamples) {
			this.structureBiomeCheckSamples.clear();
		}
	}


	private static void renderBoundingBox(
		BoundingBox boundingBox,
		PoseStack poseStack,
		VertexConsumer vertexConsumer,
		double cameraX,
		double cameraY,
		double cameraZ,
		float alpha
	) {
			var aabb = new AABB(
				boundingBox.minX(), boundingBox.minY(), boundingBox.minZ(),
				boundingBox.maxX() + 1, boundingBox.maxY() + 1, boundingBox.maxZ() + 1
			).move(-cameraX, -cameraY, -cameraZ);

			renderLineBox(
				poseStack,
				vertexConsumer,
				aabb,
				1.0f, 1.0f, 1.0f, alpha
			);
	}

	public static void renderLineBox(PoseStack poseStack, VertexConsumer buffer, AABB box, float red, float green, float blue, float alpha) {
		//? >= 1.21.9 {
		ShapeRenderer.renderLineBox(poseStack.last(), buffer, box, red, green, blue, alpha);
		//?} else if >= 1.21.3 {
		/*ShapeRenderer.renderLineBox(poseStack, buffer, box, red, green, blue, alpha);
		*///?} else {
		/*LevelRenderer.renderLineBox(poseStack, buffer, box, red, green, blue, alpha);
		*///?}
	}

	private void renderLabel(
		BoundingBox box,
		String label,
		Minecraft mc,
		PoseStack poseStack,
		MultiBufferSource buffers,
		double camX,
		double camY,
		double camZ
	) {
		// Center of the box (block-aligned)
		double centerX = (box.minX() + box.maxX() + 1) * 0.5;
		double centerZ = (box.minZ() + box.maxZ() + 1) * 0.5;

		// Put text just above the top
		double topY   = box.maxY() + 1.0;
		double labelY = topY + 0.25;

		poseStack.pushPose();
		poseStack.translate(centerX - camX, labelY - camY, centerZ - camZ);

		// Face the camera
		poseStack.mulPose(mc.gameRenderer.getMainCamera().rotation());

		// ----- Dynamic scale from bounding box size (min = 1) -----
		// Use horizontal diagonal length as a size proxy
		float spanX = box.getXSpan();
		float spanZ = box.getZSpan();
		float diagXZ = (float)Math.sqrt(spanX * spanX + spanZ * spanZ);

		// Tune the divisor to your liking; bigger divisor = smaller growth
		// Min scale factor is 1 so small structures stay readable.
		float dynamicScaleFactor = Math.max(1.0f, diagXZ / 24.0f);

		// Minecraft text world-scale baseline
		float base = 0.025f;
		float scale = base * dynamicScaleFactor;

		// Negative X/Y to keep text upright (standard billboard text trick)
		poseStack.scale(-scale, -scale, scale);

		Font font = mc.font;
		String[] lines = label.split("\n");
		int lineHeight = font.lineHeight;

		// Offset so the first line starts above the box, not centered
		int totalPixelHeight = lines.length * lineHeight;
		float yStart = -totalPixelHeight; // shift whole block upward

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			int width = font.width(line);

			font.drawInBatch(
				line,
				-width / 2f,
				yStart + i * lineHeight,
				0xFFFFFFFF,
				false,
				poseStack.last().pose(),
				buffers,
				Font.DisplayMode.SEE_THROUGH,
				0,
				0xF000F0
			);
		}

		poseStack.popPose();
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