package su.plugin.effect.api.object.gui.sub;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import su.plugin.core.bukkit.event.gui.IconClickEvent;
import su.plugin.core.bukkit.gui.GUI;
import su.plugin.core.bukkit.gui.Icon;
import su.plugin.core.bukkit.util.ItemUtil;
import su.plugin.effect.api.category.EffectShape;
import su.plugin.effect.api.object.effect.PlayerEffect;

@Getter
public class EffectShapeSelectGUI extends GUI {
	
	private PlayerEffect effect;
	
	public EffectShapeSelectGUI(PlayerEffect effect) {
		super("EffectShapeSelect", "모양 선택", 1);
		
		this.effect = effect;
		
		//
		
		int i = 1;
		
		for(EffectShape shape : EffectShape.values()) {
			Icon shapeIcon = new Icon() {
				private EffectShape effectShape = shape;
				
				@Override
				protected ItemStack updateItem() {
					return ItemUtil.makeItem(389, "§f§l" + effectShape.getName(), "§f클릭 시 변경됩니다.");
				}
				
				@Override
				public void onIconClick(IconClickEvent e) {
					effect.setShape(effectShape);
					
					// Open Effect Setting GUI
				}
			};
			
			setIcon(i * 2, 1, shapeIcon);
			
			i++;
		}
		
		updateAsynchronously();
	}
	
}