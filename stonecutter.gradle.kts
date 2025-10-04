val IS_CI = System.getenv("CI") == "true"

plugins {
    id("dev.kikugie.stonecutter")
	id("net.neoforged.moddev") version "2.0.107" apply false
	id("fabric-loom") version "1.11-SNAPSHOT" apply false
}

if (IS_CI) stonecutter active null
else stonecutter active "1.21.9" /* [SC] DO NOT EDIT */
