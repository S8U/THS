package su.plugin.core.bukkit.api.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;

public class GUIManager {
	
	@Getter
	private HashMap<String, GUI> playerGUIs = new HashMap<>();
	
	@Getter
	private HashMap<String, QuickBar> quickBars = new HashMap<>();
	
	public void setPlayerGUI(String name, GUI gui) {
		playerGUIs.put(name.toLowerCase(), gui);
	}
	
	public void setPlayerGUI(Player p, GUI gui) {
		setPlayerGUI(p.getName(), gui);
	}
	
	public void removePlayerGUI(String name) {
		playerGUIs.remove(name.toLowerCase());
	}
	
	public void removePlayerGUI(Player p) {
		removePlayerGUI(p.getName());
	}
	
	public GUI getPlayerGUI(String name) {
		return playerGUIs.get(name.toLowerCase());
	}
	
	public GUI getPlayerGUI(Player p) {
		return getPlayerGUI(p.getName());
	}
	
	public boolean hasGUI(String name) {
		return playerGUIs.containsKey(name.toLowerCase());
	}
	
	public boolean hasGUI(Player p) {
		return hasGUI(p.getName());
	}
	
	public List<Player> getPlayers(GUI gui) {
		List<Player> players = new ArrayList<>();
		
		for(String name : playerGUIs.keySet()) {
			GUI g = getPlayerGUI(name);
			if(!gui.equals(g)) continue;
			players.add(Bukkit.getPlayer(name));
		}
		
		return players;
	}
	
	public void setQuickBar(String name, QuickBar gui) {
		quickBars.put(name.toLowerCase(), gui);
	}
	
	public void setQuickBar(Player p, QuickBar gui) {
		setQuickBar(p.getName(), gui);
	}
	
	public void removeQuickBar(String name) {
		quickBars.remove(name.toLowerCase());
	}
	
	public void removeQuickBar(Player p) {
		removeQuickBar(p.getName());
	}
	
	public void clearQuickBar(Player p) {
		if(!hasQuickBar(p)) return;
		removeQuickBar(p);
		if(p == null) return;
		p.getInventory().clear();
	}
	
	public QuickBar getQuickBar(String name) {
		return quickBars.get(name.toLowerCase());
	}
	
	public QuickBar getQuickBar(Player p) {
		return getQuickBar(p.getName());
	}
	
	public boolean hasQuickBar(String name) {
		return quickBars.containsKey(name.toLowerCase());
	}
	
	public boolean hasQuickBar(Player p) {
		return hasQuickBar(p.getName());
	}
	
	public List<Player> getOnlinePlayers(QuickBar gui) {
		List<Player> players = new ArrayList<>();
		
		for(String name : quickBars.keySet()) {
			if(!gui.equals(getQuickBar(name))) continue;
			Player player = Bukkit.getPlayer(name);
			if(player == null) continue;
			players.add(player);
		}
		
		return players;
	}
	
	public List<String> getPlayers(QuickBar gui) {
		List<String> players = new ArrayList<>();
		
		for(String name : quickBars.keySet()) {
			if(!gui.equals(getQuickBar(name))) continue;
			players.add(name);
		}
		
		return players;
	}
	
} 