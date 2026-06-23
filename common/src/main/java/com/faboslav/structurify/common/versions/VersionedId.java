package com.faboslav.structurify.common.versions;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class VersionedId
{
	public static Identifier GetId(ResourceKey<?> resourceKey) {
		return resourceKey/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/;
	}
}
