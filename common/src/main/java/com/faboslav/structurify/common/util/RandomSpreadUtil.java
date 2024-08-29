package com.faboslav.structurify.common.util;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureSetData;
import net.minecraft.resources.ResourceLocation;

public final class RandomSpreadUtil
{
	private static StructureSetData getStructureSetData(ResourceLocation structureSetIdentifier) {
		if (structureSetIdentifier == null || !Structurify.getConfig().getStructureSetData().containsKey(structureSetIdentifier.toString())) {
			return null;
		}

		return Structurify.getConfig().getStructureSetData().get(structureSetIdentifier.toString());
	}

	public static int getModifiedSpacing(ResourceLocation structureSetIdentifier, int originalSpacing) {
		int spacing = originalSpacing;
		StructureSetData structureSetData = getStructureSetData(structureSetIdentifier);

		if (structureSetData != null && !structureSetData.isUsingDefaultSpacing()) {
			spacing = structureSetData.getSpacing();
		}

		if (Structurify.getConfig().enableGlobalSpacingAndSeparationModifier) {
			spacing = (int) (spacing * Structurify.getConfig().globalSpacingAndSeparationModifier);
		}

		return getCorrectedModifiedSpacingValue(spacing);
	}

	public static int getModifiedSeparation(
		ResourceLocation structureSetIdentifier,
		int spacing,
		int originalSeparation
	) {
		StructureSetData structureSetData = getStructureSetData(structureSetIdentifier);
		int separation = originalSeparation;

		if (structureSetData != null && !structureSetData.isUsingDefaultSpacing()) {
			separation = structureSetData.getSeparation();
		}

		if (Structurify.getConfig().enableGlobalSpacingAndSeparationModifier) {
			separation = (int) (separation * Structurify.getConfig().globalSpacingAndSeparationModifier);
		}

		return getCorrectedModifiedSeparationValue(spacing, separation);
	}

	private static int getCorrectedModifiedSpacingValue(int spacingValue) {
		return Math.max(1, spacingValue);
	}

	public static int getCorrectedModifiedSeparationValue(int spacing, int separation) {
		separation = Math.max(0, separation);

		if (separation >= spacing) {
			separation = spacing - 1;
		}

		return separation;
	}
}
