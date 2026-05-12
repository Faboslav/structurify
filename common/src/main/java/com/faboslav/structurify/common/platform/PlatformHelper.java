package com.faboslav.structurify.common.platform;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface PlatformHelper
{
	Map<String, Optional<ModIconInfo>> MOD_ICON_INFO_CACHE = new HashMap<>();

	boolean isModLoaded(String modId);

	Optional<ModIconInfo> getModIconInfo(String modId);

	String getModVersion();

	Path getConfigDirectory();
}

