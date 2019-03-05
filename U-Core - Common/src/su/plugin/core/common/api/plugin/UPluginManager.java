package su.plugin.core.common.api.plugin;

import java.util.HashMap;

import lombok.Getter;

public class UPluginManager {
	
	@Getter
	private static HashMap<String, UPlugin> plugins = new HashMap<>(); // Package, IUPlugin
	
	public void registerUPlugin(UPlugin plugin) {
		plugins.put(plugin.getPluginPackage(), plugin);
	}
	
	public void unRegisterUPlugin(UPlugin plugin) {
		plugins.remove(plugin.getPluginPackage());
	}
	
	public UPlugin getUPluginByPackage(String classPackage) {
		for(String pack : plugins.keySet()) {
			if(classPackage.equals(pack) || (classPackage.startsWith(pack) && classPackage.length() > pack.length() && classPackage.substring(pack.length(), pack.length() + 1).equals("."))) return plugins.get(pack);
		}
		
		return null;
	}
	
	public UPlugin getUPluginByName(String name) {
		for(UPlugin p : plugins.values()) {
			if(p.getName().equalsIgnoreCase(name)) return p;
		}
		
		return null;
	}
	
}