package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.common.world.level.structure.StructureSectionClaim;
//? if < 1.21.11 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public final class StructureOverlapDebugRenderer
{
	public static void renderStructureSectionClaim(
		StructureSectionClaim structureSectionClaim,
		BlockPos pos
		//? if < 1.21.11 {
		/*, Minecraft minecraft,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
		*///?}
	) {
		final int x = pos.getX();
		final int y = pos.getY();
		final int z = pos.getZ();

		final float r = 1.0f;
		final float g = 0.2f;
		final float b = 0.2f;
		final float a = 0.95f;

		final int sectionSize = SectionPos.SECTION_SIZE;

		BoundingBox box = new BoundingBox(
			x,
			y,
			z,
			x + sectionSize - 1,
			y + sectionSize - 1,
			z + sectionSize - 1
		);

		//? if >= 1.21.11 {
		AABB col = new AABB(x, y, z, x + sectionSize, y + sectionSize, z + sectionSize);
		RenderUtil.renderLineBox(col, r, g, b, a);
		RenderUtil.renderLabel(box, structureSectionClaim.structureId());
		//?} else {
		/*AABB col = new AABB(x, y, z, x + sectionSize, y + sectionSize, z + sectionSize).move(-camX, -camY, -camZ);
		RenderUtil.renderLineBox(
			poseStack,
			bufferSource.getBuffer(RenderTypes.lines()),
			col,
			r, g, b, a
		);
		RenderUtil.renderLabel(
			box,
			structureSectionClaim.structureId(),
			minecraft,
			poseStack,
			bufferSource,
			camX,
			camY,
			camZ
		);
		*///?}
	}
}
