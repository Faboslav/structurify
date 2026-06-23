package com.faboslav.structurify.common.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//? if >= 1.21.11 {
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.network.chat.Component;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
 *///?}
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

//? if >= 1.21.11 {
//?} else if >= 1.21.3 {
/*import net.minecraft.client.renderer.ShapeRenderer;
 *///?} else {
/*import net.minecraft.client.renderer.LevelRenderer;
 *///?}

public final class RenderUtil
{
	public static void renderBoundingBox(
		BoundingBox boundingBox,
		PoseStack poseStack,
		VertexConsumer vertexConsumer,
		double cameraX,
		double cameraY,
		double cameraZ,
		float alpha
	) {
		var aabb = new AABB(
			boundingBox.minX(), boundingBox.minY(), boundingBox.minZ(),
			boundingBox.maxX() + 1, boundingBox.maxY() + 1, boundingBox.maxZ() + 1
		).move(-cameraX, -cameraY, -cameraZ);

		renderLineBox(poseStack, vertexConsumer, aabb, 1.0f, 1.0f, 1.0f, alpha);
	}

	public static void renderLineBox(
		PoseStack poseStack,
		VertexConsumer buffer,
		AABB box,
		float red,
		float green,
		float blue,
		float alpha
	) {
		//? if >= 1.21.11 {
		line(poseStack, buffer, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, red, green, blue, alpha, 1.0f, 0.0f, 0.0f);
		line(poseStack, buffer, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, red, green, blue, alpha, 0.0f, 1.0f, 0.0f);
		line(poseStack, buffer, box.minX, box.minY, box.minZ, box.minX, box.minY, box.maxZ, red, green, blue, alpha, 0.0f, 0.0f, 1.0f);
		line(poseStack, buffer, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, red, green, blue, alpha, 0.0f, 1.0f, 0.0f);
		line(poseStack, buffer, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, red, green, blue, alpha, 0.0f, 0.0f, 1.0f);
		line(poseStack, buffer, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, red, green, blue, alpha, 1.0f, 0.0f, 0.0f);
		line(poseStack, buffer, box.minX, box.maxY, box.minZ, box.minX, box.maxY, box.maxZ, red, green, blue, alpha, 0.0f, 0.0f, 1.0f);
		line(poseStack, buffer, box.minX, box.minY, box.maxZ, box.maxX, box.minY, box.maxZ, red, green, blue, alpha, 1.0f, 0.0f, 0.0f);
		line(poseStack, buffer, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, red, green, blue, alpha, 0.0f, 1.0f, 0.0f);
		line(poseStack, buffer, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha, 0.0f, 0.0f, 1.0f);
		line(poseStack, buffer, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha, 0.0f, 1.0f, 0.0f);
		line(poseStack, buffer, box.minX, box.maxY, box.maxZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha, 1.0f, 0.0f, 0.0f);
		//?} else if >= 1.21.9 {
		/*ShapeRenderer.renderLineBox(poseStack.last(), buffer, box, red, green, blue, alpha);
		 *///?} else if >= 1.21.3 {
		/*ShapeRenderer.renderLineBox(poseStack, buffer, box, red, green, blue, alpha);
		 *///?} else {
		/*LevelRenderer.renderLineBox(poseStack, buffer, box, red, green, blue, alpha);
		 *///?}
	}

	//? if >= 1.21.11 {
	private static void line(
		PoseStack poseStack,
		VertexConsumer buffer,
		double x1,
		double y1,
		double z1,
		double x2,
		double y2,
		double z2,
		float red,
		float green,
		float blue,
		float alpha,
		float normalX,
		float normalY,
		float normalZ
	) {
		PoseStack.Pose pose = poseStack.last();
		buffer.addVertex(pose, (float)x1, (float)y1, (float)z1).setColor(red, green, blue, alpha).setNormal(pose, normalX, normalY, normalZ).setLight(0xF000F0);
		buffer.addVertex(pose, (float)x2, (float)y2, (float)z2).setColor(red, green, blue, alpha).setNormal(pose, normalX, normalY, normalZ).setLight(0xF000F0);
	}
	//?}

	public static void renderLabel(
		BoundingBox box,
		String label,
		Minecraft mc,
		PoseStack poseStack,
		//? if >= 1.21.11 {
		SubmitNodeCollector submitNodeCollector,
		//?} else {
		/*MultiBufferSource buffers,
		 *///?}
		double camX,
		double camY,
		double camZ
	) {
		renderLabel(box, label, mc, poseStack,
			//? if >= 1.21.11 {
			submitNodeCollector,
			//?} else {
			/*buffers,
			 *///?}
			camX, camY, camZ, false
		);
		renderLabel(box, label, mc, poseStack,
			//? if >= 1.21.11 {
			submitNodeCollector,
			//?} else {
			/*buffers,
			 *///?}
			camX, camY, camZ, true
		);
	}

	private static void renderLabel(
		BoundingBox box,
		String label,
		Minecraft mc,
		PoseStack poseStack,
		//? if >= 1.21.11 {
		SubmitNodeCollector submitNodeCollector,
		//?} else {
		/*MultiBufferSource buffers,
		 *///?}
		double camX,
		double camY,
		double camZ,
		boolean inverse
	) {
		double centerX = (box.minX() + box.maxX() + 1) * 0.5;
		double centerZ = (box.minZ() + box.maxZ() + 1) * 0.5;
		double topY = box.maxY() + 1.0;
		double labelY = topY + 0.25;

		poseStack.pushPose();
		poseStack.translate(centerX - camX, labelY - camY, centerZ - camZ);
		poseStack.mulPose(Axis.YP.rotationDegrees(180f - (inverse ? 180f : 0f)));

		float spanX = box.getXSpan();
		float spanZ = box.getZSpan();
		float diagXZ = (float)Math.sqrt(spanX * spanX + spanZ * spanZ);
		float base = 0.03f;
		float dynamic = Math.max(1.0f, diagXZ / 24.0f);
		float scale = Math.max(base, base * dynamic);

		poseStack.scale(-scale, -scale, scale);

		Font font = mc.font;
		String[] lines = label.split("\n");
		int lineHeight = font.lineHeight;
		int totalPixelHeight = lines.length * lineHeight;
		float yStart = -totalPixelHeight;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			int width = font.width(line);

			//? if >= 1.21.11 {
			submitNodeCollector.submitText(
				poseStack,
				-width / 2f,
				yStart + i * lineHeight,
				Component.literal(line).getVisualOrderText(),
				false,
				Font.DisplayMode.NORMAL,
				0xF000F0,
				0xFFFFFFFF,
				0,
				0
			);
			//?} else {
			/*font.drawInBatch(
				line,
				-width / 2f,
				yStart + i * lineHeight,
				0xFFFFFFFF,
				false,
				poseStack.last().pose(),
				buffers,
				Font.DisplayMode.NORMAL,
				0,
				0xF000F0
			);
			*///?}
		}

		poseStack.popPose();
	}
}