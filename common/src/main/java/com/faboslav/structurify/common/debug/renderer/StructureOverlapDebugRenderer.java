package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.common.world.level.structure.StructureSectionClaim;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
//? if >= 1.21.11 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
 *///?}
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public final class StructureOverlapDebugRenderer
{
	public static void renderStructureSectionClaim(
		StructureSectionClaim structureSectionClaim,
		Minecraft minecraft,
		PoseStack poseStack,
		//? if >= 1.21.11 {
		SubmitNodeCollector submitNodeCollector,
		//?} else {
		/*MultiBufferSource bufferSource,
		 *///?}
		BlockPos pos,
		double camX,
		double camY,
		double camZ
	) {
		final int x = pos.getX();
		final int y = pos.getY();
		final int z = pos.getZ();

		final float r = 1.0f;
		final float g = 0.2f;
		final float b = 0.2f;
		final float a = 0.95f;

		final int sectionSize = SectionPos.SECTION_SIZE;

		AABB col = new AABB(x, y, z, x + sectionSize, y + sectionSize, z + sectionSize).move(-camX, -camY, -camZ);

		BoundingBox box = new BoundingBox(
			(int)Math.floor(col.minX + camX),
			(int)Math.floor(col.minY + camY),
			(int)Math.floor(col.minZ + camZ),
			(int)Math.ceil(col.maxX + camX) - 1,
			(int)Math.ceil(col.maxY + camY) - 1,
			(int)Math.ceil(col.maxZ + camZ) - 1
		);

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

		RenderUtil.renderLabel(
			box,
			structureSectionClaim.structureId(),
			minecraft,
			poseStack,
			//? if >= 1.21.11 {
			submitNodeCollector,
			//?} else {
			/*bufferSource,
			 *///?}
			camX,
			camY,
			camZ
		);
	}
}