package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureData;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

@Mixin(LocateCommand.class)
public class LocateCommandMixin
{
	@WrapMethod(
		method = "locateStructure"
	)
	private static int structurify$executeLocateStructure(
		CommandSourceStack source,
		ResourceOrTagKeyArgument.Result<Structure> predicate,
		Operation<Integer> original
	) throws CommandSyntaxException {
		if (Structurify.getConfig().disableAllStructures) {
			throw new SimpleCommandExceptionType(Component.translatable("command.structurify.locate.exception.all_structures_are_disabled")).create();
		}

		Optional<ResourceKey<Structure>> structureRegistryKey = predicate.unwrap().left();
		Optional<TagKey<Structure>> structureTagKey = predicate.unwrap().right();

		if (structureRegistryKey.isPresent()) {
			String structureId = structureRegistryKey.get().location().toString();

			if (structurify$isStructureDisabled(structureId)) {
				throw new SimpleCommandExceptionType(Component.translatable("command.structurify.locate.structure_is_disabled", structureId)).create();
			}
		} else if (structureTagKey.isPresent()) {
			var structureRegistry = source.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE);

			try {
				structureRegistry.getTag(structureTagKey.get()).ifPresent(tagStructures -> {
					boolean areAllStructuresInTagDisabled = true;

					for (var tagStructure : tagStructures) {
						String tagStructureId = tagStructure.unwrapKey().get().location().toString();

						if (!structurify$isStructureDisabled(tagStructureId)) {
							areAllStructuresInTagDisabled = false;
						}
					}

					if (areAllStructuresInTagDisabled) {
						throw new RuntimeException(new SimpleCommandExceptionType(Component.translatable("command.structurify.locate.structure_is_disabled", "#" + structureTagKey.get().location().toString())).create());
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

		return original.call(source, predicate);
	}

	@Unique
	private static boolean structurify$isStructureDisabled(String structureId) {
		StructureData structureData = Structurify.getConfig().getStructureData().getOrDefault(structureId, null);

		if (structureData == null) {
			return false;
		}

		return structureData.isDisabled();
	}
}
