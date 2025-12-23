package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.common.util.StructurePieceUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import java.util.List;

//? if >= 1.21.11 {
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?} else {
/*import net.minecraft.client.renderer.RenderType;
 *///?}

public class StructureDebugRenderer
{
	public static void renderStructurePieces(
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

			RenderUtil.renderBoundingBox(
				structurePieceBoundingBox,
				poseStack, bufferSource.getBuffer(
					//? if >= 1.21.11 {
					RenderTypes.lines()
					//?} else {
					/*RenderType.lines()
			 		*///?}
				),
				camX,
				camY,
				camZ,
				0.2f
			);
			String pieceName = StructurePieceUtil.getStructurePieceName(structurePiece);
			RenderUtil.renderLabel(structurePieceBoundingBox, pieceName, minecraft, poseStack, bufferSource, camX, camY, camZ);
		}
	}
}
