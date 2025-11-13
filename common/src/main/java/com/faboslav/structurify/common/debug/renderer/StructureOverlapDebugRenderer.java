package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.world.level.structure.StructureSectionClaim;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
		MultiBufferSource bufferSource,
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

		AABB col = new AABB(
			x,
			y,
			z,
			x + sectionSize,
			y + sectionSize,
			z + sectionSize
		).move(-camX, -camY, -camZ);

		BoundingBox box = new BoundingBox(
			(int) Math.floor(col.minX + camX),
			(int) Math.floor(col.minY + camY),
			(int) Math.floor(col.minZ + camZ),
			(int) Math.ceil(col.maxX + camX) - 1,
			(int) Math.ceil(col.maxY + camY) - 1,
			(int) Math.ceil(col.maxZ + camZ) - 1
		);

		RenderUtil.renderLineBox(
			poseStack,
			bufferSource.getBuffer(RenderType.lines()),
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
	}
}