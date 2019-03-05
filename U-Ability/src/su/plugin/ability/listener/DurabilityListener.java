package su.plugin.ability.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;

public class DurabilityListener implements Listener {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(!api.isUseInfinityDurability() || !(e.getEntity() instanceof Player)) return;
		
		Player p = (Player) e.getEntity();
		
		for(ItemStack item : p.getInventory().getArmorContents()) {
			if(item == null || item.getType().getMaxDurability() == 0) continue;
			item.setDurability((short) 0);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(!api.isUseInfinityDurability()) return;
		
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();
		
		if(item == null || item.getType().getMaxDurability() == 0) return;
		item.setDurability((short) 0);
	}
	
}