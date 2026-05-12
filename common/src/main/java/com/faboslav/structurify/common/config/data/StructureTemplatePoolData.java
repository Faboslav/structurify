package com.faboslav.structurify.common.config.data;

import java.util.HashMap;
import java.util.Map;

public class StructureTemplatePoolData
{
	public static final int MIN_STRUCTURE_TEMPLATE_POOL_ELEMENT_WEIGHT = 0;
	public static final int MAX_STRUCTURE_TEMPLATE_POOL_ELEMENT_WEIGHT = 150;

	private final Map<String, Integer> defaultStructureTemplatePoolElementWeights;
	private final Map<String, Integer> structureTemplatePoolElementWeights;

	public StructureTemplatePoolData(Map<String, Integer> structureTemplatePoolElementsWeights) {
		this.defaultStructureTemplatePoolElementWeights = new HashMap<>(structureTemplatePoolElementsWeights);
		this.structureTemplatePoolElementWeights = new HashMap<>(structureTemplatePoolElementsWeights);
	}

	public Map<String, Integer> getDefaultStructureTemplatePoolElementWeights() {
		return defaultStructureTemplatePoolElementWeights;
	}

	public Map<String, Integer> getStructureTemplatePoolElementWeights() {
		return structureTemplatePoolElementWeights;
	}

	public boolean isUsingDefaultValues() {
		return this.structureTemplatePoolElementWeights.equals(this.defaultStructureTemplatePoolElementWeights);
	}
}
