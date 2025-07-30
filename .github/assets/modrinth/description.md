<br>

<center>
	<img title="Structurify" src="https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/logo/logo.png" alt="Structurify" width="743">
</center>

<br>

<center>
	<a style="text-decoration: none;" href="https://ko-fi.com/faboslav">
		<img src="https://img.shields.io/static/v1?label=&message=Buy Me a Coffee&color=5b9c51&labelColor=5b9c51&logoColor=ffffff&style=for-the-badge&logo=ko-fi" alt="Buy Me a Coffee">
	</a>
	<a style="text-decoration: none;" href="https://www.patreon.com/Faboslav">
		<img src="https://img.shields.io/static/v1?label=&message=Become a Patron&color=5b9c51&labelColor=5b9c51&logoColor=ffffff&style=for-the-badge&logo=patreon" alt="Become a Patreon">
	</a>
	<a style="text-decoration: none;" href="https://discord.gg/faboland">
		<img src="https://img.shields.io/discord/924964658169913404?style=for-the-badge&logo=discord&logoColor=ffffff&label=Join Our Discord&labelColor=5b9c51&color=5b9c51" alt="Join Our Discord">
	</a>
</center>

<br>

Structurify is a configuration mod that makes configuring everything related to structures very easy and accessible,
eliminating the hassle of creating multiple datapacks.

<p>
	<img src="https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structurify_configuration.webp">
</p>

**Core Features:**

* **Globally disable all structures:** Easily disable all structures across your world with a single setting,
  simplifying your world generation process.
* **Disable individual structures:** Disable specific structures individually, giving you precise control over which
  ones generate in your world.
* **Manage biomes for specific structures:** Customize the list of biomes for individual structures, ensuring they only
  generate in selected biomes.
* **Globally set structure spread for all structures:** Set global spacing and separation modifiers for all structure
  sets, enabling consistent structure spread throughout your world.
* **Individually set structure spread:** Adjust spacing and separation values for specific structure sets, allowing for
  customized generation distances between them.

**Other Features:**

* **Set Salt and Frequency:** Customize salt and frequency values for individual structure sets, allowing for consistent yet varied generation on the same world seed.
* **Flatness check:** The structure will only generate if the terrain within the specified distance is flat enough based on the specified threshold.
* **Biome check:** Limit structure generation to areas where all nearby biomes fall within a defined list, useful for avoiding cases like villages spawning across rivers.

**Available Commands:**

* **/structurify dump:** Dumps complete config file with default settings to the file.

**Future plans:**

* Exclusion Zones
* Disable individual structure pieces
* Change weight of individual structure pieces
* Utility commands related to the structures
* Presets for specific settings

<br>

# 💡 How to use the mod

This mod is designed for in-game configuration, so it requires the **[YACL (YetAnotherConfigLib)](https://modrinth.com/mod/yacl)** mod.

## Fabric/Quilt

If you’re using Fabric or Quilt, you’ll also need the [Mod Menu](https://modrinth.com/mod/modmenu)
mod or another mod that provides access to mod configs.

## NeoForge/Forge

No extra mods are needed.

<br>

# 🖥️ In-Game configuration

## Structure settings

Structures are organized into categories based on the mods and datapacks in use (namespaces), making them easier to
manage.
It is possible to disable the generation of structures and manage a list of biomes where specific structures should
generate.

![Structures settings](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structures_settings.webp)

Specific structures can also be easily searched for across these categories.

![Search](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structures_search.webp)

## Biome managment for specific structures

Each structure has its own configuration, allowing specific biomes to be added and/or removed to adjust structure
generation.

![Search](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structures_structure_biome_blacklist.webp)

## Structure Sets (Structure Spread) settings

Custom structure spread can be configured via global spacing and separation modifiers or through per-structure specific
spacing and separation values.

* Spacing is the average distance in chunks between structures within the same structure set (group of structures).
* Separation is the minimum distance in chunks between structures within the same structure set (group of structures).
  The separation value cannot be greater than the spacing value.

Additionally both salt and frequency can be configured for each specific structure set.

![Structure Spread settings](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structure_spread_settings.webp)

<br>

# 📝 JSON file configuration

All configurations mentioned in the `In-Game` section of this guide are mirrored and saved in a JSON file located at
`config/structurify.json`.
This file is particularly useful for managing configurations on the server side. For that case it is recommended to
configure everything based on the `In-Game` section of this guide.

# ⚙️ Compatibility

Structurify is designed to be fully compatible with most of the world/structure generation mods and datapacks, and it currently
offers enhanced compatibility with the following:

### Global datapack loaders:

* [Paxi](https://modrinth.com/mod/paxi)
* [Open Loader](https://modrinth.com/mod/open-loader)
* [Global Packs](https://modrinth.com/mod/globalpacks)
* [Global Datapacks](https://modrinth.com/mod/datapacks)

### Mods:
* [Repurposed Structures](https://modrinth.com/mod/repurposed-structures-fabric)
* [YUNG's mods](https://modrinth.com/user/YUNGNICKYOUNG/mods)
* [Structure Gel API](https://modrinth.com/mod/structure-gel-api)
* [Terra](https://modrinth.com/plugin/terra)

Since structure generation is a complex system, things can occasionally break, especially with mods that implement their own custom structure generation logic or heavily alter vanilla generation behavior.

<br>

# 💬 Community

Feel free to <a href="https://discord.gg/faboland">join our community at the discord server</a> to chat, share your
creations, ask any question or to simply be updated about the latest development of the mod and notified when the new
release is out. Also don't hesitate to <a href="https://github.com/Faboslav/structurify/issues">report any crash or bug
via GitHub issues</a>.

<br>

# 👋 Support

I will continue developing my mods as a hobby because I truly enjoy it. If you'd like to support me, you can do so
on [Patreon](https://www.patreon.com/Faboslav) or [Ko-fi](https://ko-fi.com/faboslav). Your support is greatly
appreciated.

<br>

# 📜 License

The mod is licensed with [CC BY-NC-ND 4.0](https://raw.githubusercontent.com/Faboslav/structurify/master/LICENSE.txt)
license.

Please feel free to explore my code for examples of how I've tackled and solved various challenges while developing this
mod. You're welcome to incorporate code snippets into your own projects. Also feel free to use this mod in any modpack (
although credit/link back to this page will be greatly appreciated).