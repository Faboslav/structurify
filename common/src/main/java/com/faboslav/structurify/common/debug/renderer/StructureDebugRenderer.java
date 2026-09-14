package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.common.util.StructurePieceUtil;
//? if < 1.21.11 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
*///?}
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import java.util.List;

public class StructureDebugRenderer
{
	public static void renderStructurePieces(
		BoundingBox structureBoundingBox,
		List<StructurePiece> structurePieces
		//? if < 1.21.11 {
		/*, Minecraft minecraft,
		PoseStack poseStack,
		MultiBufferSource bufferSource,
		double camX,
		double camY,
		double camZ
		*///?}
	) {
		for (var structurePiece : structurePieces) {
			var structurePieceBoundingBox = structurePiece.getBoundingBox();

			if (structurePieceBoundingBox.equals(structureBoundingBox)) {
				continue;
			}

			String pieceName = StructurePieceUtil.getStructurePieceName(structurePiece);

			//? if >= 1.21.11 {
			RenderUtil.renderBoundingBox(structurePieceBoundingBox, 0.2f);
			RenderUtil.renderLabel(structurePieceBoundingBox, pieceName);
			//?} else {
			/*RenderUtil.renderBoundingBox(
				structurePieceBoundingBox,
				poseStack,
				bufferSource.getBuffer(RenderTypes.lines()),
				camX,
				camY,
				camZ,
				0.2f
			);
			RenderUtil.renderLabel(structurePieceBoundingBox, pieceName, minecraft, poseStack, bufferSource, camX, camY, camZ);
			*///?}
		}
	}
}
