package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureBiomeCheckOverview;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureBiomeCheckSample;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

//? if >= 1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?} else {
/*import net.minecraft.client.renderer.RenderType;
 *///?}

public class BiomeCheckDebugRenderer
{
	public static void renderStructureBiomeCheckOverview(
		StructureBiomeCheckOverview structureBiomeCheckOverview,
		Minecraft minecraft,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
	) {
		BoundingBox structureBoundingBox = structureBiomeCheckOverview.structureBoundingBox();
		RenderUtil.renderBoundingBox(
			structureBoundingBox,
			poseStack,
			bufferSource.getBuffer(
				//? if >= 1.21.11 {
				RenderTypes.lines()
				//?} else {
				/*RenderType.lines()
				 *///?}
			),
			camX,
			camY,
			camZ,
			0.8f
		);
		RenderUtil.renderLabel(structureBoundingBox, structureBiomeCheckOverview.toString(), minecraft, poseStack, bufferSource, camX, camY, camZ);

		StructureDebugRenderer.renderStructurePieces(structureBoundingBox, structureBiomeCheckOverview.structurePieces(), minecraft, poseStack, bufferSource, camX, camY, camZ);
	}


	public static void renderStructureBiomeCheckSample(
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

		RenderUtil.renderLineBox(
			poseStack,
			bufferSource.getBuffer(RenderTypes.lines()),
			col,
			r, g, b, a
		);
	}
}
