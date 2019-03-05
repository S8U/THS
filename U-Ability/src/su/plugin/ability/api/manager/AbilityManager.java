package su.plugin.ability.api.manager;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.Ability;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.util.NumberUtil;

public class AbilityManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private HashMap<Integer, Ability> abilities = new HashMap<>();
	@Getter
	private List<String> blackList = new ArrayList<>();
	
	public boolean registerAbility(Ability ability) {
		if(existsAbility(ability.getAbilityId())) return false;
		abilities.put(ability.getAbilityId(), ability);
		return true;
	}
	
	public void unRegisterAbility(int abilityId) {
		abilities.remove(abilityId);
	}
	
	public boolean existsAbility(int abilityId) {
		return abilities.containsKey(abilityId);
	}
	
	public int getConfirmationCount() {
		int t = 0;
		for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
			if(gp.getRedrawCount() < 1) t++;
		}
		return t;
	}
	
	public Ability getAbility(int abilityId) {
		return abilities.get(abilityId);
	}

	public List<Ability> getAbilities(String pluginName) {
		List<Ability> list = new ArrayList<>();

		for(Ability ab : abilities.values()) {
			if(!ab.getPluginName().equalsIgnoreCase(pluginName)) continue;
			list.add(ab);
		}

		return list;
	}
	
	public List<Ability> getAssignedAbilities() { // 할당된 능력
		List<Ability> assigned = new ArrayList<>();
		for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
			if(!gp.hasAbility()) continue;
			for(Ability ability : gp.getAbilities()) {
				assigned.add(ability);
			}
		}
		return assigned;
	}
	
	public Ability getRandomAbility(boolean overlap) {
		Ability ra = abilities.get(NumberUtil.random(0, getAbilities().size() -1));
		
		if(!overlap) {
			for(Ability ab : getAssignedAbilities()) {
				if(ab.getAbilityId() == ra.getAbilityId()) return getRandomAbility(overlap);
			}
		}
		
		return ra;
	}
	
	public void giveRandomAbility(GamePlayer p, boolean overlap) {
		Ability ab = getRandomAbility(overlap);
		p.addAbility(ab);
		p.getAbility(ab.getAbilityId()).setPlayer(p.getPlayer());
	}
	
	public void giveRandomAbilityToAll(boolean overlap) {
		for(GamePlayer p : api.getPlayerManager().getOnlineJoinedPlayers()) {
			giveRandomAbility(p, overlap);
		}
	}
	
	public void addBlackList(Ability ability) {
		addBlackList(ability.getName(), ability.getPluginName());
	}
	
	public void addBlackList(String abilityName, String pluginName) {
		blackList.add(abilityName + "@" + pluginName);
	}
	
	public void removeBlackList(Ability ability) {
		removeBlackList(ability.getName(), ability.getPluginName());
	}
	
	public void removeBlackList(String abilityName, String pluginName) {
		blackList.remove(abilityName + "@" + pluginName);
	}
	
	public boolean isBlackListed(Ability ability) {
		return isBlackListed(ability.getName(), ability.getPluginName());
	}
	
	public boolean isBlackListed(String abilityName, String pluginName) {
		return blackList.contains(abilityName + "@" + pluginName);
	}
	
	public int registerAbilities(JavaPlugin plugin) {
		int i = 0;
		try {
			ZipInputStream zip = new ZipInputStream(new FileInputStream(PluginUtil.getFile(plugin)));
			ZipEntry entry = null;
			while((entry = zip.getNextEntry()) != null) {
				if(entry.isDirectory() || !entry.getName().endsWith(".class")) continue;
				String className = entry.getName().replaceAll("/", ".");
				if(className.startsWith(Ability.class.getPackage().getName())) continue;
				Class<?> c = Class.forName(className.substring(0, className.length() - 6));
				try {
					Ability ability = (Ability) c.newInstance();
					if(ability.getName() == null || ability.isBlackListed()) continue;
					registerAbility(ability);
					i++;
				} catch (Exception ex) {}
			}
			zip.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}

}