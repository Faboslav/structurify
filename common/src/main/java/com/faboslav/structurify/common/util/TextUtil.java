package com.faboslav.structurify.common.util;


import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextUtil
{
	public static Component createTextWithPrefix(MutableComponent prefix, String translationKey) {
		return prefix.copy().append("\n\n").append(Component.translatable(translationKey));
	}

	public static Component createTextWithPrefix(MutableComponent prefix, MutableComponent text) {
		return prefix.copy().append("\n\n").append(text);
	}
}
