package su.plugin.itemtools.api;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.util.ItemUtil;

public class ItemToolsAPI {
	
	@Getter
	private static List<String> enchantPlayers = new ArrayList<>();
	@Getter
	private static List<String> anvilPlayers = new ArrayList<>();
	@Getter
	private static List<String> workbenchPlayers = new ArrayList<>();
	
	@Getter
	private static ItemStack enchantTool, workbenchTool, anvilTool;
	
	public void makeItem() {
		enchantTool = ItemUtil.makeItem(116, "§6[도구] §f인챈트 테이블", "§6우클릭 시 인챈트 테이블이 열립니다.");
		workbenchTool = ItemUtil.makeItem(58, "§6[도구] §f작업대", "§6우클릭 시 작업대가 열립니다.");
		anvilTool = ItemUtil.makeItem(145, "§6[도구] §f모루", "§6우클릭 시 모루가 열립니다.");
	}

	public static double getArmourPoint(LivingEntity entity) {
		double total = 0;

		for(ItemStack armour : entity.getEquipment().getArmorContents()) {
			if(armour == null) continue;
			if(!armour.hasItemMeta() || !armour.getItemMeta().hasLore()) continue;

			for(String line : armour.getItemMeta().getLore()) {
				if(!line.startsWith("§r§3§r§9추가 방어력: §f")) continue;

				double point = Double.parseDouble(line.substring("§r§3§r§9추가 방어력: §f".length(), line.length()));

				total += point;
				break;
			}
		}

		return total;
	}

	public static Double getAttackDamagePoint(LivingEntity entity) {
		ItemStack item = ((Player) entity).getItemInHand();
		if(item == null) return null;

		if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
			for(String line : item.getItemMeta().getLore()) {
				if(!line.startsWith("§r§3§r§c추가 공격력: §f")) continue;

				return Double.parseDouble(line.substring("§r§3§r§c추가 공격력: §f".length(), line.length()));
			}
		}

		return null;
	}

}
