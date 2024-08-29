package com.faboslav.structurify.common.mixin;

import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(PackRepository.class)
public interface ResourcePackManagerAccessor
{
	@Accessor
	Set<PackRepository> getSources();
}
