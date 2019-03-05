package su.plugin.ability.api.util;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.object.GamePlayer;

public class BuildUtil {
	
	public static String buildGamePlayerList(List<GamePlayer> players) {
		StringBuilder sb = new StringBuilder();
		if(players.size() < 1) {
			return "없음";
		}
		for(GamePlayer p : players) {
			if(sb.length() < 1) {
				sb.append(p.getDisplayName());
			} else {
				sb.append(", " + p.getDisplayName());
			}
		}
		return sb.toString();
	}
	
	public static String buildPlayerList(List<Player> players) {
		StringBuilder sb = new StringBuilder();
		if(players.size() < 1) {
			return "없음";
		}
		for(Player p : players) {
			if(sb.length() < 1) {
				sb.append(p.getName());
			} else {
				sb.append(", " + p.getName());
			}
		}
		return sb.toString();
	}
	
	public static String buildItemListString(List<ItemStack> itemlist) {
		StringBuilder sb = new StringBuilder();
		if(itemlist.size() < 1) {
			return "없음";
		}
		for(ItemStack item : itemlist) {
			String info = item.getType().toString() + " x " + item.getAmount();
			if(sb.length() < 1) {
				sb.append(info);
			} else {
				sb.append(", " + info);
			}
		}
		return sb.toString();
	}
	
}