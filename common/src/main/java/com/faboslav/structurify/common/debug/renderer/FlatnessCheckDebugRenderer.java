package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureFlatnessCheckOverview;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureFlatnessCheckSample;
//? if < 1.21.11 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
*///?}
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public final class FlatnessCheckDebugRenderer
{
	public static void renderStructureFlatnessCheckOverview(
		StructureFlatnessCheckOverview structureFlatnessCheckOverview
		//? if < 1.21.11 {
		/*, Minecraft minecraft,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
		*///?}
	) {
		BoundingBox structureBoundingBox = structureFlatnessCheckOverview.structureBoundingBox();
		String label = structureFlatnessCheckOverview.toString();

		//? if >= 1.21.11 {
		RenderUtil.renderBoundingBox(structureBoundingBox, 0.8f);
		RenderUtil.renderLabel(structureBoundingBox, label);
		StructureDebugRenderer.renderStructurePieces(structureBoundingBox, structureFlatnessCheckOverview.structurePieces());
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
		StructureDebugRenderer.renderStructurePieces(structureBoundingBox, structureFlatnessCheckOverview.structurePieces(), minecraft, poseStack, bufferSource, camX, camY, camZ);
		*///?}
	}

	public static void renderStructureFlatnessCheckSample(
		StructureFlatnessCheckSample structureFlatnessCheckSample
		//? if < 1.21.11 {
		/*, PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
		*///?}
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

		//? if >= 1.21.11 {
		AABB col = new AABB(x, occY, z, x + 1, freeY, z + 1);
		RenderUtil.renderLineBox(col, r, g, b, a);
		//?} else {
		/*AABB col = new AABB(x, occY, z, x + 1, freeY, z + 1).move(-camX, -camY, -camZ);
		RenderUtil.renderLineBox(
			poseStack,
			bufferSource.getBuffer(RenderTypes.lines()),
			col,
			r, g, b, a
		);
		*///?}
	}
}
