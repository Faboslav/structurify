package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureFlatnessCheckOverview;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureFlatnessCheckSample;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
//? if >= 1.21.11 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
 *///?}
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public final class FlatnessCheckDebugRenderer
{
	public static void renderStructureFlatnessCheckOverview(
		StructureFlatnessCheckOverview structureFlatnessCheckOverview,
		Minecraft minecraft,
		PoseStack poseStack,
		//? if >= 1.21.11 {
		SubmitNodeCollector submitNodeCollector,
		//?} else {
		/*MultiBufferSource bufferSource,
		 *///?}
		double camX,
		double camY,
		double camZ
	) {
		BoundingBox structureBoundingBox = structureFlatnessCheckOverview.structureBoundingBox();

		//? if >= 1.21.11 {
		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, vertexConsumer) -> {
			RenderUtil.renderBoundingBox(structureBoundingBox, poseStack, vertexConsumer, camX, camY, camZ, 0.8f);
		});
		RenderUtil.renderLabel(structureBoundingBox, structureFlatnessCheckOverview.toString(), minecraft, poseStack, submitNodeCollector, camX, camY, camZ);
		StructureDebugRenderer.renderStructurePieces(structureBoundingBox, structureFlatnessCheckOverview.structurePieces(), minecraft, poseStack, submitNodeCollector, camX, camY, camZ);
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
		RenderUtil.renderLabel(structureBoundingBox, structureFlatnessCheckOverview.toString(), minecraft, poseStack, bufferSource, camX, camY, camZ);
		StructureDebugRenderer.renderStructurePieces(structureBoundingBox, structureFlatnessCheckOverview.structurePieces(), minecraft, poseStack, bufferSource, camX, camY, camZ);
		*///?}
	}

	public static void renderStructureFlatnessCheckSample(
		StructureFlatnessCheckSample structureFlatnessCheckSample,
		PoseStack poseStack,
		//? if >= 1.21.11 {
		SubmitNodeCollector submitNodeCollector,
		//?} else {
		/*MultiBufferSource bufferSource,
		 *///?}
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

		//? if >= 1.21.11 {
		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, vertexConsumer) -> {
			RenderUtil.renderLineBox(poseStack, vertexConsumer, col, r, g, b, a);
		});
		//?} else {
		/*RenderUtil.renderLineBox(
			poseStack,
			bufferSource.getBuffer(RenderTypes.lines()),
			col,
			r, g, b, a
		);
		*///?}
	}
}