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
import su.plugin.core.common.api.player.UPlayer;

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
	
	public GUI(String key, String title, int row) {
		this.key = key;
		this.title = title;
		this.row = row;
		inventory = Bukkit.createInventory(null, row * 9, ChatColor.translateAlternateColorCodes('&', title));
	}
	
	// Item
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
	
	// Icon
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
	
	// Update
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

	public void updateFakeIcons(UPlayer up) {
		icons.values().forEach(icon -> {
			if (!(icon instanceof FakeIcon)) return;

			((FakeIcon) icon).update(up);
		});
	}

	public void updateFakeIcons(Player p) {
		updateFakeIcons(KCore.getUPlayerByPlatformPlayer(p));
	}

	public void updateFakeIcons() {
		List<Player> players = getPlayers();
		icons.values().forEach(icon -> {
			if (!(icon instanceof FakeIcon)) return;

			players.forEach(player -> {
				((FakeIcon) icon).update(player);
			});
		});
	}
	
	// Open
	public void open(Player p) {
		p.closeInventory();
		
		KCore.getGUIManager().setPlayerGUI(p, this);
		
		Bukkit.getScheduler().runTask(KCorePlugin.getInstance(), () -> {
			p.openInventory(inventory);

			icons.values().forEach(icon -> {
				if (!(icon instanceof FakeIcon)) return;

				((FakeIcon) icon).update(p);
			});
		});
	}

	public void closeAll() {
		getPlayers().forEach(p -> p.closeInventory());
	}
	
	// Object
	public void setObject(String key, Object value) {
		objects.put(key, value);
	}
	
	public boolean existsObject(String key) {
		return objects.containsKey(key);
	}
	
	public Object getObject(String key) {
		return objects.get(key);
	}

	// Event
	public void onGUIClick(GUIClickEvent e) { }

	//
	public List<Player> getPlayers() {
		List<Player> list = new ArrayList<>();

		for(Player ap : KCore.getOnlinePlayers()) {
			GUI gui = KCore.getGUIManager().getPlayerGUI(ap);
			if(gui == null || gui != this) continue;

			list.add(ap);
		}

		return list;
	}
	
}