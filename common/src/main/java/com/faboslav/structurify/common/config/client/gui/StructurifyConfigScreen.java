package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.client.gui.widget.DynamicGridWidget;
import com.faboslav.structurify.common.config.client.gui.widget.ImageButtonWidget;
import com.faboslav.structurify.common.mixin.yacl.CategoryTabAccessor;
import dev.isxander.yacl3.gui.YACLScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class StructurifyConfigScreen extends Screen
{
	private final Screen parent;

	private final StructureSetsConfigScreen structureSetsConfigScreen = new StructureSetsConfigScreen();

	@Nullable
	public YACLScreen structuresScreen = null;

	@Nullable
	public Map<String, YACLScreen> structureScreens = new HashMap<>();

	public Map<String, StructurifyConfigScreenState> screenStates = new HashMap<>();

	public StructurifyConfigScreen(@Nullable Screen parent) {
		super(Component.translatable("structurify"));
		this.parent = parent;
	}

	public StructureSetsConfigScreen getStructureSetsScreen() {
		return this.structureSetsConfigScreen;
	}

	@Override
	public void onClose() {
		assert this.minecraft != null;
		this.minecraft.setScreen(this.parent);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		/*? if <1.20.2 {*/
		super.renderBackground(context);
		/*?} else {*/
		/*super.renderBackground(context, mouseX, mouseY, delta);
		 *//*?}*/
		super.render(context, mouseX, mouseY, delta);

		assert this.minecraft != null;
		context.drawCenteredString(this.minecraft.font, Component.translatable("gui.structurify.title"), this.width / 2, 10, 0xFFFFFF);
	}

	@Override
	protected void init() {
		super.init();

		int fontHeight = this.font.lineHeight;
		DynamicGridWidget grid = new DynamicGridWidget(10, 10 + fontHeight + 10, width - 13, height - 20 - fontHeight - 10 - 20);

		grid.setPadding(3);

		grid.addChild(new ImageButtonWidget(0, 0, 0, 0, Component.translatable("gui.structurify.structures_category.title"), Structurify.makeId("textures/gui/config/images/buttons/structures.webp"), btn -> {
			if (this.structuresScreen == null) {
				this.structuresScreen = StructuresConfigScreen.createConfigGui(Structurify.getConfig(), this);
			}

			this.minecraft.setScreen(this.structuresScreen);
			this.loadScreenState(this.structuresScreen);
		}), 2, 1);

		grid.addChild(new ImageButtonWidget(0, 0, 0, 0, Component.translatable("gui.structurify.structure_sets_category.title"), Structurify.makeId("textures/gui/config/images/buttons/structure_sets.webp"), btn -> {
			YACLScreen structureSetsScreen = this.structureSetsConfigScreen.getStructureSetsScreen();

			if (structureSetsScreen == null) {
				this.structureSetsConfigScreen.createStructureSetsScreen(Structurify.getConfig(), this);
				structureSetsScreen = this.structureSetsConfigScreen.getStructureSetsScreen();
			}

			this.minecraft.setScreen(structureSetsScreen);
			this.loadScreenState(structureSetsScreen);
		}), 2, 1);

		grid.calculateLayout();
		grid.visitWidgets(this::addRenderableWidget);

		int kofiButtonWidth = 135;
		int discordButtonWidth = 135;
		int discordAndKoFiButtonsWidth = kofiButtonWidth + discordButtonWidth + 30; // button widths + left margin of Ko-Fi button + right margin of Discord button
		int doneButtonWidth = this.width - discordAndKoFiButtonsWidth;
		var buttonWidget = Button.builder(CommonComponents.GUI_DONE, (btn) -> this.minecraft.setScreen(this.parent)).bounds(this.width / 2 - doneButtonWidth / 2, this.height - 30, doneButtonWidth, 20).build();
		var donateButton = Button.builder(Component.literal("Buy Me a Coffee").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD), (btn) -> Util.getPlatform().openUri("https://ko-fi.com/faboslav")).bounds(10, this.height - 30, kofiButtonWidth, 20).build();
		var discordButton = Button.builder(Component.literal("Join Our Discord").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD), (btn) -> Util.getPlatform().openUri("https://discord.gg/QGwFvvMQCn")).bounds(this.width - discordButtonWidth - 10, this.height - 30, discordButtonWidth, 20).build();

		this.addRenderableWidget(buttonWidget);
		this.addRenderableWidget(donateButton);
		this.addRenderableWidget(discordButton);
	}

	public void saveScreenState(YACLScreen yaclScreen) {
		var currentTab = yaclScreen.tabNavigationBar.getTabManager().getCurrentTab();

		if(currentTab instanceof YACLScreen.CategoryTab yaclCategoryTab) {
			var categoryTab = ((CategoryTabAccessor) yaclCategoryTab);
			var optionListWidget = categoryTab.getOptionList().getList();

			this.screenStates.put(yaclScreen.getTitle().getString(), new StructurifyConfigScreenState(
				categoryTab.getSearchField().getValue(),
				//? >= 1.21.4 {
				/*optionListWidget.scrollAmount()
				*///?} else {
				optionListWidget.getScrollAmount()
				//?}
			));
		}
	}

	public void loadScreenState(YACLScreen yaclScreen) {
		var currentTab = yaclScreen.tabNavigationBar.getTabManager().getCurrentTab();

		if(currentTab instanceof YACLScreen.CategoryTab categoryTab) {
			var screenState = this.screenStates.get(yaclScreen.getTitle().getString());

			if(screenState != null) {
				var yaclScreenCategoryTab = ((CategoryTabAccessor) categoryTab);
				yaclScreenCategoryTab.getSearchField().setValue(screenState.lastSearchText());
				yaclScreenCategoryTab.getOptionList().getList().setScrollAmount(screenState.lastScrollAmount());
			}
		}
	}
}