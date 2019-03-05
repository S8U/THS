package su.plugin.effect.api.object.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.event.gui.IconClickEvent;
import su.plugin.core.bukkit.gui.GUI;
import su.plugin.core.bukkit.gui.Icon;
import su.plugin.core.bukkit.util.ItemUtil;
import su.plugin.effect.api.category.EffectShape;
import su.plugin.effect.api.category.EffectType;
import su.plugin.effect.api.object.effect.Effect;
import su.plugin.effect.api.object.effect.PlayerEffect;

@Setter
@Getter
public class EffectListGUI extends GUI {
	
	private int page = 1;
	
	private List<Effect> effects = new ArrayList<>();
	
	public EffectListGUI() {
		super("EffectList", "이펙트 목록", 6);
		
		//
		
		for(int i = 0; i < 36; i++) {
			int num = (page - 1) * 36 + i;
			
			Icon effectIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					Effect effect = effects.get(num);
					if(effect == null) return null;
					
					List<String> lore = new ArrayList<>();
					
					lore.add("§a§l상태: §f" + (effect.isEnable() ? "" : "비") + "활성화");
					lore.add("§f");
					lore.add("§a§l형식: §f" + effect.getType().getName());
					lore.add("§f");
					lore.add("§a§l파티클: §f" + effect.getParticle().getName());
					
					if(effect.getType() == EffectType.PLAYER) {
						PlayerEffect pe = (PlayerEffect) effect;
						lore.add("§a§l모양: §f" + pe.getShape().getName());
						lore.add("§a§l크기: §f" + pe.getSize());
						lore.add("§a§l높이: §f " + pe.getHeight());
						
						if(pe.getShape() == EffectShape.WING) {
							lore.add("§a§l날개 모양");
							for(String line : pe.getWingShape()) {
								lore.add(line);
							}
						} else {
							lore.add("§a§l파티클 개수: §f " + pe.getAmount());
							lore.add("§a§l회전 방향: §f" + pe.getRotate().getName());
							lore.add("§a§l회전 속도: §f" + pe.getRotateSpeed());
						}
					} else {
						// Projectile
					}
					
					return ItemUtil.makeItem("299", effect.getName(), (String[]) lore.toArray());
				}
				
				@Override
				public void onIconClick(IconClickEvent e) {
					// Open Effect Setting GUI
				}
			};
			setIcon(i, effectIcon);
		}
		
		//
		
		ItemStack deco = ItemUtil.getItem("160:7");
		for (int i = 1; i <= 9; i++) {
			setItem(i, 5, deco);
		}
		
		//
		
		Icon createIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				return ItemUtil.makeItem("58", "§a§l이펙트 생성");
			}
		};
		setIcon(1, 6, createIcon);
		
		Icon deleteIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				return ItemUtil.makeItem("327", "§a§l이펙트 삭제");
			}
		};
		setIcon(9, 6, deleteIcon);
		
		//
		
		Icon previousIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				return ItemUtil.makeItem("339", "§a§l이전");
			}
			
			@Override
			public void onIconClick(IconClickEvent e) {
				if(page < 2) return;
				
				page--;
				update();
			}
		};
		setIcon(4, 6, previousIcon);
		
		Icon nextIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				return ItemUtil.makeItem("339", "§a§l다음");
			}
			
			@Override
			public void onIconClick(IconClickEvent e) {
				if(page >= getMaxPage()) return;
				
				page++;
				updateAsynchronously();
			}
		};
		setIcon(6, 6, nextIcon);
		
		Icon pageIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				return ItemUtil.makeItem("340", "§a§l[ " + page + " / " + getMaxPage() + " ]");
			}
		};
		setIcon(5, 6, pageIcon);
		
		//
		
		updateAsynchronously();
	}
	
	public int getMaxPage() {
		return (int) Math.ceil(effects.size() / 36) + 1;
	}
	
}