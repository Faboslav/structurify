package com.faboslav.structurify.common.mixin;

import net.minecraft.world.level.levelgen.WorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldOptions.class)
public abstract class WorldOptionsMixin
{
	@Unique
	private static final long structurify$gameTestSeed = 1234567890L;

	@Inject(
		method = "randomSeed",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void useGameTestSeed(CallbackInfoReturnable<Long> cir) {
		cir.setReturnValue(structurify$gameTestSeed);
	}
}
