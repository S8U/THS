package su.plugin.core.bukkit.api.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.KCore;

@Getter
public class QuickBar {
	
	private HashMap<Integer, ItemStack> items = new HashMap<>();
	
	private HashMap<Integer, Icon> icons = new HashMap<>();
	
	public void setItem(int x, ItemStack item) {
		items.put(x, item);
	}
	
	public ItemStack getItem(int x) {
		return items.get(x);
	}
	
	public void setIcon(int x, Icon icon) {
		icons.put(x, icon);
	}
	
	public Icon getIcon(int x) {
		return icons.get(x);
	}
	
	public void updateIcon(int x) {
		Icon icon = getIcon(x);
		if(icon == null) return;

		icon.update();
		setItem(x, icon.getItem());

		for(Player ap : getPlayers()) {
			setTo(ap);
		}
	}
	
	protected void onUpdate() { }
	
	public void update() {
		onUpdate();
		
		for(int x : icons.keySet()) {
			updateIcon(x);
		}
	}
	
	public void setTo(Player p) {
		KCore.getGUIManager().setQuickBar(p, this);
		
		p.getInventory().clear();
		
		for(int i = 0; i < 9; i++) {
			p.getInventory().setItem(i, items.get(i + 1));
		}

		p.updateInventory();
	}

	public List<Player> getPlayers() {
		List<Player> list = new ArrayList<>();

		KCore.getGUIManager().getQuickBars().forEach((k, v) -> {
			if(!v.equals(this)) return;

			Player p = Bukkit.getPlayer(k);
			if(p == null) return;

			list.add(p);
		});

		return list;
	}
	
}