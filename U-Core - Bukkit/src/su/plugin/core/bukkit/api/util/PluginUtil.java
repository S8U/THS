package su.plugin.core.bukkit.api.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.experimental.UtilityClass;
import su.plugin.core.common.api.event.UnregisterableListener;

@UtilityClass
public class PluginUtil {
	
	public static Plugin getPlugin(String name) {
		for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if(!plugin.getName().equalsIgnoreCase(name)) continue;
			return plugin;
		}
		return null;
	}
	
	public static boolean existsPlugin(String name) {
		return getPlugin(name) != null;
	}
	
	public static void enablePlugin(Plugin plugin) {
		Bukkit.getPluginManager().enablePlugin(plugin);
	}
	
	public static void disablePlugin(Plugin plugin) {
		Bukkit.getPluginManager().disablePlugin(plugin);
	}
	
	public static Plugin loadPlugin(File file) {
		try {
			return Bukkit.getPluginManager().loadPlugin(file);
		} catch (UnknownDependencyException | InvalidPluginException | InvalidDescriptionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * unLoadPlugin
	 * 
	 *  플러그인이 없을 경우 0
	 *  오류 발생 시 -1
	 *  완료 시 1
	 */
	
	@SuppressWarnings("unchecked")
	public static int unLoadPlugin(Plugin plugin) {
		String name = plugin.getName();
		org.bukkit.plugin.PluginManager pluginManager = Bukkit.getPluginManager();
		
		if(pluginManager.getPlugin(name) == null) return 0;
		
		SimpleCommandMap commandMap = null;
		List<Plugin> plugins = null;
		Map<String, Plugin> names = null;
		Map<String, Command> commands = null;
		
		try {
			Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
			pluginsField.setAccessible(true);
			plugins = (List<Plugin>) pluginsField.get(pluginManager);
			
			Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
			lookupNamesField.setAccessible(true);
			names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);
			
			Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);
			
			Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
			knownCommandsField.setAccessible(true);
			commands = (Map<String, Command>) knownCommandsField.get(commandMap);
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
		
		disablePlugin(plugin);
		
		plugins.remove(plugin);
		
		names.remove(name);
		
		if(commandMap != null) {
			for(Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry<String, Command> entry = it.next();
				if(entry.getValue() instanceof PluginCommand) {
					PluginCommand c = (PluginCommand) entry.getValue();
					if(c.getPlugin() == plugin) {
						c.unregister(commandMap);
						it.remove();
					}
				}
			}
		}
		
		try {
			((URLClassLoader) plugin.getClass().getClassLoader()).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 1;
	}
	
	public static File getFile(JavaPlugin plugin) {
		try {
			Field field = JavaPlugin.class.getDeclaredField("file");
			field.setAccessible(true);
			return (File) field.get(plugin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void deletePlugin(JavaPlugin plugin) {
		try {
			unLoadPlugin(plugin);
			getFile(plugin).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int registerListeners(JavaPlugin plugin) {
		return registerListeners(plugin, null);
	}
	
	public static int registerListeners(JavaPlugin plugin, String pack) {
		int i = 0;
		
		try {
			ZipInputStream jarStream = new ZipInputStream(new FileInputStream(getFile(plugin)));
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
						Bukkit.getPluginManager().registerEvents(listener, plugin);
						i++;
					} catch (Exception ex) { }
				} catch(ClassNotFoundException cnfe) { }
			}
			
			jarStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return i;
	}
	
	public static void registerCommand(JavaPlugin plugin, CommandExecutor executor, String... commands) {
		PluginCommand command = plugin.getCommand(commands[0]);
		if(commands.length > 1) {
			List<String> aliases = new ArrayList<>();
			for(int i = 1; i < commands.length; i++) {
				aliases.add(commands[i]);
			}
			command.setAliases(aliases);
		}
		command.setExecutor(executor);
	}
	
	public static void unRegisterCommands(Plugin plugin) {
		if(!existsPlugin(plugin.getName())) return;
		
		SimpleCommandMap commandMap = (SimpleCommandMap) KReflectionUtil.getCommandMap();
		Map<String, Command> commands = null;
		
		try {
			Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
			knownCommandsField.setAccessible(true);
			commands = (Map<String, Command>) knownCommandsField.get(commandMap);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(commandMap != null) {
			for(Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); ) {
				Map.Entry<String, Command> entry = it.next();
				if(entry.getValue() instanceof PluginCommand) {
					PluginCommand c = (PluginCommand) entry.getValue();
					if(c.getPlugin() == plugin) {
						c.unregister(commandMap);
						it.remove();
					}
				}
			}
		}
	}
	
	public static void unRegisterListener(Listener listener) {
		HandlerList.unregisterAll(listener);
	}
	
	public static void unRegisterListeners(Plugin plugin) {
		HandlerList.unregisterAll(plugin);
	}
	
	public static String getMD5(JavaPlugin plugin) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(getFile(plugin)));
			DigestInputStream dis = new DigestInputStream(bis, md);
			
			while(dis.read() != -1);
			dis.close();
			bis.close();
			
			Formatter formatter = new Formatter();
			for(byte b : md.digest()) {
				formatter.format("%02x", b);
			}
			String fs = formatter.toString();
			formatter.close();
			
			return fs;
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}