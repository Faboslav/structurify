package com.faboslav.structurify.common.config.util;

public final class StructureSetUtil
{
	public static int correctSpacing(int spacing, int separation) {
		if (spacing <= separation) {
			return separation;
		}

		return spacing;
	}
}
