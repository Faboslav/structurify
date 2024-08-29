package com.faboslav.structurify.common.config.client.gui.widget;

import com.faboslav.structurify.common.mixin.AnimatedDynamicTextureImageAccessor;
import dev.isxander.yacl3.gui.image.ImageRendererManager;
import dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL20.*;

public class ImageButtonWidget extends AbstractWidget
{
	float durationHovered = 1f;
	private final CompletableFuture<AnimatedDynamicTextureImage> image;
	private final Consumer<AbstractWidget> onPress;

	public ImageButtonWidget(
		int x,
		int y,
		int width,
		int height,
		Component message,
		ResourceLocation image,
		Consumer<AbstractWidget> clickEvent
	) {
		super(x, y, width, height, message);
		this.image = ImageRendererManager.registerImage(image, AnimatedDynamicTextureImage.createWEBPFromTexture(image));
		this.onPress = clickEvent;
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		if (this.onPress != null) {
			this.onPress.accept(this);
		}
	}

	@Override
	protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
		/*?} else {*/
		context.enableScissor(getX(), getY(), getX() + width, getY() + height);
		this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

		if (this.isHovered || this.isFocused()) {
			durationHovered += delta / 2f;
		} else {
			if (durationHovered < 0) {
				durationHovered = 0;
			} else {
				durationHovered -= durationHovered / 4f;
			}
		}

		// Ease in out lerp.
		float alphaScale = Mth.clampedLerp(0.7f, 0.2f, Mth.clamp(durationHovered - 1f, 0.0f, 1.0f));

		if (image.isDone()) {
			try {
				var contentImage = image.get();
				if (contentImage != null) {

					// Scale the image so that the image height is the same as the button height.
					float neededWidth = ((AnimatedDynamicTextureImageAccessor) contentImage).structurify$getFrameWidth() * ((float) this.height / ((AnimatedDynamicTextureImageAccessor) contentImage).structurify$getFrameHeight());

					// Scale the image to fit within the width and height of the button.
					context.pose().pushPose();
					// gl bilinear scaling.
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
					contentImage.render(context, getX(), getY(), (int) Math.max(neededWidth, this.width), delta);
					context.pose().popPose();
				}
			} catch (InterruptedException | ExecutionException ignored) {
			}
		}

//        context.drawTexture(image, getX(), getY(), this.width, this.height, 0, 0, 1920, 1080, 1920, 1080);

		int greyColor = FastColor.ARGB32.color((int) (alphaScale * 255), 0, 0, 0);
		context.fill(getX(), getY(), getX() + width, getY() + height, greyColor);

		// Draw text.
		var client = Minecraft.getInstance();

		float fontScaling = 1.24f;

		int unscaledTextX = this.getX() + 5;
		int unscaledTextY = this.getY() + this.height - client.font.lineHeight - 5;
		int textX = (int) (unscaledTextX / fontScaling);
		int textY = (int) (unscaledTextY / fontScaling);
		int endX = (int) ((this.getX() + this.width - 5) / fontScaling);
		int endY = (int) ((this.getY() + this.height - 5) / fontScaling);

		context.fill(unscaledTextX - 5, unscaledTextY - 5, unscaledTextX + this.width - 5, unscaledTextY + client.font.lineHeight + 5, 0xAF000000);

		context.pose().pushPose();
		context.pose().scale(fontScaling, fontScaling, 1.0f);

//            context.fill(textX, textY, endX, endY, 0xFFFF2F00);

		/*? >1.20.1 {*/
		/*renderScrollingString(context, client.font, getMessage(), textX, textY, endX, endY, 0xFFFFFF);
		 *//*?} else {*/
		renderScrollingString(context, client.font, getMessage(), textX, textY, endX, endY, 0xFFFFFF);
		/*?}*/

		context.pose().popPose();

		// Draw border.
		context.renderOutline(getX(), getY(), width, height, 0x0FFFFFFF);
		context.disableScissor();
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {
		builder.add(NarratedElementType.HINT, this.getMessage());
	}
}
