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

		if (Structurify.getConfig().enableGlobalSpacingAndSeparationModifier) {
			if (structureSetData != null && structureSetData.overrideGlobalSpacingAndSeparationModifier()) {
				spacing = structureSetData.getSpacing();
			} else {
				spacing = (int) (spacing * Structurify.getConfig().globalSpacingAndSeparationModifier);
			}
		} else if (structureSetData != null) {
			spacing = structureSetData.getSpacing();
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

		if (Structurify.getConfig().enableGlobalSpacingAndSeparationModifier) {
			if (structureSetData != null && structureSetData.overrideGlobalSpacingAndSeparationModifier()) {
				separation = structureSetData.getSeparation();
			} else {
				separation = (int) (separation * Structurify.getConfig().globalSpacingAndSeparationModifier);
			}
		} else if (structureSetData != null) {
			separation = structureSetData.getSeparation();
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
