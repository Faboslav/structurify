package com.faboslav.structurify.common.debug.renderer;

import com.faboslav.structurify.common.util.RenderUtil;
import com.faboslav.structurify.common.util.StructurePieceUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
//? if >= 1.21.11 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
 *///?}
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import java.util.List;

public class StructureDebugRenderer
{
	public static void renderStructurePieces(
		BoundingBox structureBoundingBox,
		List<StructurePiece> structurePieces,
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
		for (var structurePiece : structurePieces) {
			var structurePieceBoundingBox = structurePiece.getBoundingBox();

			if (structurePieceBoundingBox.equals(structureBoundingBox)) {
				continue;
			}

			//? if >= 1.21.11 {
			submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lines(), (pose, vertexConsumer) -> {
				RenderUtil.renderBoundingBox(structurePieceBoundingBox, poseStack, vertexConsumer, camX, camY, camZ, 0.2f);
			});
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
			*///?}

			String pieceName = StructurePieceUtil.getStructurePieceName(structurePiece);

			RenderUtil.renderLabel(
				structurePieceBoundingBox,
				pieceName,
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
}