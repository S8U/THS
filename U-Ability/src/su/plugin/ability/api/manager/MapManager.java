package su.plugin.ability.api.manager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GameMap;

public class MapManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Setter
	@Getter
	private Location spawn;
	
	@Setter
	@Getter
	private GameMap playingMap;
	
	@Getter
	private LinkedHashMap<String, GameMap> maps = new LinkedHashMap<>();
	private HashMap<String, Location> leftLocation = new HashMap<>();
	private HashMap<String, Location> rightLocation = new HashMap<>();
	
	public void setMap(String name, GameMap map) {
		maps.put(name.toLowerCase(), map);
	}
	
	public void setLeftLocation(Player p, Location location) {
		leftLocation.put(p.getName().toLowerCase(), location);
	}
	
	public void setRightLocation(Player p, Location location) {
		rightLocation.put(p.getName().toLowerCase(), location);
	}
	
	public void deleteMap(GameMap map) {
		deleteMap(map.getName());
	}
	
	public void deleteMap(String name) {
		if(!existsMap(name)) return;
		maps.remove(name.toLowerCase());
	}
	
	public boolean existsMap(String name) {
		return maps.containsKey(name.toLowerCase());
	}
	
	public boolean existsLeftLocation(Player p) {
		return leftLocation.containsKey(p.getName().toLowerCase());
	}
	
	public boolean existsRightLocation(Player p) {
		return rightLocation.containsKey(p.getName().toLowerCase());
	}
	
	public GameMap getMap(String name) {
		if(!existsMap(name)) return null;
		return maps.get(name.toLowerCase());
	}
	
	public Location getLeftLocation(Player p) {
		return leftLocation.get(p.getName().toLowerCase());
	}
	
	public Location getRightLocation(Player p){
		return rightLocation.get(p.getName().toLowerCase());
	}
	
	public GameMap getRandomMap() {
		if(maps.size() < 1) return null;
		String[] mapstr = new String[maps.size()];
		int i = 0;
		for(String mapname : maps.keySet()) {
			mapstr[i] = mapname;
			i++;
		}
		Random r = new Random();
		int randomcount = r.nextInt(mapstr.length);
		return getMap(mapstr[randomcount]);
	}
	
	public Location getProgressLocation() {
		if(!api.getGameManager().isAutoMode()) return null;
		return api.getGameManager().isTeleportedAll() ?
				playingMap.getTPAllLocation() : (api.getGameManager().isTeleportedInMap() ? playingMap.getMapLocation() : spawn);
	}
	
}
