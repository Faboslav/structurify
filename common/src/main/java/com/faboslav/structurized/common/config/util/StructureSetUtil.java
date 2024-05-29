package com.faboslav.structurized.common.config.util;

public final class StructureSetUtil
{
	public static int correctSpacing(int spacing, int separation) {
		if(spacing <= separation) {
			return separation;
		}

		return spacing;
	}
}
