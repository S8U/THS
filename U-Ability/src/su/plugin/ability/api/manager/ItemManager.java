package su.plugin.ability.api.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.event.RankItemGiveEvent;
import su.plugin.ability.api.event.StartItemGiveEvent;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.lib.VaultHandler;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.permission.api.PermissionAPI;

public class ItemManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Setter
	@Getter
	private int startLevel;
	
	@Setter
	@Getter
	private ItemStack startHelmet, startChestplate, startLeggings, startBoots;
	
	@Setter
	@Getter
	private List<ItemStack> startItems = new ArrayList<>();
	
	@Setter
	@Getter
	private HashMap<String, List<ItemStack>> rankItems = new HashMap<>();
	
	public void setRankItem(String rank, List<ItemStack> items) {
		rankItems.put(rank.toLowerCase(), items);
	}
	
	public boolean existsRankItem(String rank) {
		return rankItems.containsKey(rank.toLowerCase());
	}
	
	public List<ItemStack> getRankItemList(String rank) {
		return rankItems.get(rank.toLowerCase());
	}
	
	public boolean hasRankItemGroup(Player p) {
		if(api.isUsePermission()) {
			return existsRankItem(PermissionAPI.getPlayerManager().getPermissionPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p)).getGroupName());
		}

		String[] group = VaultHandler.getChat().getPlayerGroups(p);
		for(String name : group) {
			if(existsRankItem(name)) return true;
		}

		return false;
	}
	
	public void giveRankItem(Player p) {
		if(!hasRankItemGroup(p)) return;
		
		RankItemGiveEvent event = new RankItemGiveEvent(p);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return;

		if(api.isUsePermission()) {
			for(ItemStack item : getRankItemList(PermissionAPI.getPlayerManager().getPermissionPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(p)).getGroupName())) {
				p.getInventory().addItem(item);
			}
		} else {
			String[] group = VaultHandler.getChat().getPlayerGroups(p);
			for(int i = 0; i < group.length; i++) {
				for(ItemStack item : getRankItemList(group[i])) {
					p.getInventory().addItem(item);
				}
			}
		}

		p.updateInventory();
	}
	
	public void giveRankItemAll() {
		for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
			giveRankItem(gp.getPlayer());
		}
	}
	
	public void addStartItem(ItemStack item) {
		startItems.add(item);
	}
	
	public void giveStartItem(Player p) {
		Bukkit.getScheduler().runTask(AbilityPlugin.getInstance(), () -> {
			StartItemGiveEvent event = new StartItemGiveEvent(p);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()) return;
			
			p.setLevel(startLevel);
			
			p.getInventory().setHelmet(startHelmet);
			p.getInventory().setChestplate(startChestplate);
			p.getInventory().setLeggings(startLeggings);
			p.getInventory().setBoots(startBoots);
			
			
			for(ItemStack item : startItems) {
				p.getInventory().addItem(item);
			}
			
			p.updateInventory();
		});
	}
	
	public void giveStartItemAll() {
		for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
			giveStartItem(gp.getPlayer());
		}
	}
	
}