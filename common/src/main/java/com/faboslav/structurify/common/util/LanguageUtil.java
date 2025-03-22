package com.faboslav.structurify.common.util;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public final class LanguageUtil
{
	private static final String FIRST_LETTER_REGEX = "\\b(.)(.*?)\\b";
	private static final Pattern FIRST_LETTER_PATTERN = Pattern.compile(FIRST_LETTER_REGEX);

	public static MutableComponent translateId(@Nullable String prefix, String id) {
		String langKey = transformToLangKey(prefix, id);
		Language language = Language.getInstance();

		if (!language.has(langKey)) {
			if (prefix == null) {
				langKey = id;
			} else if(id.contains(":")) {
				langKey = id.split(":")[1];
			}

			langKey = langKey.replace("_", " ").replace("/", " ");

			langKey = FIRST_LETTER_PATTERN
				.matcher(langKey)
				.replaceAll(matchResult -> matchResult.group(1).toUpperCase() + matchResult.group(2));
		}

		return Component.translatable(langKey);
	}

	private static String transformToLangKey(@Nullable String prefix, String identifier) {
		if (prefix == null) {
			return identifier.replace(":", ".");
		}

		return prefix + "." + identifier.replace(":", ".");
	}
}
