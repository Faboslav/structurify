package com.faboslav.structurify.common.mixin.yacl;

import com.faboslav.structurify.common.util.YACLUtil;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.SearchFieldWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.components.AbstractWidget;
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

import java.util.function.Consumer;

@Mixin(value = YACLScreen.CategoryTab.class, remap = false)
public abstract class CategoryTabMixin
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
		this.structurify$optionListWidget = YACLUtil.getOptionListWidget(this);
		this.searchField.setY(this.searchField.getY() - 22);

		this.structurify$toggleGroupsButton = Button.builder(Component.empty(), button -> this.structurify$toggleGroups())
			.pos(this.saveFinishedButton.getX(), this.undoButton.getY() - 22)
			.size(this.saveFinishedButton.getWidth(), this.saveFinishedButton.getHeight())
			.build();

		this.structurify$updateToggleGroupsButton();
	}

	@Inject(
		method = "visitChildren",
		at = @At("TAIL")
	)
	private void structurify$visitChildren(Consumer<AbstractWidget> consumer, CallbackInfo ci) {
		if (this.structurify$toggleGroupsButton != null) {
			consumer.accept(this.structurify$toggleGroupsButton);
		}
	}

	@Inject(
		method = "tick",
		at = @At("TAIL")
	)
	private void structurify$tick(CallbackInfo ci) {
		this.structurify$updateToggleGroupsButton();
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

	@Unique
	private void structurify$updateToggleGroupsButton() {
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
