package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.mixin.yacl.CategoryTabAccessor;
//? if <= 1.20.1 {
/*import com.faboslav.structurify.common.mixin.yacl.ElementListWidgetExtMixin;
*///?}
import com.faboslav.structurify.common.mixin.yacl.GroupSeparatorEntryAccessor;
import com.faboslav.structurify.common.util.YACLUtil;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StructurifyConfigScreen
{
	public Map<String, StructurifyConfigScreenState> screenStates = new HashMap<>();
	public YACLScreen previousScreen = null;
	public YACLScreen currentScreen = null;

	public Screen generateScreen(Screen parent) {
		var config = Structurify.getConfig();
		var yaclBuilder = YetAnotherConfigLib.createBuilder()
			.title(Component.translatable("gui.structurify.structures_category.title"))
			.save(config::save);

		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());

		StructuresConfigScreen.createStructuresTab(yaclBuilder, config);
		StructureSetsConfigScreen.createStructureSetsTab(yaclBuilder, config);

		var yaclScreen = (YACLScreen) yaclBuilder.build().generateScreen(parent);

		return yaclScreen;
	}

	public void saveScreenState(YACLScreen yaclScreen) {
		var currentTab = yaclScreen.tabNavigationBar.getTabManager().getCurrentTab();

		if (currentTab instanceof YACLScreen.CategoryTab yaclScreenCategoryTab) {
			var categoryTab = ((CategoryTabAccessor) yaclScreenCategoryTab);
			var optionListWidget = YACLUtil.getOptionListWidget(yaclScreenCategoryTab);
			var collapsedGroups = new HashMap<String, Boolean>();

			for (OptionListWidget.Entry entry : optionListWidget.children()) {
				if (entry instanceof OptionListWidget.GroupSeparatorEntry groupSeparatorEntry) {
					GroupSeparatorEntryAccessor yaclGroupSeparatorEntry = (GroupSeparatorEntryAccessor) entry;
					var groupName = yaclGroupSeparatorEntry.getGroup().name().getString();

					if (!collapsedGroups.containsKey(groupName)) {
						collapsedGroups.put(groupName, groupSeparatorEntry.isExpanded());
					}
				}
			}

			this.screenStates.put(yaclScreen.getTitle().getString(), new StructurifyConfigScreenState(
				categoryTab.getSearchField().getValue(),
				//? if >= 1.21.4 {
				optionListWidget.scrollAmount(),
				 //?} else {
				/*optionListWidget.getScrollAmount(),
				*///?}
				collapsedGroups
			));
		}
	}

	public void loadScreenState(YACLScreen yaclScreen) {
		var currentTab = yaclScreen.tabNavigationBar.getTabManager().getCurrentTab();

		if (currentTab instanceof YACLScreen.CategoryTab yaclScreenCategoryTab) {
			var screenState = this.screenStates.get(yaclScreen.getTitle().getString());

			if (screenState != null) {
				var categoryTab = ((CategoryTabAccessor) yaclScreenCategoryTab);
				var optionListWidget = YACLUtil.getOptionListWidget(yaclScreenCategoryTab);
				categoryTab.getSearchField().setValue(screenState.lastSearchText());
				optionListWidget.setScrollAmount(screenState.lastScrollAmount());
				//? if <= 1.20.1 {
				/*((ElementListWidgetExtMixin) optionListWidget).structurify$resetSmoothScrolling();
				*///?}

				for (OptionListWidget.Entry entry : optionListWidget.children()) {
					if (entry instanceof OptionListWidget.GroupSeparatorEntry groupSeparatorEntry) {
						GroupSeparatorEntryAccessor yaclGroupSeparatorEntry = (GroupSeparatorEntryAccessor) entry;
						var groupName = yaclGroupSeparatorEntry.getGroup().name().getString();

						Boolean isGroupCollapsed = screenState.collapsedGroups().getOrDefault(groupName, false);
						groupSeparatorEntry.setExpanded(isGroupCollapsed);
					}
				}
			}
		}
	}

	public void switchScreen(YACLScreen from, YACLScreen to) {
		this.previousScreen = from;
		Minecraft.getInstance().setScreen(to);
		this.currentScreen = to;
	}
}