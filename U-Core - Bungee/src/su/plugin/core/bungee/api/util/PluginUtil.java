package su.plugin.core.bungee.api.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import su.plugin.core.common.api.command.UnregisterableCommandListener;
import su.plugin.core.common.api.event.UnregisterableListener;

@UtilityClass
public class PluginUtil {
	
	public static int registerListeners(Plugin plugin) {
		return registerListeners(plugin, null);
	}
	
	public static int registerListeners(Plugin plugin, String pack) {
		int i = 0;
		
		try {
			ZipInputStream jarStream = new ZipInputStream(new FileInputStream(plugin.getFile()));
			ZipEntry item = null;
			
			while((item = jarStream.getNextEntry()) != null) {
				if(item.isDirectory() || !item.getName().endsWith(".class")) continue;
				
				String className = item.getName().replaceAll("/", ".").substring(0, item.getName().length() - 6);
				if(pack != null && !className.substring(0, className.lastIndexOf(".")).equals(pack)) continue;
				
				try {
					Class<?> c = Class.forName(className);
					
					try {
						Listener listener = (Listener) c.newInstance();
						if(listener instanceof UnregisterableListener) continue;
						ProxyServer.getInstance().getPluginManager().registerListener(plugin, listener);
						i++;
					} catch (Exception ex) { }
				} catch(ClassNotFoundException cnfe) { }
			}
				
			jarStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return i;
	}
	
	public static int registerCommands(Plugin plugin) {
		return registerCommands(plugin, null);
	}
	
	public static int registerCommands(Plugin plugin, String pack) {
		int i = 0;
		
		try {
			ZipInputStream jarStream = new ZipInputStream(new FileInputStream(plugin.getFile()));
			ZipEntry item = null;
			while((item = jarStream.getNextEntry()) != null) {
				if(item.isDirectory() || !item.getName().endsWith(".class")) continue;
				
				String className = item.getName().replaceAll("/", ".").substring(0, item.getName().length() - 6);
				if(pack != null && !className.substring(0, className.lastIndexOf(".")).equals(pack)) continue;
				
				try {
					Class<?> c = Class.forName(className);
					
					try {
						Command command = (Command) c.newInstance();
						if(command instanceof UnregisterableCommandListener) continue;
						ProxyServer.getInstance().getPluginManager().registerCommand(plugin, command);
						i++;
					} catch (Exception ex) { }
				} catch(ClassNotFoundException cnfe) { }
			}
			
			jarStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return i;
	}

	public static boolean existsPlugin(String name) {
		return ProxyServer.getInstance().getPluginManager().getPlugin(name) != null;
	}
	
}