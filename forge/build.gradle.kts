val IS_CI = System.getenv("CI") == "true"

plugins {
	`multiloader-loader`
	id("net.neoforged.moddev.legacyforge")
	id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.22"
}

mixin {
	add(sourceSets.main.get(), "${mod.id}.refmap.json")

	config("${mod.id}-common.mixins.json")
	config("${mod.id}-forge.mixins.json")
}

legacyForge {
	enable {
		forgeVersion = "${commonMod.mc}-${commonMod.dep("forge")}"
	}
}

dependencies {
	compileOnly("org.jetbrains:annotations:24.1.0")
	annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")

	"io.github.llamalad7:mixinextras-common:0.4.1".let {
		compileOnly(it)
		annotationProcessor(it)
	}

	"io.github.llamalad7:mixinextras-forge:0.4.1".let {
		implementation(it)
		jarJar(it)
	}

	// Required dependencies
	modImplementation("dev.isxander:yet-another-config-lib:${commonMod.dep("yacl")}-forge")

	// Global Packs
	commonMod.depOrNull("global_packs")?.let { globalPacksVersion ->
		modImplementation(commonMod.modrinth("globalpacks", globalPacksVersion)) { isTransitive = false }
	}

	// Open Loader
	commonMod.depOrNull("open_loader")?.let { openLoaderVersion ->
		modImplementation(
			group = "net.darkhax.openloader",
			name = "OpenLoader-Forge-${commonMod.mc}",
			version = openLoaderVersion
		) { isTransitive = false }
	}

	// Litostitched
	try {
		modImplementation(fletchingTable.modrinth("lithostitched", minecraft = commonMod.mc, loaders = "forge"))
		stonecutter.constants["lithostitched"] = true
	} catch (e: Throwable) {
		stonecutter.constants["lithostitched"] = false
	}

	// YUNG's API
	try {
		modImplementation(fletchingTable.modrinth("yungs-api", minecraft = commonMod.mc, loaders = "forge"))
		stonecutter.constants["yungs_api"] = true
	} catch (e: Throwable) {
		stonecutter.constants["yungs_api"] = false
	}

	// Repurposed Structures
	try {
		val repurposedStructuresMinecraftVersion = when (commonMod.mc) {
			"26.1.2" -> "26.1"
			"1.21.10" -> "1.21.11"
			else -> commonMod.mc
		}
		modImplementation(fletchingTable.modrinth("repurposed-structures-forge", minecraft = repurposedStructuresMinecraftVersion, loaders = "forge"))
		modImplementation(fletchingTable.modrinth("midnightlib", minecraft = repurposedStructuresMinecraftVersion, loaders = "forge"))
		stonecutter.constants["repurposed_structures"] = true
	} catch (e: Throwable) {
		stonecutter.constants["repurposed_structures"] = false
	}

	// Structure Gel Api
	try {
		modImplementation(fletchingTable.modrinth("structure-gel-api", minecraft = commonMod.mc, loaders = "forge"))
		stonecutter.constants["structure_gel_api"] = true
	} catch (e: Throwable) {
		stonecutter.constants["structure_gel_api"] = false
	}

	// Cataclysm
	try {
		modImplementation(fletchingTable.modrinth("l_enders-cataclysm", minecraft = commonMod.mc, loaders = "forge"))
		stonecutter.constants["cataclysm"] = true
	} catch (e: Throwable) {
		stonecutter.constants["cataclysm"] = false
	}

	if(!IS_CI) {
		val modrinthBundles = listOf(
			"better-modlist",
			"ct-overhaul-village",
			"fantasy-structures-(by-berezka)",
			"alexs-caves",
			"aquamirae",
			"fossils-and-archeology-revival",
			"dungeon-now-loading",
			"explorations",
			"the-graveyard-forge",
			"goblins-tyranny",
			"dungeons-enhanced",
			"legendary-monsters",
			"yungs-better-end-island"
		)

		for (bundle in modrinthBundles) {
			fletchingTable.modrinthBundle(bundle, commonMod.mc, "forge") {
				recursive = true
				include("required", "optional", "embedded")
			}.forEach(::modImplementation)
		}
		//modImplementation(fletchingTable.modrinth("fungal-infectionspore", commonMod.mc, "forge"))
		//modImplementation(fletchingTable.modrinth("dungeons-enhanced", commonMod.mc, "forge"))
		//modImplementation(fletchingTable.modrinth("legendary-monsters", commonMod.mc, "forge"))
		/*
		val endersCataclysmWithDeps: List<Dependency> = fletchingTable.modrinthBundle("l_enders-cataclysm", commonMod.mc, "forge") {
			recursive = true
			include("required", "optional", "embedded")
		}
		for (mod in endersCataclysmWithDeps) modImplementation(mod)*/

		// For debugging
		// modImplementation(modrinth("blue-skies", "1.3.31")) { transitive = false }
	}
}

legacyForge {
	runs {
		register("client") {
			client()
			ideFolderName = "Forge"
			ideName = "Forge Client (${project.path})"
		}
		register("server") {
			server()
			ideFolderName = "Forge"
			ideName = "Forge Server (${project.path})"
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

tasks {
	jar {
		finalizedBy("reobfJar")
		manifest {
			attributes(
				mapOf(
					"MixinConfigs" to "${mod.id}-common.mixins.json,${mod.id}-forge.mixins.json"
				)
			)
		}
	}
}
