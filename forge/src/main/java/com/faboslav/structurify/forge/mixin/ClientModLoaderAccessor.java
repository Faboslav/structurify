package com.faboslav.structurify.forge.mixin;

import net.minecraft.resource.ResourcePackProvider;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(net.minecraftforge.client.loading.ClientModLoader.class)
public interface ClientModLoaderAccessor {
	@Invoker
	static ResourcePackProvider callBuildPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks) {
		throw new UnsupportedOperationException();
	}
}
