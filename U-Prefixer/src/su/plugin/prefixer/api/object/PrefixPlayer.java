package su.plugin.prefixer.api.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.prefixer.api.PrefixerAPI;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class PrefixPlayer {
	
	private final PlayerKey playerKey;
	
	@Setter
	private List<String> prefixes = new ArrayList<>();
	
	@Setter
	private HashMap<Integer, String> mainPrefixes = new HashMap<>();
	
	public Player getBukkitPlayer() {
		return KCore.getPlayer(playerKey);
	}
	
	public boolean isOnline() {
		return getBukkitPlayer() != null;
	}
	
	//
	
	public boolean addPrefix(String prefix) {
		if(prefixes.contains(prefix)) return false;
		
		return prefixes.add(prefix);
	}
	
	public boolean deletePrefix(String prefix) {
		return prefixes.remove(prefix);
	}
	
	public boolean hasPrefix(String prefix) {
		return prefixes.contains(prefix);
	}
	
	//
	
	public void setMainPrefix(int priority, String prefix) {
		if(isMainPrefix(prefix)) return;
		mainPrefixes.put(priority, prefix);
	}
	
	public void addMainPrefix(String prefix) {
		setMainPrefix(mainPrefixes.size(), prefix);
	}
	
	public void removeMainPrefix(String prefix) {
		mainPrefixes.values().remove(prefix);
	}
	
	public void removeMainPrefix() {
		mainPrefixes.clear();
	}
	
	public boolean isMainPrefix(String prefix) {
		return mainPrefixes.values().contains(prefix);
	}
	
	public boolean hasMainPrefix() {
		return !mainPrefixes.isEmpty();
	}
	
	public List<String> getMainPrefixList() {
		List<String> list = new ArrayList<>();
		
		List<Integer> il = new ArrayList<>();
		il.addAll(mainPrefixes.keySet());
		Collections.sort(il);
		
		for(int i = 0; i < mainPrefixes.size(); i++) {
			list.add(mainPrefixes.get(il.get(i)));
		}
		
		return list;
	}
	
	public int getMainPrefixPriority(String mainPrefix) {
		for(int priority : mainPrefixes.keySet()) {
			if(mainPrefixes.get(priority).equals(mainPrefix)) return priority;
		}
		
		return -1;
	}
	
	public Location getMainPrefixLocation() {
		return getBukkitPlayer().getLocation().add(0, PrefixerAPI.getHologramY() + ((mainPrefixes.size() - 1) * 0.25), 0);
	}
	
	//
	
	public void setHologram(Hologram hologram) {
		if(hologram == null) return;
		
		PrefixerAPI.getHologramManager().setHologram(playerKey, hologram);
	}
	
	public void removeHologram() {
		Hologram hologram = getHologram();
		if(hologram == null) return;
		
		hologram.delete();
		
		PrefixerAPI.getHologramManager().removeHologram(playerKey);
	}
	
	public boolean hasHologram() {
		return PrefixerAPI.getHologramManager().existsHologram(playerKey);
	}
	
	public Hologram getHologram() {
		return PrefixerAPI.getHologramManager().getHologram(playerKey);
	}
	
}