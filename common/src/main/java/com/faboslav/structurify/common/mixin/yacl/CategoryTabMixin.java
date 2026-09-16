package com.faboslav.structurify.common.mixin.yacl;

import com.faboslav.structurify.common.api.StructurifyCategoryTab;
import com.faboslav.structurify.common.api.StructurifyYACLScreen;
import com.faboslav.structurify.common.util.YACLUtil;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.SearchFieldWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = YACLScreen.CategoryTab.class, remap = false)
public abstract class CategoryTabMixin implements StructurifyCategoryTab
{
	@Shadow
	@Final
	private SearchFieldWidget searchField;

	@Shadow
	@Final
	public Button undoButton;

	@Shadow
	@Final
	public Button saveFinishedButton;

	@Unique
	@Nullable
	private OptionListWidget structurify$optionListWidget = null;

	@Unique
	@Nullable
	private Button structurify$toggleGroupsButton = null;

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	private void structurify$init(YACLScreen screen, ConfigCategory category, ScreenRectangle tabArea, CallbackInfo ci) {
		if (!((StructurifyYACLScreen) screen).structurify$isStructurifyScreen()) {
			return;
		}

		this.structurify$optionListWidget = YACLUtil.getOptionListWidget(this);
		this.searchField.setY(this.searchField.getY() - 22);

		this.structurify$toggleGroupsButton = Button.builder(Component.empty(), button -> this.structurify$toggleGroups())
			.pos(this.saveFinishedButton.getX(), this.undoButton.getY() - 22)
			.size(this.saveFinishedButton.getWidth(), this.saveFinishedButton.getHeight())
			.build();

		this.structurify$updateToggleGroupsButton();
	}

	@Override
	@Nullable
	public Button structurify$getToggleGroupsButton() {
		return this.structurify$toggleGroupsButton;
	}

	@Unique
	private void structurify$toggleGroups() {
		if (this.structurify$optionListWidget == null) {
			return;
		}

		var expand = !this.structurify$hasExpandedGroup();

		for (OptionListWidget.Entry entry : this.structurify$optionListWidget.children()) {
			if (entry instanceof OptionListWidget.GroupSeparatorEntry groupSeparatorEntry) {
				groupSeparatorEntry.setExpanded(expand);
			}
		}

		this.structurify$optionListWidget.setScrollAmount(0);
		this.structurify$updateToggleGroupsButton();
	}

	@Override
	public void structurify$updateToggleGroupsButton() {
		if (this.structurify$toggleGroupsButton == null || this.structurify$optionListWidget == null) {
			return;
		}

		var labelKey = this.structurify$hasExpandedGroup() ? "gui.structurify.label.collapse_all" : "gui.structurify.label.expand_all";
		this.structurify$toggleGroupsButton.setMessage(Component.translatable(labelKey));
		this.structurify$toggleGroupsButton.setTooltip(Tooltip.create(Component.translatable(labelKey + ".tooltip")));
	}

	@Unique
	private boolean structurify$hasExpandedGroup() {
		if (this.structurify$optionListWidget == null) {
			return false;
		}

		for (OptionListWidget.Entry entry : this.structurify$optionListWidget.children()) {
			if (entry instanceof OptionListWidget.GroupSeparatorEntry groupSeparatorEntry && groupSeparatorEntry.isExpanded()) {
				return true;
			}
		}

		return false;
	}
}
