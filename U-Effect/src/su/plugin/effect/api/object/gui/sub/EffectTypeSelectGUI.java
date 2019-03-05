package su.plugin.effect.api.object.gui.sub;

import org.bukkit.inventory.ItemStack;

import su.plugin.core.bukkit.event.gui.IconClickEvent;
import su.plugin.core.bukkit.gui.GUI;
import su.plugin.core.bukkit.gui.Icon;
import su.plugin.core.bukkit.util.ItemUtil;
import su.plugin.effect.api.category.EffectShape;
import su.plugin.effect.api.category.EffectType;
import su.plugin.effect.api.object.effect.PlayerEffect;

public class EffectTypeSelectGUI extends GUI {
	
	private PlayerEffect effect;
	
	public EffectTypeSelectGUI(PlayerEffect effect) {
		super("EffectTypeSelect", "형식 선택", 1);
		
		this.effect = effect;
		
		//
		
		Icon projectileIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				return ItemUtil.makeItem(261, "§f§l" + EffectType.PROJECTILE.getName(), "§f클릭 시 변경됩니다.");
			}
			
			@Override
			public void onIconClick(IconClickEvent e) {
				effect.setType(EffectType.PROJECTILE);
				
				// Open Effect Setting GUI
			}
		};
		setIcon(4, 1, projectileIcon);
		
		Icon playerIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				return ItemUtil.makeItem(261, "§f§l" + EffectType.PLAYER.getName(), "§f클릭 시 변경됩니다.");
			}
			
			@Override
			public void onIconClick(IconClickEvent e) {
				effect.setType(EffectType.PLAYER);
				
				// Open Effect Setting GUI
			}
		};
		setIcon(6, 1, playerIcon);
		
		
		updateAsynchronously();
	}
	
}