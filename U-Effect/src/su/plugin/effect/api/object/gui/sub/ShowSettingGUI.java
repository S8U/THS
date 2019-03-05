package su.plugin.effect.api.object.gui.sub;

import org.bukkit.inventory.ItemStack;

import su.plugin.core.bukkit.event.gui.IconClickEvent;
import su.plugin.core.bukkit.gui.GUI;
import su.plugin.core.bukkit.gui.Icon;
import su.plugin.core.bukkit.util.ItemUtil;
import su.plugin.effect.api.category.EffectShape;
import su.plugin.effect.api.category.EffectShow;
import su.plugin.effect.api.object.effect.PlayerEffect;

public class ShowSettingGUI extends GUI {
	
	private PlayerEffect effect;
	
	public ShowSettingGUI(PlayerEffect effect) {
		super("ShowSetting", "보기 설정", 1);
		
		this.effect = effect;
		
		//
		
		int i = 1;
		
		for(EffectShow showSetting : EffectShow.values()) {
			Icon settingIcon = new Icon() {
				private EffectShow effectShow = showSetting;
				
				@Override
				protected ItemStack updateItem() {
					return ItemUtil.makeItem(effect.getShow() == EffectShow.ALWAYS ? "98:3" : "1", "§f§l" + effectShow.getName(), "§f클릭 시 변경됩니다.");
				}
				
				@Override
				public void onIconClick(IconClickEvent e) {
					effect.setShow(effectShow);
					
					// Open Effect Setting GUI
				}
			};
			
			setIcon(i * 2 + 1, 1, settingIcon);
			
			i++;
		}
		
		updateAsynchronously();
	}
	
}