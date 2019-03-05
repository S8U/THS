package su.plugin.antirecipe.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AntiRecipeAPI {
	
	private final String pluginPrefix = ChatColor.RED + "[ U-AntiRecipe ]" + ChatColor.WHITE;
	
	@Setter
	private List<ItemStack> banRecipes = new ArrayList<>();
	
	public boolean isBannedItem(ItemStack i) {
		for(ItemStack banitem : banRecipes) {
			if(i.equals(banitem)) return true;
		}
		return false;
	}
	
}