package su.plugin.effect.api.object.gui.sub;

import org.bukkit.inventory.ItemStack;

import su.plugin.core.bukkit.gui.GUI;
import su.plugin.core.bukkit.gui.Icon;
import su.plugin.effect.api.object.effect.PlayerEffect;

public class WingSettingGUI extends GUI {
	
	private PlayerEffect effect;
	
	public WingSettingGUI(PlayerEffect effect) {
		super("WingSetting", "날개 모양 설정", 6);
		
		this.effect = effect;
		
		//
		
		for (int i = 0; i < 54; i++) {
			Icon settingIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					return null;
				}
			};
			setIcon(i, settingIcon);
		}
	}
	
}