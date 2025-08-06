plugins {
	id("fabric-loom")
	`multiloader-loader`
	kotlin("jvm") version "2.2.0"
	id("com.google.devtools.ksp") version "2.2.0-2.0.2"
	id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.14"
}

stonecutter {
	const("terra", rootProject.project(stonecutter.current.project).property("deps.terra").toString() != "")
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
	commonMod.depOrNull("terra")?.let { terraVersion ->
		modImplementation("com.dfsek.terra:fabric:${terraVersion}")
	}

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