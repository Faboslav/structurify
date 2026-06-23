package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureBiomeCheckOverview;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureBiomeCheckSample;
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

public class BiomeCheckDebugRenderer
{
	public static void renderStructureBiomeCheckOverview(
		StructureBiomeCheckOverview structureBiomeCheckOverview,
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
		BoundingBox structureBoundingBox = structureBiomeCheckOverview.structureBoundingBox();

		//? if >= 1.21.11 {
		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, vertexConsumer) -> {
			RenderUtil.renderBoundingBox(structureBoundingBox, poseStack, vertexConsumer, camX, camY, camZ, 0.8f);
		});
		RenderUtil.renderLabel(structureBoundingBox, structureBiomeCheckOverview.toString(), minecraft, poseStack, submitNodeCollector, camX, camY, camZ);
		StructureDebugRenderer.renderStructurePieces(structureBoundingBox, structureBiomeCheckOverview.structurePieces(), minecraft, poseStack, submitNodeCollector, camX, camY, camZ);
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
		RenderUtil.renderLabel(structureBoundingBox, structureBiomeCheckOverview.toString(), minecraft, poseStack, bufferSource, camX, camY, camZ);
		StructureDebugRenderer.renderStructurePieces(structureBoundingBox, structureBiomeCheckOverview.structurePieces(), minecraft, poseStack, bufferSource, camX, camY, camZ);
		*///?}
	}

	public static void renderStructureBiomeCheckSample(
		StructureBiomeCheckSample structureBiomeCheckSample,
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
		final int x = structureBiomeCheckSample.x();
		final int y = structureBiomeCheckSample.y();
		final int z = structureBiomeCheckSample.z();
		final boolean result = structureBiomeCheckSample.result();

		final float r = result ? 0.2f:1.0f;
		final float g = result ? 1.0f:0.2f;
		final float b = 0.2f;
		final float a = 0.95f;

		AABB col = new AABB(x, y, z, x + 1, y + 1, z + 1).move(-camX, -camY, -camZ);

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