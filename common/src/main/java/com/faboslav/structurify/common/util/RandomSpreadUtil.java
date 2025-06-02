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

	public static int getModifiedSalt(ResourceLocation structureSetIdentifier, int originalSalt) {
		StructureSetData structureSetData = getStructureSetData(structureSetIdentifier);

		if(structureSetData == null) {
			return originalSalt;
		}

		return structureSetData.getSalt();
	}

	public static float getModifiedFrequency(ResourceLocation structureSetIdentifier, float originalFrequency) {
		StructureSetData structureSetData = getStructureSetData(structureSetIdentifier);

		if(structureSetData == null) {
			return originalFrequency;
		}

		return structureSetData.getFrequency();
	}

	public static int getModifiedSpacing(ResourceLocation structureSetIdentifier, int originalSpacing) {
		StructureSetData structureSetData = getStructureSetData(structureSetIdentifier);
		return getModifiedSpacing(structureSetData, originalSpacing);
	}

	public static int getModifiedSpacing(StructureSetData structureSetData, int originalSpacing) {
		int spacing = originalSpacing;

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
		return getModifiedSeparation(structureSetData, spacing, originalSeparation);
	}

	public static int getModifiedSeparation(
		StructureSetData structureSetData,
		int spacing,
		int originalSeparation
	) {
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

	private static int getCorrectedModifiedSeparationValue(int spacing, int separation) {
		separation = Math.max(0, separation);

		if (separation >= spacing) {
			separation = spacing - 1;
		}

		return separation;
	}
}
