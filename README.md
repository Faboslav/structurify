# ⚙️ Structurify

<hr>

Structurify is a configuration mod that makes configuring everything related to structures very easy and accessible, eliminating the hassle of creating multiple datapacks.

<p>
	<img src="https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structurify_configuration.webp">
</p>

<p></p>

Currently it is possible to:

* [Globally disable all structures](#structure-settings)
* [Disable individual structures](#structure-settings)
* [Blacklist specific structures in individual biomes](#biome-blacklist-for-specific-structures)
* [Globally set structure spread for all structures](#structure-spread-settings)
* [Individually set structure spread](#structure-spread-settings)

Possible future features that can be implemented:

* Disable individual structure pieces
* Change weight of individual structure pieces
* Utility commands related to the structures
* Disable specific structure tags
* Biome Tags in the biome blacklist
* Presets for specific settings

<br>

## 🖥️ In-Game usage

<hr>

### Structure settings

Structures are organized into categories based on the mods and datapacks in use, making them easier to manage.
It is possible to disable the generation of structures and [manage a blacklist of biomes](#biome-blacklist-for-specific-structures) where specific structures should not generate.

![Structures settings](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structures_settings.webp)

Specific structures can also be easily searched for across these categories.

![Search](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structures_search.webp)

### Biome Blacklist for specific structures

Each structure has its own configuration, allowing specific biomes to be blacklisted to prevent structure generation.

![Search](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structures_structure_biome_blacklist.webp)

### Structure Spread settings

Custom structure spread can be configured via global spacing and separation modifiers or through per-structure specific spacing and separation values.

* Spacing is the average distance in chunks between structures within the same structure set (group of structures).
* Separation is the minimum distance in chunks between structures within the same structure set (group of structures). The separation value cannot be greater than the spacing value.

![Structure Spread settings](https://raw.githubusercontent.com/Faboslav/structurify/master/.github/assets/images/structure_spread_settings.webp)

## 📘 JSON file configuration

<hr>

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

### 📝 general

The general section contains settings that apply globally across all structures and structure sets.

| Key                                                                                             | Description                                    | Default value | Examples                     |
|-------------------------------------------------------------------------------------------------|------------------------------------------------|---------------|------------------------------|
| [disabled_all_structures](#disable_all_structures)                                              | Disable all structures                         | `false`       | `true` <br> `false`          |
| [enable_global_spacing_and_separation_modifier](#enable_global_spacing_and_separation_modifier) | Enables global spacing and separation modifier | `true`        | `true` <br> `false`          |
| [global_spacing_and_separation_modifier](#global_spacing_and_separation_modifier)               | The global spacing and separation modifier     | `1.0`         | `0.1` <br> `1.0` <br> `2.0`  |

#### disable_all_structures

If set to `true` all structures will be disabled and will not be generated in the world regardless of the specific structure options.

```json
"disabled_all_structures": false
```

#### enable_global_spacing_and_separation_modifier

If set to `true` all structure sets (groups of structures) will have modified spacing and separation.

```json
"enable_global_spacing_and_separation_modifier": true
```

#### global_spacing_and_separation_modifier

When set to a value different from `1.0`, all structure sets (groups of structures) will be either more concentrated or more spread out.

```json
"global_spacing_and_separation_modifier": 1.0
```

- `< 1.0` - more concentrated
- `= 1.0` - unaffected (default value)
- `> 1.0` - more spread out

<br>

### 📝 structures

All structures related settings are saved to the `structures` field of the json file.


| Key                                                             | Description                 | Default value | Examples                                   |
|-----------------------------------------------------------------|-----------------------------|---------------|--------------------------------------------|
| [name](#name)                                                   | Structure identifier        | `-`           | `minecraft:shipwreck`                      |
| [is_disabled](#enable_global_spacing_and_separation_modifier)   | Disables the structure      | `false`       | `true` <br> `false`                        |
| [biome_blacklist_type](#global_spacing_and_separation_modifier) | Type of the biome blacklist | `NONE`        | `NONE` <br> `CENTER_PART` <br> `ALL_PARTS` |
| [blacklisted_biomes](#global_spacing_and_separation_modifier)   | Array of biome identifiers  | `[]`          | `["minecraft:deep_cold_ocean"]`            |

#### name

The unique identifier for the structure, typically in the format `mod_id:structure_name`.

```json
"name": "minecraft:shipwreck"
```

#### is_disabled

If set to `true` the structure will be disabled and will not be generated in the world.

```json
"is_disabled": false
```

#### biome_blacklist_type

When set to a value different from `1.0`, all structure sets will be either more concentrated or more spread out.

```json
"biome_blacklist_type": "NONE"
```

- `NONE` - The blacklist will be inactive even if values are provided.
- `CENTER_PART` - Only the starting piece (center part) of the structure cannot be in any of the blacklisted biomes.
- `ALL_PARTS` - All structure pieces cannot be in any of the blacklisted biomes.

#### blacklisted_biomes

Array of unique biome identifiers typically in the format `mod_id:biome_name`. An empty array means no biomes are blacklisted.

```json
"blacklisted_biomes": []
```

<br>

### 📝 structure_sets

All structure sets related settings are saved to the `structure_sets` field of the json file.

| Key                       | Description                 | Default value | Examples                |
|---------------------------|-----------------------------|---------------|-------------------------|
| [name](#name)             | Structure identifier        | `-`           | `minecraft:villages`    |
| [spacing](#spacing)       | Disables the structure      | `-`           | `34` <br> `8` <br> `60` |
| [separation](#separation) | Type of the biome blacklist | `-`           | `8` <br> `4` <br> `40`  |

#### name

The unique identifier for the structure, typically in the format `mod_id:structure_name`.

```json
"name": "minecraft:villages"
```

#### spacing

Spacing is the average distance in chunks between structures within the same structure set (group of structures).

```json
"spacing": 34
```

#### separation

"Separation is the minimum distance in chunks between structures within the same structure set (group of structures). The separation value cannot be greater than the spacing value.

```json
"spacing": 8
```

<br>

### 📝 examples

Example of disabling all of the vanilla mineshaft structures can be done with the following configuration:

```json
{
	"structures": [
		{
		  "name": "minecraft:mineshaft",
		  "is_disabled": true,
		  "biome_blacklist_type": "CENTER_PART",
		  "blacklisted_biomes": []
		},
		{
		  "name": "minecraft:mineshaft_mesa",
		  "is_disabled": true,
		  "biome_blacklist_type": "NONE",
		  "blacklisted_biomes": []
		}
	]
}
```

<br>
Forbidding the shipwreck structure to spawn in deep cold and frozen oceans can be done via the following configuration:

```json
{
	"structures": [
		{
			"name": "minecraft:shipwreck",
			"is_disabled": false,
			"biome_blacklist_type": "CENTER_PART",
			"blacklisted_biomes": [
				"minecraft:deep_cold_ocean",
				"minecraft:deep_frozen_ocean"
			]
		}
	]
}
```

<br>
With the following configuration plains village will not be generated at all if any part of the village would be placed on the river biome:

```json
{
	"structures": [
		{
			"name": "minecraft:village_plains",
			"is_disabled": false,
			"biome_blacklist_type": "ALL_PARTS",
			"blacklisted_biomes": [
				"minecraft:river"
			]
		}
	]
}
```

The following configuration complements the previous example by compensating for blacklisted biomes with more frequent generations:

```json
{
	"structure_sets": [
		{
			"name": "minecraft:villages",
			"spacing": 16,
			"separation": 8
		}
	]
}
```

<br>

## 💬 Community

<hr>

Feel free to <a href="https://discord.gg/QGwFvvMQCn">join our community at the discord server</a> to chat, share your creations, ask any question or to simply be updated about the latest development of the mod and notified when the new release is out. Also don't hesitate to <a href="https://github.com/Faboslav/structurify/issues">report any crash or bug via GitHub issues</a>.

<br>

## 👋 Support

<hr>

I will continue developing my mods as a hobby because I truly enjoy it. If you'd like to support me, you can do so on [Patreon](https://www.patreon.com/Faboslav) or [Ko-fi](https://ko-fi.com/faboslav). Your support is greatly appreciated.

<br>

## 📜 License

<hr>

The mod is licensed with [CC BY-NC-ND 4.0](https://raw.githubusercontent.com/Faboslav/structurify/master/LICENSE.txt) license.

Please feel free to explore my code for examples of how I've tackled and solved various challenges while developing this mod. You're welcome to incorporate code snippets into your own projects. Also feel free to use this mod in any modpack (although credit/link back to this page will be greatly appreciated).