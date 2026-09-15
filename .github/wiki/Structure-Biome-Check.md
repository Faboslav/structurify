Biome check ensures that structures only generate in the correct biomes. When enabled, generation is skipped if the structure would generate in a biome that does not match its biome rules.

It can be enabled or disabled globally, per specific namespace, or per specific structure. Global settings act as the default, but they can be overridden. For example, it can be enabled globally while still being disabled for specific structure namespaces or individual structures. Likewise, it can remain disabled globally and only be enabled for selected namespaces or specific structures.

You can also choose a **Mode** for how the biome check behaves:

- **Strict**: Uses the structure’s default biome settings defined by the structure itself. If any part of the structure intersects a biome that is not allowed by those settings, the structure will not generate.

- **Blacklist**: The structure will not generate if any part of it intersects a biome or biome tag specified in the blacklist.

These settings can be configured through the [structure settings screen](structures-settings).

The images below demonstrate how structure biome check works.

<table width="100%" cellspacing="0" cellpadding="0" border="0">
<tr>
<td width="50%" align="center" valign="top">
<strong>Structure generation without biome check</strong><br><br>
<a href="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/biome_check/generation_without_biome_check.webp">
<img
src="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/biome_check/generation_without_biome_check.webp"
alt="Structure generation without biome check"
title="Structure generation without biome check"
width="100%">
</a>
</td>

<td width="50%" align="center" valign="top">
<strong>Structure generation with biome check</strong><br><br>
<a href="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/generation_with_checks.webp">
<img
src="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/generation_with_checks.webp"
alt="Structure generation with biome check"
title="Structure generation with biome check"
width="100%">
</a>
</td>
</tr>

<tr>
<td width="50%" align="center" valign="top">
Without the biome check enabled, the Plains Village does not generate entirely within the plains biome. Parts of the structure extend into the desert, jungle, and river biomes. This behavior can be prevented by enabling the <strong>Strict biome check mode</strong>.
</td>

<td width="50%" align="center" valign="top">
With the biome check enabled, the Plains Village generates exclusively in the plains biome defined by the structure’s biome configuration.
</td>
</tr>
</table>
<br>

## Configuring Global Biome Check

The global biome check setting controls whether the biome check is applied to structures by default during generation.

When enabled, structures will only generate if the biomes they intersect match the configured biome rules. This setting acts as the default behavior for all structures, but it can be overridden by namespace or individual structure settings.

<p align="center">
<a href="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/biome_check/global_biome_check.webp">
<img src="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/biome_check/global_biome_check.webp"
title="Globally enabling Biome Check"
alt="Globally enabling Biome Check">
</a>
</p>
<br>

## Configuring the biome check for namespace structures

You can configure the biome check for all structures within a specific namespace using the namespace settings.

This allows enabling or disabling the biome check for every structure belonging to that namespace at once. Namespace settings override the global biome check setting.

<p align="center">
<a href="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/biome_check/namespace_biome_check.webp">
<img src="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/biome_check/namespace_biome_check.webp"
title="Enabling Biome Check for structure namespace"
alt="Enabling Biome Check for structure namespace">
</a>
</p>
<br>

## Configuring the biome check for specific structure

You can configure the biome check for an individual structure using the structure’s settings.

This allows precise control over whether a specific structure should be restricted by biome rules during generation. Structure-specific settings override both global and namespace settings.

<p align="center">
<a href="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/biome_check/structure_biome_check.webp">
<img src="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/biome_check/structure_biome_check.webp"
title="Enabling Biome Check for specific structure"
alt="Enabling Biome Check for specific structure">
</a>
</p>
<br>