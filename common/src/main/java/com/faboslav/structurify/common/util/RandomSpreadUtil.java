package com.faboslav.structurify.common.util;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureSetData;

public final class RandomSpreadUtil
{
	private static StructureSetData getStructureSetData(String structureSetId) {
		if (structureSetId == null || !Structurify.getConfig().getStructureSetData().containsKey(structureSetId)) {
			return null;
		}

		return Structurify.getConfig().getStructureSetData().get(structureSetId);
	}

	public static int getModifiedSalt(String structureSetId, int originalSalt) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);

		if (structureSetData == null) {
			return originalSalt;
		}

		return structureSetData.getSalt();
	}

	public static float getModifiedFrequency(String structureSetId, float originalFrequency) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);

		if (structureSetData == null) {
			return originalFrequency;
		}

		return structureSetData.getFrequency();
	}

	public static int getModifiedSpacing(String structureSetId, int originalSpacing) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);
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
		String structureSetId,
		int spacing,
		int originalSeparation
	) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);
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

	public static int getModifiedStructureWeight(String structureSetId, String structureId, int originalWeight) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);

		if (structureSetData == null) {
			return originalWeight;
		}

		return Math.max(1, structureSetData.getStructureWeights().getOrDefault(structureId, originalWeight));
	}
}
