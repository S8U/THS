package su.plugin.antirecipe;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import su.plugin.antirecipe.api.AntiRecipeAPI;
import su.plugin.core.bukkit.api.plugin.UKPlugin;

public class AntiRecipePlugin extends UKPlugin {
	
	@Getter
	private static AntiRecipePlugin instance;
	@Getter
	private static AntiRecipeAPI api = new AntiRecipeAPI();
	
	public void onUEnable() {
		instance = this;
		setPrefix(api.getPluginPrefix());
		
		registerListeners();
		
		onConfigLoad();
	}
	
	public void createConfig() {
		getJsonConfig().addDefault("ban_list", Arrays.asList("322", "322 1"));
		
		getJsonConfig().save();
	}
	
	public void onConfigLoad() {
		createConfig();
		
		List<String> list = getJsonConfig().getStringList("ban_list");
		for(String type : list) {
			ItemStack i;
			if(type.contains(" ")) {
				String[] info = type.split(" ");
				i = new ItemStack(Material.getMaterial(Integer.valueOf(info[0])), 1, Short.valueOf(info[1]));
			} else {
				i = new ItemStack(Integer.valueOf(type));
			}
			api.getBanRecipes().add(i);
		}
		
		log("아이템 목록을 불러왔습니다.");
	}
	
}