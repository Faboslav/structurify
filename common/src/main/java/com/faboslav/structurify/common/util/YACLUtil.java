package com.faboslav.structurify.common.util;

import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class YACLUtil {
	private YACLUtil() {}

	@Nullable
	public static OptionListWidget getOptionListWidget(Object tab) {
		Object holder = readField(tab, "optionList");
		if (holder == null) return null;

		// Defensive: sometimes it's still null because the tab wasn't opened yet.
		try {
			// Try common accessor names
			for (String methodName : new String[]{"widget", "getWidget", "getType", "getList"}) {
				try {
					Method m = holder.getClass().getMethod(methodName);
					m.setAccessible(true);
					Object result = m.invoke(holder);
					if (result instanceof OptionListWidget olw) return olw;
				} catch (NoSuchMethodException ignored) {}
			}
		} catch (Throwable ignored) {}

		return null;
	}

	private static @Nullable Object readField(Object instance, String name) {
		Class<?> c = instance.getClass();
		while (c != null) {
			try {
				Field f = c.getDeclaredField(name);
				f.setAccessible(true);
				return f.get(instance);
			} catch (NoSuchFieldException e) {
				c = c.getSuperclass();
			} catch (Throwable t) {
				return null;
			}
		}
		return null;
	}
}