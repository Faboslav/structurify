package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureBiomeCheckOverview;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureBiomeCheckSample;
//? if < 1.21.11 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
*///?}
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class BiomeCheckDebugRenderer
{
	public static void renderStructureBiomeCheckOverview(
		StructureBiomeCheckOverview structureBiomeCheckOverview
		//? if < 1.21.11 {
		/*, Minecraft minecraft,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
		*///?}
	) {
		BoundingBox structureBoundingBox = structureBiomeCheckOverview.structureBoundingBox();
		String label = structureBiomeCheckOverview.toString();

		//? if >= 1.21.11 {
		RenderUtil.renderBoundingBox(structureBoundingBox, 0.8f);
		RenderUtil.renderLabel(structureBoundingBox, label);
		StructureDebugRenderer.renderStructurePieces(structureBoundingBox, structureBiomeCheckOverview.structurePieces());
		//?} else {
		/*RenderUtil.renderBoundingBox(
			structureBoundingBox,
			poseStack,
			bufferSource.getBuffer(RenderTypes.lines()),
			camX,
			camY,
			camZ,
			0.8f
		);
		RenderUtil.renderLabel(structureBoundingBox, label, minecraft, poseStack, bufferSource, camX, camY, camZ);
		StructureDebugRenderer.renderStructurePieces(structureBoundingBox, structureBiomeCheckOverview.structurePieces(), minecraft, poseStack, bufferSource, camX, camY, camZ);
		*///?}
	}

	public static void renderStructureBiomeCheckSample(
		StructureBiomeCheckSample structureBiomeCheckSample
		//? if < 1.21.11 {
		/*, PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
		*///?}
	) {
		final int x = structureBiomeCheckSample.x();
		final int y = structureBiomeCheckSample.y();
		final int z = structureBiomeCheckSample.z();
		final boolean result = structureBiomeCheckSample.result();

		final float r = result ? 0.2f:1.0f;
		final float g = result ? 1.0f:0.2f;
		final float b = 0.2f;
		final float a = 0.95f;

		//? if >= 1.21.11 {
		AABB col = new AABB(x, y, z, x + 1, y + 1, z + 1);
		RenderUtil.renderLineBox(col, r, g, b, a);
		//?} else {
		/*AABB col = new AABB(x, y, z, x + 1, y + 1, z + 1).move(-camX, -camY, -camZ);
		RenderUtil.renderLineBox(
			poseStack,
			bufferSource.getBuffer(RenderTypes.lines()),
			col,
			r, g, b, a
		);
		*///?}
	}
}
