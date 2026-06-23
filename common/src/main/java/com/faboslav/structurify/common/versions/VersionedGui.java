package com.faboslav.structurify.common.versions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

//? if >= 26.2 {
import net.minecraft.client.gui.Gui;
//?}

public final class VersionedGui
{
	//? if >= 26.2 {
	public static Gui getGui(Minecraft minecraft)
	//?} else {
	/*public static Minecraft getGui(Minecraft minecraft)
	 *///?}
	{
		//? if >= 26.2 {
		return minecraft.gui;
		//?} else {
		/*return minecraft;
		 *///?}
	}

	//? if >= 26.2 {
	public static Gui getGui()
	//?} else {
	/*public static Minecraft getGui()
	 *///?}
	{
		//? if >= 26.2 {
		return getGui(Minecraft.getInstance());
		//?} else {
		/*return getGui(Minecraft.getInstance());
		 *///?}
	}

	//? if >= 26.2 {
	public static Screen getScreen(Minecraft minecraft)
	//?} else {
	/*public static Screen getScreen(Minecraft minecraft)
	 *///?}
	{
		//? if >= 26.2 {
		return minecraft.gui.screen();
		//?} else {
		/*return minecraft.screen;
		 *///?}
	}

	//? if >= 26.2 {
	public static Screen getScreen()
	//?} else {
	/*public static Screen getScreen()
	 *///?}
	{
		//? if >= 26.2 {
		return getScreen(Minecraft.getInstance());
		//?} else {
		/*return getScreen(Minecraft.getInstance());
		 *///?}
	}
}
