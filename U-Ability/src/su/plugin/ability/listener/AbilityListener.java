package su.plugin.ability.listener;

import java.util.HashMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.ClickType;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.object.Ability;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.util.ItemUtil;

public class AbilityListener implements Listener {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private static HashMap<Integer, ItemStack[]> leftEvents = new HashMap<>();
	@Getter
	private static HashMap<Integer, ItemStack[]> rightEvents = new HashMap<>();
	
	@EventHandler
	public void onUseAbility(PlayerInteractEvent e) {
		if(api.getGameManager().getGameState().getProgress() < GameState.PLAYING.getProgress() || api.isInvincibilityTime()) return;
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		ItemStack item = p.getItemInHand();
		Action a = e.getAction();
		if(a.equals(Action.LEFT_CLICK_AIR) || a.equals(Action.LEFT_CLICK_BLOCK)) {
			for(Ability ab : gp.getAbilities()) {
				if(!leftEvents.containsKey(ab.getAbilityId()) || !containsItem(leftEvents.get(ab.getAbilityId()), item)) continue;
				ab.excute(e, item, ClickType.LEFT);
			}
		} else if(a.equals(Action.RIGHT_CLICK_AIR) || a.equals(Action.RIGHT_CLICK_BLOCK)) {
			for(Ability ab : gp.getAbilities()) {
				if(!rightEvents.containsKey(ab.getAbilityId()) || !containsItem(rightEvents.get(ab.getAbilityId()), item)) continue;
				ab.excute(e, item, ClickType.RIGHT);
			}
		}
	}
	
	private boolean containsItem(ItemStack[] items, ItemStack item) {
		if(items == null || item == null) return false;
		for(ItemStack i : items) {
			if(ItemUtil.equalsItem(i, item)) return true;
		}
		return false;
	}
	
}