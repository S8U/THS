package su.plugin.ability.api.manager;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import lombok.Getter;

public class KitManager {
	
	@Getter
	private HashMap<String, Inventory> kits = new HashMap<>();
	
	public void setKit(String kit, Inventory inventory) {
		kits.put(kit.toLowerCase(), inventory);
	}
	
	public Inventory createKit(String name) {
		Inventory inv = Bukkit.createInventory(null, 27, name);
		setKit(name, inv);
		return inv;
	}
	
	public boolean existsKit(String kit) {
		return kits.containsKey(kit.toLowerCase());
	}
	
	public Inventory getKit(String kit) {
		if(!existsKit(kit)) return null;
		return kits.get(kit.toLowerCase());
	}
	
}
