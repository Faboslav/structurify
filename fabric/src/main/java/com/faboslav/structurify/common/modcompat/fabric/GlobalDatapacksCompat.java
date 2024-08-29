package com.faboslav.structurify.common.modcompat.fabric;

import com.faboslav.structurify.common.modcompat.ModCompat;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;

/*? if >1.20.1 {*/
/*import net.minecraft.world.level.validation.DirectoryValidator;
*//*?}*/

public final class GlobalDatapacksCompat implements ModCompat
{
	@Override
	public EnumSet<Type> compatTypes() {
		return EnumSet.of(Type.CUSTOM_RESOURCE_PACK_PROVIDERS);
	}

	@Override
	public ArrayList<RepositorySource> getResourcePackProviders() {
		var resourcePackProviders = new ArrayList<RepositorySource>();

		try {
			Class<?> globalDatapackClass = Class.forName("me.declipsonator.globaldatapack.GlobalDatapack");
			Field globalPackFolderField = globalDatapackClass.getField("globalPackFolder");
			Path globalPackFolder = (Path) globalPackFolderField.get(null);

			/*? if =1.20.1 {*/
			resourcePackProviders.add(new FolderRepositorySource(globalPackFolder, PackType.SERVER_DATA, PackSource.WORLD));
			/*?} else {*/
			/*resourcePackProviders.add(new FolderRepositorySource(globalPackFolder, PackType.SERVER_DATA, PackSource.WORLD, new DirectoryValidator(path -> true)));
			 *//*?}*/
		} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
			System.out.println("Dependency not found or field access failed: " + e.getMessage());
		}

		return resourcePackProviders;
	}
}
