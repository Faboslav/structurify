package com.faboslav.structurify.common.mixin;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(LocateCommand.class)
public interface LocateCommandInvoker
{
	@Accessor("MAX_STRUCTURE_SEARCH_RADIUS")
	static int structurify$getMaxStructureSearchRadius() {
		throw new AssertionError();
	}

	@Accessor("ERROR_STRUCTURE_NOT_FOUND")
	static DynamicCommandExceptionType structurify$getStructureNotFoundError() {
		throw new AssertionError();
	}

	@Accessor("ERROR_STRUCTURE_INVALID")
	static DynamicCommandExceptionType structurify$getStructureInvalidError() {
		throw new AssertionError();
	}

	@Invoker("getHolders")
	static Optional<? extends HolderSet.ListBacked<Structure>> structurify$invokeGetHolders(ResourceOrTagKeyArgument.Result<Structure> structure, Registry<Structure> structureRegistry) {
		throw new UnsupportedOperationException();
	}
}
