val IS_CI = System.getenv("CI") == "true"

plugins {
	`multiloader-loader`
	id("net.neoforged.moddev")
	id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.22"
}

neoForge {
	enable {
		version = commonMod.dep("neoforge")
	}
}

dependencies {
	// Required dependencies
	implementation("dev.isxander:yet-another-config-lib:${commonMod.dep("yacl")}-neoforge")

	// Global Packs
	commonMod.depOrNull("global_packs")?.let { globalPacksVersion ->
		implementation(commonMod.modrinth("globalpacks", globalPacksVersion)) { isTransitive = false }
	}

	// Open Loader
	commonMod.depOrNull("open_loader")?.let { openLoaderVersion ->
		if (commonMod.mc == "1.21.1") {
			implementation(
				group = "net.darkhax.openloader",
				name = "openloader-neoforge-${commonMod.mc}",
				version = openLoaderVersion
			)
		} else {
			implementation(
				group = "net.darkhax.openloader",
				name = "OpenLoader-NeoForge-${commonMod.mc}",
				version = openLoaderVersion
			)
		}
	}

	// Litostitched
	commonMod.depOrNull("lithostitched_minecraft")?.let { lithostitchedMcVersion ->
		commonMod.depOrNull("lithostitched")?.let { lithostitchedVersion ->
			implementation(fletchingTable.modrinth("lithostitched", minecraft = commonMod.mc, loaders = "neoforge"))
		}
	}

	// Yungs api
	commonMod.depOrNull("yungs_api_minecraft")?.let { lithostitchedMcVersion ->
		commonMod.depOrNull("yungs_api")?.let { lithostitchedVersion ->
			implementation(fletchingTable.modrinth("yungs-api", minecraft = commonMod.mc, loaders = "neoforge"))
		}
	}

	// Repurposed Structures
	if (commonMod.mc >= "1.21.1") {
		commonMod.depOrNull("repurposed_structures")?.let { repurposedStructuresVersion ->
			val repurposedStructuresMinecraftVersion = when (commonMod.mc) {
				"26.1.2" -> "26.1"
				"1.21.10" -> "1.21.11"
				else -> commonMod.mc
			}
			implementation(fletchingTable.modrinth("repurposed-structures-forge", minecraft = repurposedStructuresMinecraftVersion, loaders = "neoforge"))
			implementation(fletchingTable.modrinth("midnightlib", minecraft = repurposedStructuresMinecraftVersion, loaders = "neoforge"))
		}
	}

	// Cataclysm
	try {
		implementation(fletchingTable.modrinth("l_enders-cataclysm", minecraft = commonMod.mc, loaders = "neoforge"))
		stonecutter.constants["cataclysm"] = true
	} catch (e: Throwable) {
		stonecutter.constants["cataclysm"] = false
	}

	if(!IS_CI) {
		if (commonMod.mc == "1.21.1") {
			val modrinthBundles = listOf(
				//"biolith",
				//"no-mans-land",
				//"yungs-better-end-island",
				//"dungeons-and-taverns",
				//"dungeons-and-taverns-ancient-city-overhaul",
				"abridged",
				"streams-reflowing",
			)

			for (bundle in modrinthBundles) {
				fletchingTable.modrinthBundle(bundle, commonMod.mc, "neoforge") {
					recursive = true
					include("required", "optional", "embedded")
				}.forEach(::implementation)
			}
		}
	}
}

neoForge {
	runs {
		register("client") {
			client()
			ideFolderName = "NeoForge"
			ideName = "NeoForge Client (${project.path})"
		}
		register("server") {
			server()
			ideFolderName = "NeoForge"
			ideName = "NeoForge Server (${project.path})"
		}
	}

	parchment {
		commonMod.depOrNull("parchment")?.let {
			mappingsVersion = it
			minecraftVersion = commonMod.mc
		}
	}

	mods {
		register(commonMod.id) {
			sourceSet(sourceSets.main.get())
		}
	}
}

sourceSets.main {
	resources.srcDir("src/generated/resources")
}

if (stonecutter.current.isActive) tasks.register("buildActive") {
	dependsOn("build")
}