package com.faboslav.structurify.common.config.client.gui.widget;

import dev.isxander.yacl3.gui.image.ImageRendererManager;
import dev.isxander.yacl3.gui.image.impl.ResourceTextureImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

//? if >= 1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}

//? if <= 1.21.1 {
/*import net.minecraft.util.FastColor;
 *///?} else {
import net.minecraft.util.ARGB;
//?}

/**
 * Inspired by use in Sounds mod
 *
 * @author IMB11
 * <a href="https://github.com/IMB11/Sounds/blob/main/src/main/java/dev/imb11/sounds/gui/ImageButtonWidget.java"https://github.com/IMB11/Sounds/blob/main/src/main/java/dev/imb11/sounds/gui/ImageButtonWidget.java</a>
 */
public class ImageButtonWidget extends AbstractWidget
{
	float durationHovered = 1f;
	private final CompletableFuture<ResourceTextureImage> image;
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
		this.image = ImageRendererManager.registerOrGetImage(image, () -> ResourceTextureImage.createFactory(image, 0.0F, 0.0F, 1920, 1080, 1920, 1080));
		this.onPress = clickEvent;
	}

	@Override
	//? if >= 1.21.9 {
	public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl)
	//?} else {
	/*public void onClick(double mouseX, double mouseY)
	*///?}
	{
		if (this.onPress != null) {
			this.onPress.accept(this);
		}
	}

	@Override
	protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
		context.enableScissor(getX(), getY(), getX() + width, getY() + height);
		this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

		if (this.isHovered() || this.isFocused()) {
			durationHovered += delta / 2f;
		} else {
			if (durationHovered < 0) {
				durationHovered = 0;
			} else {
				durationHovered -= durationHovered / 4f;
			}
		}

		this.renderImage(image, context, delta);
		this.renderLabel(context);

		context.disableScissor();
	}

	private void renderImage(CompletableFuture<ResourceTextureImage> image, GuiGraphics context, float delta) {
		if (!image.isDone()) {
			return;
		}

		try {
			var contentImage = image.get();
			if (contentImage != null) {
				try {
					Field widthField = contentImage.getClass().getDeclaredField("width");
					widthField.setAccessible(true);
					int width = widthField.getInt(contentImage);

					Field heightField = contentImage.getClass().getDeclaredField("height");
					heightField.setAccessible(true);
					int height = heightField.getInt(contentImage);
					float neededWidth = width * ((float) this.height / height);

					//? if >= 1.21.6 {
					context.pose().pushMatrix();
					//?} else {
					/*context.pose().pushPose();
					*///?}

					contentImage.render(context, getX(), getY(), (int) Math.max(neededWidth, this.width), delta);
					//? if >= 1.21.6 {
					context.pose().popMatrix();
					//?} else {
					/*context.pose().popPose();
					*///?}
					} catch (NoSuchFieldException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			} catch (InterruptedException | ExecutionException ignored) {
			}
	}

	private void renderLabel(GuiGraphics context) {
		float alphaScale = Mth.clampedLerp(0.7f, 0.2f, Mth.clamp(durationHovered - 1f, 0.0f, 1.0f));

		//? if <=1.21.1 {
		/*int greyColor = FastColor.ABGR32.color((int) (alphaScale * 255), 0, 0, 0);
		 *///?} else {
		int greyColor = ARGB.color((int) (alphaScale * 255), 0, 0, 0);
		//?}
		context.fill(getX(), getY(), getX() + width, getY() + height, greyColor);


		var client = Minecraft.getInstance();
		float fontScaling = 1.24f;

		int unscaledTextX = this.getX() + 5;
		int unscaledTextY = this.getY() + this.height - client.font.lineHeight - 5;
		int textX = (int) (unscaledTextX / fontScaling);
		int textY = (int) (unscaledTextY / fontScaling);
		int endX = (int) ((this.getX() + this.width - 5) / fontScaling);
		int endY = (int) ((this.getY() + this.height - 5) / fontScaling);

		context.fill(unscaledTextX - 5, unscaledTextY - 5, unscaledTextX + this.width - 5, unscaledTextY + client.font.lineHeight + 5, 0xAF000000);

		//? if >= 1.21.6 {
		context.pose().pushMatrix();
		context.pose().scale(fontScaling, fontScaling);
		//?} else {
		/*context.pose().pushPose();
		context.pose().scale(fontScaling, fontScaling, 1.0f);
		 *///?}

		renderScrollingString(context, client.font, getMessage(), textX, textY, endX, endY, 0xFFFFFFFF);

		//? if >= 1.21.6 {
		context.pose().popMatrix();
		//?} else {
		/*context.pose().popPose();
		 *///?}

		// Draw border.
		//? if >= 1.21.9 {
		context.submitOutline(getX(), getY(), width, height, 0x0FFFFFFF);
		//?} else {
		/*context.renderOutline(getX(), getY(), width, height, 0x0FFFFFFF);
		 *///?}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput builder) {
		builder.add(NarratedElementType.HINT, this.getMessage());
	}
}
