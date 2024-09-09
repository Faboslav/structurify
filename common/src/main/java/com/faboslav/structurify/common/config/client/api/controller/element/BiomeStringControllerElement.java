package com.faboslav.structurify.common.config.client.api.controller.element;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.client.api.controller.BiomeStringController;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownControllerElement;
import dev.isxander.yacl3.gui.image.impl.ResourceTextureImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Related code is based on LibBamboo: Utility library mod with permissions from the author
 *
 * @author Crendgrim
 * <a href="https://github.com/Crendgrim/libbamboo</a>
 */
public final class BiomeStringControllerElement extends AbstractDropdownControllerElement<String, String>
{
	private final BiomeStringController biomeStringController;


	public BiomeStringControllerElement(BiomeStringController control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim);
		this.biomeStringController = control;
	}

	@Override
	protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		var oldDimension = getDimension();
		setDimension(getDimension().withWidth(getDimension().width() - getDecorationPadding()));
		super.drawValueText(graphics, mouseX, mouseY, delta);
		setDimension(oldDimension);

		int imageX = getDimension().xLimit() - getXPadding() - getDecorationPadding() + 4;
		int imageY = getDimension().y() + 4;
		this.renderBiomeImage(this.biomeStringController.option().pendingValue(), graphics, imageX, imageY, delta);
	}

	@Override
	public List<String> computeMatchingValues() {
		return this.biomeStringController.getAllowedValues(this.inputField).stream().filter(this::matchingValue).sorted((s1, s2) -> {
			if (s1.startsWith(this.inputField) && !s2.startsWith(this.inputField)) {
				return -1;
			} else {
				return !s1.startsWith(this.inputField) && s2.startsWith(this.inputField) ? 1:s1.compareTo(s2);
			}
		}).toList();
	}

	@Override
	public String getString(String biome) {
		return LanguageUtil.translateId("string", biome).getString();
	}

	@Override
	protected int getDecorationPadding() {
		return 16;
		// return super.getXPadding();
	}

	@Override
	protected int getDropdownEntryPadding() {
		// return 0;
		return 4;
	}

	@Override
	protected void renderDropdownEntry(GuiGraphics graphics, Dimension<Integer> entryDimension, String value) {
		super.renderDropdownEntry(graphics, entryDimension, value);

		int imageX = entryDimension.xLimit() - 1;
		int imageY = entryDimension.y() + 4;
		this.renderBiomeImage(value, graphics, imageX, imageY, 1);
	}

	@Override
	protected int getControlWidth() {
		return super.getControlWidth() + getDecorationPadding();
	}

	@Override
	protected Component getValueText() {
		if (inputField.isEmpty() || biomeStringController == null) {
			return super.getValueText();
		}

		if (inputFieldFocused) {
			return Component.literal(inputField);
		}

		return LanguageUtil.translateId("string", this.biomeStringController.option().pendingValue());
	}

	private void renderBiomeImage(String biomeName, GuiGraphics graphics, int x, int y, float delta) {
		biomeName = Structurify.makeVanillaId(biomeName).getPath();
		ResourceLocation imageId = Structurify.makeId("textures/gui/config/images/biomes/" + biomeName + ".png");

		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

		try {
			var resource = resourceManager.getResourceOrThrow(imageId);
			resource.source().close();
		} catch (FileNotFoundException e) {
			imageId = Structurify.makeId("textures/gui/config/images/biomes/unknown.png");
		}


		try {
			ResourceTextureImage.createFactory(imageId, 0.0F, 0.0F, 16, 16, 16, 16).prepareImage().completeImage().render(graphics,
				x,
				y,
				11,
				delta
			);
		} catch (Exception ignored) {
		}
	}
}