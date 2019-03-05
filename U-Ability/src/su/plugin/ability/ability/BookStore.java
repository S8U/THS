package su.plugin.ability.ability;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.object.Ability;
import su.plugin.core.bukkit.api.util.ItemUtil;

public class BookStore extends Ability implements Listener {
	
	public BookStore() {
		initAbility("북스토어", AbilityType.PASSIVE, AbilityRank.A,
				"가지고 있는 책 10권 당 공격력 1이 추가됩니다.",
				"추가 공격력은 최대 5로 제한됩니다.");
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(getPlayer() == null || !((Player) e.getDamager()).getName().equalsIgnoreCase(getPlayerKey().getName())) return;
		int add = (int) (ItemUtil.getItemAmount(getPlayer().getInventory(), new ItemStack(Material.BOOK)) * 0.1);
		if(add > 5) {
			add = 5;
		}
		e.setDamage(e.getDamage() + add);
	}
	
}