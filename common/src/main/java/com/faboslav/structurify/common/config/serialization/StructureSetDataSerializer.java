package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class StructureSetDataSerializer
{
	public static final String NAME_PROPERTY = "name";
	private static final String SALT_PROPERTY = "salt";
	private static final String FREQUENCY_PROPERTY = "frequency";
	private static final String OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY = "override_global_spacing_and_separation_modifier";
	private static final String SPACING_PROPERTY = "spacing";
	private static final String SEPARATION_PROPERTY = "separation";

	public static void load(JsonObject structureSetJson, StructureSetData structureSetData) {
		var structureSetName = structureSetJson.get(NAME_PROPERTY).getAsString();

		if (structureSetJson.has(SALT_PROPERTY)) {
			var salt = structureSetJson.get(SALT_PROPERTY).getAsInt();

			if ((salt < StructureSetData.MIN_SALT || salt > StructureSetData.MAX_SALT) && salt != structureSetData.getDefaultSalt()) {
				Structurify.getLogger().info("Salt value for structure set {} is currently {}, which is invalid, value will be automatically corrected to {}.", structureSetName, salt, structureSetData.getDefaultSalt());
				salt = structureSetData.getDefaultSalt();
			}

			structureSetData.setSalt(salt);
		}

		if (structureSetJson.has(FREQUENCY_PROPERTY)) {
			var frequency = structureSetJson.get(FREQUENCY_PROPERTY).getAsFloat();

			if (frequency < StructureSetData.MIN_FREQUENCY || frequency > StructureSetData.MAX_FREQUENCY) {
				Structurify.getLogger().info("Frequency value for structure set {} is currently {}, which is invalid, value will be automatically corrected to {}.", structureSetName, frequency, structureSetData.getDefaultFrequency());
				frequency = structureSetData.getDefaultFrequency();
			}

			structureSetData.setFrequency(frequency);
		}

		if (structureSetJson.has(OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY)) {
			var overrideGlobalSpacingAndSeparationModifier = structureSetJson.get(OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY).getAsBoolean();
			structureSetData.setOverrideGlobalSpacingAndSeparationModifier(overrideGlobalSpacingAndSeparationModifier);
		}

		if (structureSetJson.has(SPACING_PROPERTY) && structureSetJson.has(SEPARATION_PROPERTY)) {
			var spacing = structureSetJson.get(SPACING_PROPERTY).getAsInt();
			var separation = structureSetJson.get(SEPARATION_PROPERTY).getAsInt();

			if (separation >= spacing) {
				Structurify.getLogger().info("Separatiton value for structure set {} is currently {}, which is bigger than spacing {}, value will be automatically corrected to {}.", structureSetName, separation, spacing, spacing - 1);
				separation = spacing - 1;
			}

			if (separation < 0) {
				Structurify.getLogger().info("Separatiton value for structure set {} is currently {}, which is lower than minimum value of zero, value will be automatically corrected to 0.", structureSetName, separation);
				separation = 0;
			}

			if (spacing < 1) {
				Structurify.getLogger().info("Spacing value for structure set {} is currently {}, which is lower than minimum value of zero, value will be automatically corrected to 0.", structureSetName, spacing);
				separation = 0;
			}

			structureSetData.setSpacing(spacing);
			structureSetData.setSeparation(separation);
		}
	}

	public static void save(JsonArray structureSetsJson, String structureSetName, StructureSetData structureSetData) {
		var overrideGlobalSpacingAndSeparationModifier = structureSetData.overrideGlobalSpacingAndSeparationModifier();
		var salt = structureSetData.getSalt();
		var frequency = structureSetData.getFrequency();

		JsonObject structureSet = new JsonObject();

		if ((salt < StructureSetData.MIN_SALT || salt > StructureSetData.MAX_SALT) && salt != structureSetData.getDefaultSalt()) {
			Structurify.getLogger().warn("Salt value for structure set {} is currently {}, which is invalid, value will be automatically corrected to {}.", structureSetName, salt, structureSetData.getDefaultSalt());
			salt = structureSetData.getDefaultSalt();
		}

		if (frequency < StructureSetData.MIN_FREQUENCY || frequency > StructureSetData.MAX_FREQUENCY) {
			Structurify.getLogger().warn("Frequency value for structure set {} is currently {}, which is invalid, value will be automatically corrected to {}.", structureSetName, frequency, structureSetData.getDefaultFrequency());
			frequency = structureSetData.getDefaultFrequency();
		}

		if(structureSetData.getDefaultSpacing() != 0 && structureSetData.getDefaultSeparation() != 0) {
			var spacing = structureSetData.getSpacing();
			var separation = structureSetData.getSeparation();

			if (separation >= spacing) {
				Structurify.getLogger().warn("Separatiton value for structure set {} is currently {}, which is bigger than spacing {}, value will be automatically corrected to {}. ", structureSetName, separation, spacing, spacing - 1);
				separation = spacing - 1;
			}

			if (separation < 0) {
				Structurify.getLogger().warn("Separatiton value for structure set {} is currently {}, which is lower than minimum value of zero, value will be automatically corrected to 0.", structureSetName, separation);
				separation = 0;
			}

			if (spacing < 1) {
				Structurify.getLogger().warn("Spacing value for structure set {} is currently {}, which is lower than minimum value of zero, value will be automatically corrected to 0.", structureSetName, spacing);
				separation = 0;
			}

			structureSet.addProperty(SPACING_PROPERTY, spacing);
			structureSet.addProperty(SEPARATION_PROPERTY, separation);
		}

		structureSet.addProperty(NAME_PROPERTY, structureSetName);
		structureSet.addProperty(SALT_PROPERTY, salt);
		structureSet.addProperty(FREQUENCY_PROPERTY, frequency);
		structureSet.addProperty(OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY, overrideGlobalSpacingAndSeparationModifier);
		structureSetsJson.add(structureSet);
	}
}
