## 2.0.0a

- Added structure overlap prevention
- Added global and per namespace flatness checks, which means flatness check can now be configured globally, per namespace or per structure
- Added global and per namespace biome checks
- Added global and per namespace max/min distance from world center checks
- Added "step" as a configurable field for structures
- Added "terrain adaptation" as a configurable field for structures
- Added "size" as a configurable field for jigsaw structures
- Added "max distance from center" as a configurable field for jigsaw structures
- Added missing frequency description
- Improved flatness and biome check performance, now it should be actually unrecognizable opposite to the previous versions
- Improved overall runtime (world generation) performance of the mod by caching values
- Improved GUI workflows, setting up everything should be easier and more clear
- Fixed GUI, which can now be opened and reloaded from the game (in the world)
- Updated biome dropdown, which should now display the full id of biome/tag, so seemingly duplicate tags like "#minecraf:is_ocean" and "#c:is_ocean" can be recognized more easily
- Lot of other small tweaks/fixes

Be aware that there are so many changes that everything can be unstable/non working, so it felt right to mark this as an alpha first and wait for your feedback.

## 1.0.21

- Fixed default salt value to the correct value of 0 instead of 1

## 1.0.20

- GUI now correctly keeps the groups/categories collapsed
- Fixed results not being correctly displayed right after search (scrolling to top was needed)
- Fixed crash related to disabled structures and explorer maps

## 1.0.19

- Fixed crash related to invalid biomes not being parsed properly in the config
- Improved biome check performance
- Improved flatness check performance
- Removed debug messages

## 1.0.18

- Fixed config loading error related to "enable_biome_check"

## 1.0.17

- Fixed global packs compat (new release contained the breaking change)
- Improved on how the compats are loaded, which should prevent minecraft crash

## 1.0.16

- Fixed frequency loading from the config (float instead of int)
- Added "/structurify locate" command, which works like async locator and will not freeze the world while locating the structure

## 1.0.15

- Fixed crash related to frequency
- Fixed frequency slider
- Updated biome check logic, so it actually checks the correct Y levels

## 1.0.14

- Decreased spacing/separation slider range max value to 512

## 1.0.13

- Fixed crash related to Repurposed Structures compat

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