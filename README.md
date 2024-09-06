<br>

<center>
	<img title="Friends&amp;Foes" src="https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/logo/logo.png" alt="Structurify" width="743">
</center>

<br>

<center>
	<a style="text-decoration: none;" href="https://ko-fi.com/faboslav">
		<img src="https://img.shields.io/static/v1?label=Support me&message=Ko-fi&color=4B4341&labelColor=4B4341&logoColor=ffffff&style=for-the-badge&logo=ko-fi" alt="Ko-fi">
	</a>
	<a style="text-decoration: none;" href="https://www.patreon.com/Faboslav">
		<img src="https://img.shields.io/endpoint.svg?color=4B4341&label=Support me&labelColor=4B4341&logoColor=ffffff&url=https://shieldsio-patreon.vercel.app/api?username=Faboslav&type=patrons&style=for-the-badge" alt="Patreon">
	</a>
	<a style="text-decoration: none;" href="https://discord.gg/QGwFvvMQCn">
		<img src="https://img.shields.io/discord/924964658169913404?style=for-the-badge&logo=discord&logoColor=ffffff&label=Join Discord&labelColor=4B4341&color=4B4341" alt="Join Discord">
	</a>
	<a style="text-decoration: none;" href="https://modrinth.com/mod/yacl">
		<img src="https://img.shields.io/static/v1?label=&amp;message=Requires YACL&amp;color=4B4341&amp;labelColor=4B4341&amp;logoColor=white&amp;style=for-the-badge&amp;logo=modrinth" height="28" />
	</a>
</center>

<br>

Structurify is a configuration mod that makes configuring everything related to structures very easy and accessible, eliminating the hassle of creating multiple datapacks.

<p>
	<img src="https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structurify_configuration.webp">
</p>

**Currently it is possible to:**

* **Globally disable all structures:** Easily disable all structures across your world with a single setting, simplifying your world generation process.
* **Disable individual structures:** Disable specific structures individually, giving you precise control over which ones generate in your world.
* **Manage biomes for specific structures:** Customize the list of biomes for individual structures, ensuring they only generate in selected biomes.
* **Globally set structure spread for all structures:** Set global spacing and separation modifiers for all structures, enabling consistent structure spread throughout your world.
* **Individually set structure spread:** Adjust spacing and separation values for specific structures, allowing for customized generation distances between them.

**Possible future features that can be implemented:**

* Disable individual structure pieces
* Change weight of individual structure pieces
* Utility commands related to the structures
* Presets for specific settings

<br>

# 🖥️ In-Game configuration

## Structure settings

Structures are organized into categories based on the mods and datapacks in use, making them easier to manage.
It is possible to disable the generation of structures and manage a list of biomes where specific structures should generate.

![Structures settings](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structures_settings.webp)

Specific structures can also be easily searched for across these categories.

![Search](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structures_search.webp)

## Biome managment for specific structures

Each structure has its own configuration, allowing specific biomes to be added and/or removed to adjust structure generation.

![Search](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structures_structure_biome_blacklist.webp)

## Structure Spread settings

Custom structure spread can be configured via global spacing and separation modifiers or through per-structure specific spacing and separation values.

* Spacing is the average distance in chunks between structures within the same structure set (group of structures).
* Separation is the minimum distance in chunks between structures within the same structure set (group of structures). The separation value cannot be greater than the spacing value.

![Structure Spread settings](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structure_spread_settings.webp)

<br>

# 📝 JSON file configuration

All configurations mentioned in the `In-Game` section of this guide are mirrored and saved in a JSON file located at `config/structurify.json`.
This file is particularly useful for managing configurations on the server side.

The default content of the structurify.json file looks like this:

```json
{
	"general": {
		"disabled_all_structures": false,
		"enable_global_spacing_and_separation_modifier": true,
		"global_spacing_and_separation_modifier": 1.0
	}, 
	"structures": [],
  	"structure_sets": []
}
```

<br>

## general

The general section contains settings that apply globally across all structures and structure sets.

| Key                                           | Description                                    | Default value | Examples                    |
|-----------------------------------------------|------------------------------------------------|---------------|-----------------------------|
| disabled_all_structures                       | Disable all structures                         | `false`       | `true` <br> `false`         |
| enable_global_spacing_and_separation_modifier | Enables global spacing and separation modifier | `true`        | `true` <br> `false`         |
| global_spacing_and_separation_modifier        | The global spacing and separation modifier     | `1.0`         | `0.1` <br> `1.0` <br> `2.0` |


### disable_all_structures

If set to `true` all structures will be disabled and will not be generated in the world regardless of the specific structure options.

```json
"disabled_all_structures": false
```

### enable_global_spacing_and_separation_modifier

If set to `true` all structure sets (groups of structures) will have modified spacing and separation.

```json
"enable_global_spacing_and_separation_modifier": true
```

### global_spacing_and_separation_modifier

When set to a value different from `1.0`, all structure sets (groups of structures) will be either more concentrated or more spread out.

```json
"global_spacing_and_separation_modifier": 1.0
```

- `< 1.0` - more concentrated
- `= 1.0` - unaffected (default value)
- `> 1.0` - more spread out

<br>

## structures

All structures related settings are saved to the `structures` field of the json file.


| Key                  | Description                      | Default value | Examples                        |
|----------------------|----------------------------------|---------------|---------------------------------|
| name                 | Structure identifier             | `-`           | `minecraft:shipwreck`           |
| is_disabled          | Disables the structure           | `false`       | `true` <br> `false`             |
| biomes               | Array of biome identifiers       | `[]`          | `["minecraft:deep_cold_ocean"]` |
| enable_biome_check   | Enables the biome check          | `false`       | `true` <br> `false`             |
| biome_check_distance | Biome check distance (in blocks) | `-`           | `8` <br> `16` <br> `128`        |

### name

The unique identifier for the structure, typically in the format `mod_id:structure_name`.

```json
"name": "minecraft:shipwreck"
```

### is_disabled

If set to `true` the structure will be disabled and will not be generated in the world.

```json
"is_disabled": false
```

### biomes

Array of unique biome identifiers typically in the format `mod_id:biome_name`. An empty array means that structures won't generate anywhere.

```json
"biomes": []
```

### enable_biome_check

If set to `true` the structure will only generate if all biomes within the specified distance are present in the list of biomes.

```json
"enable_biome_check": false
```

### biome_check_distance

The distance in blocks from the structure’s center within which biomes will be checked.

```json
"biome_check_distance": 32
```

<br>

## structure_sets

All structure sets related settings are saved to the `structure_sets` field of the json file.

| Key         | Description                                                                                                                                                                         | Default value | Examples                |
|-------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|-------------------------|
| name        | Structure identifier                                                                                                                                                                | `-`           | `minecraft:villages`    |
| spacing     | Spacing is the average distance in chunks between structures within the same structure set (group of structures)                                                                    | `-`           | `34` <br> `8` <br> `60` |
| separation  | Separation is the minimum distance in chunks between structures within the same structure set (group of structures). The separation value cannot be greater than the spacing value. | `-`           | `8` <br> `4` <br> `40`  |

### name

The unique identifier for the structure, typically in the format `mod_id:structure_name`.

```json
"name": "minecraft:villages"
```

### spacing

Spacing is the average distance in chunks between structures within the same structure set (group of structures).

```json
"spacing": 34
```

### separation

Separation is the minimum distance in chunks between structures within the same structure set (group of structures). The separation value cannot be greater than the spacing value.

```json
"separation": 8
```

<br>

# ⚙️ Compatibility

Structurify is designed to be fully compatible with most of the world generation mods and datapacks, and it currently offers enhanced compatibility with the following mods:

* [Paxi](https://modrinth.com/mod/paxi)
* [Global Packs](https://modrinth.com/mod/globalpacks)
* [Global Datapacks](https://modrinth.com/mod/datapacks)

<br>

# 💬 Community

Feel free to <a href="https://discord.gg/QGwFvvMQCn">join our community at the discord server</a> to chat, share your creations, ask any question or to simply be updated about the latest development of the mod and notified when the new release is out. Also don't hesitate to <a href="https://github.com/Faboslav/structurify/issues">report any crash or bug via GitHub issues</a>.

<br>

# 👋 Support

I will continue developing my mods as a hobby because I truly enjoy it. If you'd like to support me, you can do so on [Patreon](https://www.patreon.com/Faboslav) or [Ko-fi](https://ko-fi.com/faboslav). Your support is greatly appreciated.

<br>

# 📜 License

The mod is licensed with [CC BY-NC-ND 4.0](https://raw.githubusercontent.com/Faboslav/structurify/master/LICENSE.txt) license.

Please feel free to explore my code for examples of how I've tackled and solved various challenges while developing this mod. You're welcome to incorporate code snippets into your own projects. Also feel free to use this mod in any modpack (although credit/link back to this page will be greatly appreciated).