package su.plugin.core.bukkit.api.permission;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import lombok.SneakyThrows;
import su.plugin.core.common.api.plugin.UPlugin;

public class PermissionManager {
	
	public boolean registerPermissions(UPlugin plugin) {
		try {
			ZipInputStream jarStream = new ZipInputStream(new FileInputStream(plugin.getFile()));
			ZipEntry item = null;
			
			while((item = jarStream.getNextEntry()) != null) {
				if(item.isDirectory() || !item.getName().endsWith(".class")) continue;
				String className = item.getName().replaceAll("/", ".");
				Class<?> c = Class.forName(className.substring(0, className.length() - 6));
				try {
					UPermissionList pl = (UPermissionList) c.newInstance();
					registerPermissions(pl);
					
					return true;
				} catch (Exception ex) { }
			}
			
			jarStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean registerPermissions(UPlugin plugin, String pack) {
		try {
			ZipInputStream jarStream = new ZipInputStream(new FileInputStream(plugin.getFile()));
			ZipEntry item = null;
			
			while((item = jarStream.getNextEntry()) != null) {
				if(item.isDirectory() || !item.getName().endsWith(".class")) continue;
				String className = item.getName().replaceAll("/", ".").substring(0, item.getName().length() - 6);
				if(!className.substring(0, className.lastIndexOf(".")).equals(pack)) continue;
				Class<?> c = Class.forName(className);
				try {
					UPermissionList pl = (UPermissionList) c.newInstance();
					registerPermissions(pl);
					
					return true;
				} catch (Exception ex) { }
			}
			
			jarStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@SneakyThrows(Exception.class)
	public static void registerPermissions(UPermissionList permissionList) {
		for(Field field : permissionList.getClass().getFields()) {
			Bukkit.getPluginManager().addPermission(new Permission(field.get(permissionList).toString()));
		}
	}
	
}