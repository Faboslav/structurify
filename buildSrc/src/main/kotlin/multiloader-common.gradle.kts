plugins {
	id("java")
	id("idea")
	id("java-library")
}

version = "${loader}-${commonMod.version}+mc${stonecutterBuild.current.version}"

base {
	archivesName = commonMod.id
}

java {
	val javaVersion = commonProject.prop("java.version")!!.toInt()

	toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

repositories {
	mavenCentral()

	exclusiveContent {
		forRepository {
			maven("https://repo.spongepowered.org/repository/maven-public") {
				name = "Sponge"
			}
		}
		filter {
			includeGroupAndSubgroups("org.spongepowered")
		}
	}

	exclusiveContent {
		forRepositories(
			maven("https://maven.parchmentmc.org") {
				name = "ParchmentMC"
			},
			maven("https://maven.neoforged.net/releases") {
				name = "NeoForge"
			}
		)
		filter {
			includeGroup("org.parchmentmc.data")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven") {
				name = "Modrinth"
			}
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://www.cursemaven.com") {
				name = "CurseMaven"
			}
		}
		filter {
			includeGroup("curse.maven")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://maven.terraformersmc.com/releases/") {
				name = "TerraformersMC"
			}
		}
		filter {
			includeGroup("dev.emi")
			includeGroupAndSubgroups("com.terraformersmc")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://thedarkcolour.github.io/KotlinForForge/") {
				name = "KotlinForForge"
			}
		}
		filter {
			includeGroup("thedarkcolour")
		}
	}

	maven("https://maven.isxander.dev/releases") {
		name = "isXander Releases"
		content {
			includeGroupAndSubgroups("dev.isxander")
		}
	}

	maven("https://maven.isxander.dev/snapshots") {
		name = "isXander Snapshots"
		content {
			includeGroupAndSubgroups("dev.isxander")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://maven.quiltmc.org/repository/release") {
				name = "QuiltMC"
			}
		}
		filter {
			includeGroupAndSubgroups("org.quiltmc")
			includeGroupAndSubgroups("org.quiltmc.parsers")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://maven.ladysnake.org/releases") {
				name = "Ladysnake Libs"
			}
		}
		filter {
			includeGroup("dev.emi")
			includeGroupAndSubgroups("org.ladysnake")
			includeGroupAndSubgroups("io.github.ladysnake")
			includeGroupAndSubgroups("dev.onyxstudios")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://maven.theillusivec4.top/") {
				name = "TheIllusiveC4"
			}
		}
		filter {
			includeGroupAndSubgroups("top.theillusivec4")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://maven.jamieswhiteshirt.com/libs-release") {
				name = "JamieWhiteshirt"
			}
		}
		filter {
			includeGroup("com.jamieswhiteshirt")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") {
				name = "DevAuth"
			}
		}
		filter {
			includeGroup("me.djtheredstoner")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://maven.resourcefulbees.com/repository/maven-public/") {
				name = "ResourcefulBees"
			}
		}
		filter {
			includeGroupAndSubgroups("com.teamresourceful")
			includeGroupAndSubgroups("earth.terrarium")
		}
	}

	maven("https://oss.sonatype.org/content/repositories/snapshots") {
		name = "Sonatype Snapshots"
		content {
			includeGroupByRegex(".*")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://maven.blamejared.com") {
				name = "BlameJared"
			}
		}
		filter {
			includeGroupAndSubgroups("net.darkhax")
			includeGroupAndSubgroups("mezz.jei")
		}
	}

	exclusiveContent {
		forRepository {
			maven("https://nexus.resourcefulbees.com/repository/telepathicgrunt/") {
				name = "TelepathicGrunt"
			}
		}
		filter {
			includeGroup("com.telepathicgrunt")
		}
	}
}

tasks {
	processResources {
		val expandProps = mapOf(
			"javaVersion" to commonMod.propOrNull("java.version"),
			"modId" to commonMod.id,
			"modName" to commonMod.name,
			"modVersion" to commonMod.version,
			"modGroup" to commonMod.group,
			"modAuthor" to commonMod.author,
			"modDescription" to commonMod.description,
			"modLicense" to commonMod.license,
			"modGitHub" to commonMod.github,
			"modDiscord" to commonMod.discord,
			"minecraftVersion" to commonMod.propOrNull("minecraft_version"),
			"minMinecraftVersion" to commonMod.propOrNull("min_minecraft_version"),
			"fabricLoaderVersion" to commonMod.depOrNull("fabric_loader"),
			"fabricApiVersion" to commonMod.depOrNull("fabric_api"),
			"forgeVersion" to commonMod.depOrNull("forge"),
			"neoForgeVersion" to commonMod.depOrNull("neoforge"),
			"yaclVersion" to commonMod.depOrNull("yacl"),
			"modMenuVersion" to commonMod.depOrNull("mod_menu"),
		).filterValues { it?.isNotEmpty() == true }.mapValues { (_, v) -> v!! }

		val jsonExpandProps = expandProps.mapValues { (_, v) -> v.replace("\n", "\\\\n") }

		filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
			expand(expandProps)
		}

		filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json")) {
			expand(jsonExpandProps)
		}

		inputs.properties(expandProps)
	}
}

tasks.named("processResources") {
	dependsOn(":common:${commonMod.mc}:stonecutterGenerate")
}