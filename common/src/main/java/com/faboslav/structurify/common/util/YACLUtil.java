package com.faboslav.structurify.common.util;

import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class YACLUtil
{
	@Nullable
	public static OptionListWidget getOptionListWidget(YACLScreen.CategoryTab tab) {
		Object optionListHolder = getDeclaredField(tab, "optionList");
		if (optionListHolder == null) {
			return null;
		}

		try {
			Class<?> widgetAndType = Class.forName("dev.isxander.yacl3.gui.WidgetAndType");
			if (widgetAndType.isInstance(optionListHolder)) {
				Method m = optionListHolder.getClass().getMethod("getType");
				Object out = m.invoke(optionListHolder);
				return (OptionListWidget) out;
			}
		} catch (ClassNotFoundException ignored) {
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed invoking WidgetAndType#getType()", e);
		}

		try {
			Class<?> listHolder = Class.forName("dev.isxander.yacl3.gui.tab.ListHolderWidget");
			if (listHolder.isInstance(optionListHolder)) {
				Method m = optionListHolder.getClass().getMethod("getList");
				Object out = m.invoke(optionListHolder);
				return (OptionListWidget) out;
			}
		} catch (ClassNotFoundException ignored) {
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed invoking ListHolderWidget#getList()", e);
		}

		return null;
	}

	private static Object getDeclaredField(Object owner, String name) {
		Class<?> c = owner.getClass();
		while (c != null) {
			try {
				Field f = c.getDeclaredField(name);
				f.setAccessible(true);
				return f.get(owner);
			} catch (NoSuchFieldException e) {
				c = c.getSuperclass();
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Failed to read field " + name, e);
			}
		}
		return null;
	}
}