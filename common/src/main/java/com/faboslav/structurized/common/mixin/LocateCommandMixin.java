package com.faboslav.structurized.common.mixin;

import com.faboslav.structurized.common.Structurized;
import com.faboslav.structurized.common.config.data.StructureData;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LocateCommand.class)
public class LocateCommandMixin
{
	@Inject(method = "executeLocateStructure", at = @At(value = "HEAD"))
	private static void structurized$executeLocateStructure(
		ServerCommandSource source,
		RegistryPredicateArgumentType.RegistryPredicate<Structure> predicate,
		CallbackInfoReturnable<Integer> cir
	) throws CommandSyntaxException {
		if(Structurized.getConfig().disableAllStructures) {
			throw new SimpleCommandExceptionType(Text.translatable("structurized.exception.all_structures_are_disabled")).create();
		}

		Optional<RegistryKey<Structure>> structureRegistryKey = predicate.getKey().left();
		Optional<TagKey<Structure>> structureTagKey = predicate.getKey().right();

		if (structureRegistryKey.isPresent()) {
			String structureId = structureRegistryKey.get().getValue().toString();

			if (structurized$isStructureDisabled(structureId)) {
				throw new SimpleCommandExceptionType(Text.translatable("structurized.exception.structure_is_disabled", structureId)).create();
			}
		} else if(structureTagKey.isPresent()) {
			var structureRegistry = source.getWorld().getRegistryManager().get(RegistryKeys.STRUCTURE);

			try {
				structureRegistry.getEntryList(structureTagKey.get()).ifPresent(tagStructures -> {
					for (var tagStructure : tagStructures) {
						String tagStructureId = tagStructure.getKey().get().getValue().toString();

						if (structurized$isStructureDisabled(tagStructureId)) {
							throw new RuntimeException(new SimpleCommandExceptionType(Text.translatable("structurized.exception.structure_is_disabled", "#" + structureTagKey.get().id().toString())).create());
						}
					}
				});
			} catch (RuntimeException e) {
				if (e.getCause() instanceof CommandSyntaxException) {
					throw (CommandSyntaxException) e.getCause();
				} else {
					throw e;
				}
			}
		}
	}

	@Unique
	private static boolean structurized$isStructureDisabled(String structureId) {
		StructureData structureData = Structurized.getConfig().getStructureData().getOrDefault(structureId, null);

		if(structureData == null) {
			return false;
		}

		return structureData.isDisabled();
	}
}
