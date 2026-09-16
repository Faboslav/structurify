package com.faboslav.structurify.common.api;

import net.minecraft.client.gui.components.Button;
import org.jetbrains.annotations.Nullable;

public interface StructurifyCategoryTab
{
	@Nullable
	Button structurify$getToggleGroupsButton();

	void structurify$updateToggleGroupsButton();
}
