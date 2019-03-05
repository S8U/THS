package su.plugin.itemtools.api;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.util.ItemUtil;

public class ItemToolsAPI {
	
	@Getter
	private static String pluginPrefix = ChatColor.GOLD + "[ U-ItemTools ]" + ChatColor.WHITE;
	
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

}
