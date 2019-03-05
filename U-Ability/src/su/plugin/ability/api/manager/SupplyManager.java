package su.plugin.ability.api.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GameMap;
import su.plugin.core.bukkit.api.KCore;

public class SupplyManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private HashMap<String, List<ItemStack>> supplies = new HashMap<>();
	
	@Getter
	private List<Location> supplyLogs = new ArrayList<>();
	
	public void setSupply(String name, List<ItemStack> list) {
		supplies.put(name.toLowerCase(), list);
	}
	
	public boolean existsSupply(String name) {
		return supplies.containsKey(name.toLowerCase());
	}
	
	public List<ItemStack> getSupply(String name) {
		return supplies.get(name.toLowerCase());
	}
	
	public List<ItemStack> getRandomSupply() {
		if(supplies.size() < 1) return null;
		
		String[] supplystr = new String[supplies.size()];
		int i = 0;
		for(String sname : supplies.keySet()) {
			supplystr[i] = sname;
			i++;
		}
		int randomcount = new Random().nextInt(supplystr.length);
		
		return getSupply(supplystr[randomcount]);
	}
	
	public boolean createSupply(Location location, final List<ItemStack> supply) {
		final Block b = location.getBlock();
		b.setType(Material.CHEST);
		Bukkit.getScheduler().scheduleSyncDelayedTask(AbilityPlugin.getInstance(), new Runnable() {
			public void run() {
				if(api.isUseSupplyFirework()) {
					KCore.spawnFirework(location, false, false, Type.BALL_LARGE, Color.BLUE, Color.AQUA, 3);
				}
				Chest c = (Chest) b.getState();
				int i = 0;
				for(ItemStack item : supply) {
					if(i <36) {
						c.getInventory().addItem(item);
						i++;
					}
				}
				
			}
		}, 2);
		supplyLogs.add(location);
		return true;
	}
	
	public boolean createRandomSupply(Location location) {
		if(supplies.size() < 1) return false;
		return createSupply(location, getRandomSupply());
	}
	
	public Location createSupplyAtRandomLocation(GameMap map, List<ItemStack> supply, boolean tpAll) {
		Location location = map.getRandomLocation(tpAll);
		createSupply(location, supply);
		return location;
	}
	
	public Location createRandomSupplyAtRandomLocation(GameMap map, boolean tpAll) {
		if(supplies.size() < 1) return null;
		return createSupplyAtRandomLocation(map, getRandomSupply(), tpAll);
	}
	
}
