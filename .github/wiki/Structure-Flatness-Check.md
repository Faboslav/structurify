Flatness check ensures that structures only generate on terrain that is sufficiently flat. When enabled, generation is skipped if the terrain in the area where the structure would generate varies too much in height.

It can be enabled or disabled globally, per specific namespace, or per specific structure. Global settings act as the default, but they can be overridden. For example, it can be enabled globally while still being disabled for specific structure namespaces or individual structures. Likewise, it can remain disabled globally and only be enabled for selected namespaces or specific structures.

These settings can be configured through the [structure settings screen](structures-settings).

The images below demonstrate how structure flatness check works.

<table width="100%" cellspacing="0" cellpadding="0" border="0">
<tr>
<td width="50%" align="center" valign="top">
<strong>Structure generation without flatness check</strong><br><br>
<a href="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/flatness_check/generation_without_flatness_check.webp">
<img
src="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/flatness_check/generation_without_flatness_check.webp"
alt="Structure generation without flatness check"
title="Structure generation without flatness check"
width="100%">
</a>
</td>

<td width="50%" align="center" valign="top">
<strong>Structure generation with flatness check</strong><br><br>
<a href="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/generation_with_checks.webp">
<img
src="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/generation_with_checks.webp"
alt="Structure generation with flatness check"
title="Structure generation with flatness check"
width="100%">
</a>
</td>
</tr>
</table>
<br>

## Configuring Global Flatness Check

The global flatness check setting controls whether the flatness check is applied to structures by default during generation.

When enabled, structures will only generate if the terrain within their generation area is sufficiently flat. This setting acts as the default behavior for all structures, but it can be overridden by namespace or individual structure settings.

<p align="center">
<a href="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/flatness_check/global_flatness_check.webp">
<img src="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/flatness_check/global_flatness_check.webp" title="Globally enabling Flatness Check" alt="Globally enabling Flatness Check">
</p>
</a>
<br>

## Configuring the flatness check for namespace structures

You can configure the flatness check for all structures within a specific namespace using the namespace settings.

This allows enabling or disabling the flatness check for every structure belonging to that namespace at once. Namespace settings override the global flatness check setting.

<p align="center">
<a href="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/flatness_check/namespace_flatness_check.webp">
<img src="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/flatness_check/namespace_flatness_check.webp" title="Enabling Flatness Check for specific structure namespace" alt="Enabling Flatness Check for specific structure namespace">
</p>
</a>
<br>

## Configuring the flatness check for specific structure

You can configure the flatness check for an individual structure using the structure’s settings.

This allows precise control over whether a specific structure should require flat terrain during generation. Structure-specific settings override both global and namespace settings.

<p align="center">
<a href="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/flatness_check/structure_flatness_check.webp">
<img src="https://raw.githubusercontent.com/Faboslav/structurify/refs/heads/master/.github/assets/images/wiki/structures/flatness_check/structure_flatness_check.webp" title="Enabling Flatness Check for specific structure namespace" alt="Enabling Flatness Check for specific structure namespace">
</p>
</a>
<br>