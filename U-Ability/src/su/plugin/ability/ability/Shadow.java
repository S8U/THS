package su.plugin.ability.ability;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.ClickType;
import su.plugin.ability.api.object.Ability;
import su.plugin.core.bukkit.api.enumeration.Particle;

public class Shadow extends Ability {
	
	public Shadow() {
		initAbility("그림자", AbilityType.ACTIVE_CONTINUE, AbilityRank.A,
				"3초 간 그림자가 됩니다.",
				"그림자 상태에는 상대에게 공격받지 않습니다.");
		setCoolTime(30);
		setDurationTime(3);
		
		registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
		registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
	}
	
	@Override
	public void onResign() {
		getGamePlayer().getKPlayer().hidePlayer();
		stopEffectTask();
	}
	
	@Override
	public void onUseCastingItem(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {
		getGamePlayer().getKPlayer().showPlayer();
		startEffectTask(1);
	}
	
	public void onEffect() {
		Particle.SMOKE_LARGE.spawn(getPlayer().getLocation(), 1, 1);
	}
	
	public void onDurationEnd() {
		getGamePlayer().getKPlayer().hidePlayer();
		stopEffectTask();
	}

}