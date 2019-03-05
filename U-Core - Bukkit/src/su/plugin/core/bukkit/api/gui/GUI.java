package su.plugin.core.bukkit.api.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.gui.GUIClickEvent;

@Getter
public class GUI {
	
	private final String title, key;
	
	private final int row;
	
	private boolean canPickUp;
	
	private Inventory inventory;
	
	@Setter
	private HashMap<Integer, Icon> icons = new HashMap<>();
	
	@Setter
	private HashMap<String, Object> objects = new HashMap<>();
	
	//
	
	public GUI(String key, Inventory inventory) {
		this.key = key;
		this.inventory = inventory;
		title = inventory.getTitle();
		row = inventory.getSize() / 9;
	}
	
	public GUI(String key, String title, int row) {
		this.key = key;
		this.title = title;
		this.row = row;
		inventory = Bukkit.createInventory(null, row * 9, ChatColor.translateAlternateColorCodes('&', title));
	}
	
	//
	
	public void setItem(int x, int y, ItemStack item) {
		inventory.setItem(x - 1 + (y - 1) * 9, item);
	}
	
	public void setItem(int pos, ItemStack item) {
		inventory.setItem(pos, item);
	}
	
	public ItemStack getItem(int x, int y) {
		return inventory.getItem(x - 1 + (y - 1) * 9);
	}
	
	public ItemStack getItem(int pos) {
		return inventory.getItem(pos);
	}
	
	//
	
	public void setIcon(int x, int y, Icon icon) {
		setIcon(x - 1 + (y - 1) * 9, icon);
	}
	
	public void setIcon(int pos, Icon icon) {
		if(pos >= inventory.getSize()) throw new ArrayIndexOutOfBoundsException("인벤토리 밖에는 아이콘을 설정할 수 없습니다. (pos:" + pos + ")");
		icons.put(pos, icon);
	}
	
	public Icon getIcon(int x, int y) {
		return icons.get(x - 1 + (y - 1) * 9);
	}
	
	public Icon getIcon(int pos) {
		return icons.get(pos);
	}
	
	//
	
	protected void onUpdate() { }
	
	public void update() {
		onUpdate();
		
		for(int pos : icons.keySet()) {
			updateIcon(pos);
		}
	}
	
	public void updateAsynchronously() {
		Bukkit.getScheduler().runTaskAsynchronously(KCorePlugin.getInstance(), () -> update());
	}
	
	public void updateIcon(int x, int y) {
		updateIcon(x - 1 + (y - 1) * 9);
	}
	
	public void updateIcon(int pos) {
		Icon icon = getIcon(pos);
		if(icon == null) return;
		icon.update();
		setItem(pos, icon.getItem());
	}
	
	//
	
	public void open(Player p) {
		p.closeInventory();
		
		KCore.getGUIManager().setPlayerGUI(p, this);
		
		Bukkit.getScheduler().runTask(KCorePlugin.getInstance(), () -> p.openInventory(inventory));
	}

	public void closeAll() {
		getPlayers().forEach(p -> p.closeInventory());
	}

	public List<Player> getPlayers() {
		List<Player> list = new ArrayList<>();

		for(Player ap : KCore.getOnlinePlayers()) {
			GUI gui = KCore.getGUIManager().getPlayerGUI(ap);
			if(gui == null || gui != this) continue;

			list.add(ap);
		}

		return list;
	}
	
	//
	
	public void onGUIClick(GUIClickEvent e) { }
	
	//
	
	public void setObject(String key, Object value) {
		objects.put(key, value);
	}
	
	public boolean existsObject(String key) {
		return objects.containsKey(key);
	}
	
	public Object getObject(String key) {
		return objects.get(key);
	}
	
}