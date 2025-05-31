## 1.0.12

- A lot of quality of life features like keeping search, scroll position and more
- Added support for biome tags
- Added compatibility with the Open Loader
- Added compatibility with the Terra
- Added "salt" as a configurable field for structure sets
- Added "frequency" as a configurable field for structure sets
- Fixed mod crash related to latest YACL
- Fixed main menu blur (on 1.21.4)
- Fixed error related to initial config creation
- Fixed mod crash related to missing/incorrect translations for biomes
- Increased spacing/separation slider range from 128 to 256

## 1.0.11

- Removed the dev console spam (sorry for the troubles and multiple releases)

## 1.0.10

- Fixed crash related to invalid catalogue compat setup

## 1.0.9

- Fixed typo causing invalid saving and loading of "disable_all_structures" option
- Improved GUI behavior, it should be more clear what is now possible and what is not
- Added new "min structure distance from world center" (min_structure_distance_from_world_center) global setting
- Added new "terrain flatness" check configuration to each structure
- Added new "config_version" property, so migrating between different config versions in the future is more manageable

## 1.0.8

- Fixed critical compatibility issue which prevented structure generation in the non vanilla biomes (for example Blue Skies)
- Structure "biome" field in json is now de/composed based on "whitelisted_biomes" and "blacklisted_biomes" fields instead to possibly avoid the list of 280 biome entries in modded environments

## 1.0.7

- Added compatibility with the Structure Gel API, ensuring that mods like Dungeons Enhanced are now fully affected by all settings
- Fixed a bug in Forge (and possibly NeoForge) where structures were not generating as expected

## 1.0.6

- Improved GUI of structure settings
- Removed biome blacklist in favor of direct biome editing (allowing to add or remove any biome) along with the optional biome check feature
- Fixed incompatibility with Repurposed Structures

## 1.0.5

- Fixed incompatibility with GregTech CEu: Modern and some other mods
- Improved performance up to 50% in some cases by correctly handling registry access
- Prepared infrastructure for caching to possibly enhance performance further

## 1.0.4

- Fixed incompatibility with Placebo/Apothic Enchanting mods
- Improved performance by creating the config screens lazily on demand

## 1.0.3

- Fixed lock on biome blacklist type
- Improved compatibility with other mods

## 1.0.2

- 1.21.1 port
- Improved compatibility with other mods

## 1.0.1

- Fixed crash related to spacing/separation global modifier
- Improved compatibility with other mods

## 1.0.0

- Initial release