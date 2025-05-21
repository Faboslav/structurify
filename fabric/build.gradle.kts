plugins {
	id("fabric-loom")
	`multiloader-loader`
	id("dev.kikugie.j52j") version "2.0"
}

dependencies {
	minecraft("com.mojang:minecraft:${commonMod.mc}")
	mappings(loom.layered {
		officialMojangMappings()
		commonMod.depOrNull("parchment")?.let { parchmentVersion ->
			parchment("org.parchmentmc.data:parchment-${commonMod.mc}:$parchmentVersion@zip")
		}
	})

	modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric_loader")}")
	modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric_api")}+${commonMod.mc}")

	// Required dependencies
	modImplementation("dev.isxander:yet-another-config-lib:${commonMod.dep("yacl")}-fabric")
	modImplementation("com.terraformersmc:modmenu:${commonMod.dep("mod_menu")}")


	// Global DataPacks
	commonMod.depOrNull("global_datapacks")?.let { globalDatapacksVersion ->
		modCompileOnly(commonMod.modrinth("datapacks", globalDatapacksVersion)) { isTransitive = false }
	}

	// Global Packs
	commonMod.depOrNull("global_packs")?.let { globalPacksVersion ->
		if (commonMod.mc == "1.20.1") {
			modCompileOnly(commonMod.modrinth("globalpacks", "${globalPacksVersion}_fabric")) { isTransitive = false }
		} else {
			modCompileOnly(commonMod.modrinth("globalpacks", globalPacksVersion)) { isTransitive = false }
		}
	}

	// Open Loader
	commonMod.depOrNull("open_loader")?.let { openLoaderVersion ->
		if (commonMod.mc == "1.21.1") {
			implementation(
				group = "net.darkhax.openloader",
				name = "openloader-fabric-${commonMod.mc}",
				version = openLoaderVersion
			)
		} else {
			implementation(
				group = "net.darkhax.openloader",
				name = "OpenLoader-Fabric-${commonMod.mc}",
				version = openLoaderVersion
			)
		}
	}

	commonMod.depOrNull("repurposed_structures")?.let { repurposedStructuresVersion ->
		commonMod.depOrNull("midnight_lib")?.let { midnightLibVersion ->
			modImplementation(
				commonMod.modrinth(
					"repurposed-structures-fabric",
					"${repurposedStructuresVersion}-fabric"
				)
			) { isTransitive = false }
			modImplementation(commonMod.modrinth("midnightlib", "${midnightLibVersion}-fabric")) {
				isTransitive = false
			}

			modCompileOnly(modRuntimeOnly(group = "com.electronwill.night-config", name = "core", version = "3.6.5"))
			modCompileOnly(modRuntimeOnly(group = "com.electronwill.night-config", name = "toml", version = "3.6.5"))
		}
	}
}

loom {
	accessWidenerPath = common.project.file("../../src/main/resources/${mod.id}.accesswidener")
	//accessWidenerPath = project(":common:${stonecutter.current.project}").loom.accessWidenerPath

	runs {
		getByName("client") {
			client()
			configName = "Fabric Client"
			ideConfigGenerated(true)
		}
		getByName("server") {
			server()
			configName = "Fabric Server"
			ideConfigGenerated(true)
		}
	}

	mixin {
		defaultRefmapName = "${mod.id}.refmap.json"
	}
}