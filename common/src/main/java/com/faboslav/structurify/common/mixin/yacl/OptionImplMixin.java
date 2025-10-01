package com.faboslav.structurify.common.mixin.yacl;

import com.faboslav.structurify.common.api.StructurifyOption;
import dev.isxander.yacl3.impl.OptionImpl;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.*;


@Mixin(value = OptionImpl.class, remap = false)
public abstract class OptionImplMixin implements StructurifyOption
{
	@Mutable
	@Final
	@Shadow
	private Component name;

	@Unique
	public void structurify$setName(Component name) {
		this.name = name;
	}
}
