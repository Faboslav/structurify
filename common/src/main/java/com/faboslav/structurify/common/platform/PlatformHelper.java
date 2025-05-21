package com.faboslav.structurify.common.platform;

import java.nio.file.Path;

public interface PlatformHelper
{
	boolean isModLoaded(String modId);

	String getModVersion();

	Path getConfigDirectory();
}

