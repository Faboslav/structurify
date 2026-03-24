package com.faboslav.structurify.common.mixin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(ResourceKeyArgument.class)
public interface ResourceKeyArgumentInvoker
{
	@Invoker("getRegistryKey")
	static <T> ResourceKey<T> structurify$invokegetRegistryKey(CommandContext<CommandSourceStack> context, String argument, ResourceKey<Registry<T>> registryKey, DynamicCommandExceptionType exception) {
		throw new UnsupportedOperationException();
	}
}
