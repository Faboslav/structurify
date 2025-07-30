package com.faboslav.structurify.common.config.client.gui;

import java.util.Map;

public record StructurifyConfigScreenState(
	String lastSearchText,
	double lastScrollAmount,
	Map<String, Boolean> collapsedGroups
) {
}
