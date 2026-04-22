plugins {
	id("fabric-loom-compat")
	id("multiloader-loader")
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

stonecutter {
	constants["terra"] = rootProject.project(stonecutter.current.project).property("deps.terra").toString() != ""
}

dependencies {
	minecraft("com.mojang:minecraft:${commonMod.mc}")

	if (stonecutter.eval(commonMod.mc, "<=1.21.11")) {
		mappings(loom.layered {
			officialMojangMappings()
			commonMod.depOrNull("parchment")?.let { parchmentVersion ->
				parchment("org.parchmentmc.data:parchment-${commonMod.mc}:$parchmentVersion@zip")
			}
		})
	}

	modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric_loader")}")

	fun addEmbeddedFabricModule(name: String) {
		modApi(fabricApi.module(name, "${commonMod.dep("fabric_api")}+${commonMod.mc}"))
	}

	addEmbeddedFabricModule("fabric-api-base")
	if (stonecutter.eval(commonMod.mc, "<=1.21.8")) {
		addEmbeddedFabricModule("fabric-resource-loader-v0")
	} else {
		addEmbeddedFabricModule("fabric-resource-loader-v1")
	}
	addEmbeddedFabricModule("fabric-rendering-v1")
	addEmbeddedFabricModule("fabric-lifecycle-events-v1")
	addEmbeddedFabricModule("fabric-command-api-v2")
	addEmbeddedFabricModule("fabric-gametest-api-v1")
	//modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric_api")}+${commonMod.mc}")

	// Required dependencies
	modImplementation("dev.isxander:yet-another-config-lib:${commonMod.dep("yacl")}-fabric")
	modImplementation("com.terraformersmc:modmenu:${commonMod.dep("mod_menu")}")

	// Global DataPacks
	commonMod.depOrNull("global_datapacks")?.let { globalDatapacksVersion ->
		modCompileOnly(commonMod.modrinth("datapacks", globalDatapacksVersion)) { isTransitive = false }
	}

	// Global Packs
	commonMod.depOrNull("global_packs")?.let { globalPacksVersion ->
		modCompileOnly(commonMod.modrinth("globalpacks", globalPacksVersion)) { isTransitive = false }
	}

	// Open Loader
	commonMod.depOrNull("open_loader")?.let { openLoaderVersion ->
		if (commonMod.mc == "1.21.1") {
			modImplementation(
				group = "net.darkhax.openloader",
				name = "openloader-fabric-${commonMod.mc}",
				version = openLoaderVersion
			)
		} else {
			modImplementation(
				group = "net.darkhax.openloader",
				name = "OpenLoader-Fabric-${commonMod.mc}",
				version = openLoaderVersion
			)
		}
	}

	// Terra
	/*
	commonMod.depOrNull("terra")?.let { terraVersion ->
		modImplementation("com.dfsek.terra:fabric:${terraVersion}")
	}*/

	// Litostitched
	/*
	commonMod.depOrNull("lithostitched_minecraft")?.let { lithostitchedMcVersion ->
		commonMod.depOrNull("lithostitched")?.let { lithostitchedVersion ->
			modImplementation(commonMod.modrinth("lithostitched", "${lithostitchedVersion}-fabric-${lithostitchedMcVersion}"))
		}
	}*/

	// Yungs api
	commonMod.depOrNull("yungs_api_minecraft")?.let { yungsApiMcVersion ->
		commonMod.depOrNull("yungs_api")?.let { yungsApiVersion ->
			modImplementation("com.yungnickyoung.minecraft.yungsapi:YungsApi:$yungsApiMcVersion-Fabric-$yungsApiVersion")
		}
	}

	// Repurposed structures
	commonMod.depOrNull("repurposed_structures")?.let { repurposedStructuresVersion ->
		commonMod.depOrNull("midnight_lib")?.let { midnightLibVersion ->
			modImplementation("com.telepathicgrunt:RepurposedStructures:${repurposedStructuresVersion}-fabric")
			if (commonMod.mc == "1.21.4" || commonMod.mc == "1.21.5") {
				modImplementation(commonMod.modrinth("midnightlib", "${midnightLibVersion}-fabric"))
			} else {
				modImplementation(commonMod.modrinth("midnightlib", "${midnightLibVersion}-fabric"))
			}
		}
	}
}

loom {
	runs {
		getByName("client") {
			client()
			ideConfigFolder.set("Fabric")
			configName = "Fabric Client"
			ideConfigGenerated(true)
		}
		getByName("server") {
			server()
			ideConfigFolder.set("Fabric")
			configName = "Fabric Server"
			ideConfigGenerated(true)
		}
	}

	if (stonecutter.eval(commonMod.mc, "<=1.21.11")) {
		mixin {
			useLegacyMixinAp = true
			defaultRefmapName = "${mod.id}.refmap.json"
		}
	}
}