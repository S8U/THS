package su.plugin.effect.api.object.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import su.plugin.core.bukkit.enumeration.InventoryAction;
import su.plugin.core.bukkit.event.gui.IconClickEvent;
import su.plugin.core.bukkit.gui.GUI;
import su.plugin.core.bukkit.gui.Icon;
import su.plugin.core.bukkit.util.ItemUtil;
import su.plugin.effect.api.category.EffectRotate;
import su.plugin.effect.api.category.EffectShape;
import su.plugin.effect.api.category.EffectType;
import su.plugin.effect.api.object.effect.Effect;
import su.plugin.effect.api.object.effect.PlayerEffect;

public class EffectSettingGUI extends GUI {
	
	private Effect effect;
	
	public EffectSettingGUI() {
		super("EffectSetting", "이펙트 설정", 6);
		
		//
		
		ItemStack deco = ItemUtil.getItem("160:7");
		for (int i = 1; i <= 9; i++) {
			setItem(i, 1, deco);
			setItem(i, 6, deco);
			
			if(i == 2) continue;
			setItem(i,  3, deco);
		}
		for (int i = 1; i <= 6; i++) {
			setItem(1, i, deco);
			setItem(3, i, deco);
			setItem(9, i, deco);
		}
		
		//
		
		Icon nameIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				return ItemUtil.makeItem(412, effect.getName(), "§f클릭 시 이펙트 이름을 변경합니다.");
			}
			
			@Override
			public void onIconClick(IconClickEvent e) {
				// Open Effect Name Setting GUI
			}
		};
		setIcon(2, 2, nameIcon);
		
		if(effect instanceof PlayerEffect) {
			Icon toggleIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					return ItemUtil.makeItem(effect.isEnable() ? "251:5" : "251:14", effect.isEnable() ? "§a§l활성화" : "§c§l비활성화", "§f클릭 시 비활성화됩니다.");
				}
				
				@Override
				public void onIconClick(IconClickEvent e) {
					effect.setEnable(!effect.isEnable());
					
					update();
				}
			};
			setIcon(2, 3, toggleIcon);
		} else {
			Icon decoIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					return ItemUtil.makeItem(416, "§f§l전시", "§f클릭 시 현재 위치에 이펙트를 전시합니다.");
				}
				
				@Override
				public void onIconClick(IconClickEvent event) {
					// Deco Effect
				}
			};
		}
		
		Icon typeIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				List<String> lore = new ArrayList<>();
				lore.add("§a현재 형식: §f" + (effect.getType() == EffectType.PLAYER ? "플레이어" : "투사체"));
				
				if(effect instanceof PlayerEffect) {
					lore.add("§f");
					lore.add("§f클릭 시 형식을 선택합니다.");
				}
				
				return ItemUtil.makeItem(effect.getType() == EffectType.PLAYER ? 299 : 261, "§f§l형식", (String[]) lore.toArray());
			}
			
			@Override
			public void onIconClick(IconClickEvent e) {
				if(!(effect instanceof PlayerEffect)) return;
				// Open Type Select GUI
			}
		};
		setIcon(2, 5, typeIcon);
		
		Icon particleIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				return ItemUtil.makeItem(401, "§f§l파티클",
						"§a현재 파티클: §f" + effect.getParticle().getName(),
						"§f",
						"§f클릭 시 파티클을 선택합니다.");
			}
			
			@Override
			public void onIconClick(IconClickEvent e) {
				// Open Particle Select GUI
			}
		};
		
		if(effect.getType() == EffectType.PLAYER) {
			setIcon(5, 2, particleIcon);
			
			PlayerEffect pe = (PlayerEffect) effect;
			
			Icon shapeIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					return ItemUtil.makeItem(389, "§f§l모양",
							"§a현재 모양: §f" + pe.getShape().getName(),
							"§f",
							"§f클릭 시 모양을 선택합니다.");
				}
				
				@Override
				public void onIconClick(IconClickEvent event) {
					// Open Shape Select GUI
				}
			};
			setIcon(6, 2, shapeIcon);
			
			Icon showSettingIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					return ItemUtil.makeItem("98:3", "§f§l보기 설정",
							"§a현재 설정: §f" + pe.getShow().getName(),
							"§f",
							"§f클릭 시 보기 설정을 변경합니다.");
				}
				
				@Override
				public void onIconClick(IconClickEvent event) {
					// Open Show Setting GUI
				}
			};
			setIcon(7, 2, showSettingIcon);
			
			Icon sizeIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					return ItemUtil.makeItem(138, "§f§l크기",
							"§a현재 크기: §f" + pe.getSize(),
							"§f",
							"§a좌클릭: §f+1",
							"§a우클릭: §f-1");
				}
				
				@Override
				public void onIconClick(IconClickEvent e) {
					pe.setSize(pe.getSize() + (e.getGUIClickEvent().getAction() == InventoryAction.LEFT_HOLD ? 1 : (e.getGUIClickEvent().getAction() == InventoryAction.RIGHT_HOLD ? -1 : 0)));
					
					update();
				}
			};
			
			Icon heightIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					return ItemUtil.makeItem(138, "§f§l높이",
							"§a현재 높이: §f" + pe.getHeight(),
							"§f",
							"§a좌클릭: §f+1",
							"§a우클릭: §f-1");
				}
				
				@Override
				public void onIconClick(IconClickEvent e) {
					pe.setHeight(pe.getHeight() + (e.getGUIClickEvent().getAction() == InventoryAction.LEFT_HOLD ? 1 : (e.getGUIClickEvent().getAction() == InventoryAction.RIGHT_HOLD ? -1 : 0)));
					
					update();
				}
			};
			
			if(pe.getShape() == EffectShape.WING) {
				setIcon(5, 4, sizeIcon);
				setIcon(7, 4, heightIcon);
				
				Icon wingSettingIcon = new Icon() {
					@Override
					protected ItemStack updateItem() {
						return ItemUtil.makeItem(138, "§f§l날개 모양 설정",
								"§f클릭 시 설정합니다.");
					}
					
					@Override
					public void onIconClick(IconClickEvent event) {
						// Open Wing Setting GUI
					}
				};
			} else {
				setIcon(4, 4, sizeIcon);
				setIcon(6, 4, heightIcon);
				
				Icon amountIcon = new Icon() {
					@Override
					protected ItemStack updateItem() {
						return ItemUtil.makeItem(138, "§f§l개수",
								"§a현재 개수: §f" + pe.getAmount(),
								"§f",
								"§a좌클릭: §f+1",
								"§a우클릭: §f-1");
					}
					
					@Override
					public void onIconClick(IconClickEvent e) {
						pe.setAmount(pe.getAmount() + (e.getGUIClickEvent().getAction() == InventoryAction.LEFT_HOLD ? 1 : (e.getGUIClickEvent().getAction() == InventoryAction.RIGHT_HOLD ? -1 : 0)));
						
						update();
					}
				};
				setIcon(8, 4, amountIcon);
				
				Icon rotateDirectionIcon = new Icon() {
					@Override
					protected ItemStack updateItem() {
						return ItemUtil.makeItem(138, "§f§l회전 방향",
								"§a현재 방향: §f" + pe.getRotate().getName(),
								"§f",
								"§a좌클릭: §f방향 변경",
								"§a우클릭: §f사용 안함");
					}
					
					@Override
					public void onIconClick(IconClickEvent e) {
						pe.setRotate(e.getGUIClickEvent().getAction() == InventoryAction.LEFT_HOLD ? (pe.getRotate() == EffectRotate.LEFT ? EffectRotate.RIGHT : EffectRotate.LEFT) : (e.getGUIClickEvent().getAction() == InventoryAction.RIGHT_HOLD ? EffectRotate.NONE : pe.getRotate()));
						
						update();
					}
				};
				setIcon(6, 5, rotateDirectionIcon);
				
				Icon rotateSpeedIcon = new Icon() {
					@Override
					protected ItemStack updateItem() {
						return ItemUtil.makeItem(138, "§f§l회전 속도",
								"§a현재 속도: §f" + pe.getRotateSpeed(),
								"§f",
								"§a좌클릭: §f+1",
								"§a우클릭: §f-1");
					}
					
					@Override
					public void onIconClick(IconClickEvent e) {
						pe.setRotateSpeed(pe.getRotateSpeed() + (e.getGUIClickEvent().getAction() == InventoryAction.LEFT_HOLD ? 1 : (e.getGUIClickEvent().getAction() == InventoryAction.RIGHT_HOLD ? -1 : 0)));
						
						update();
					}
				};
				setIcon(7, 5, rotateSpeedIcon);
			}
		} else {
			setIcon(6, 3, particleIcon);
		}
		
		//
		
		updateAsynchronously();
	}
	
}