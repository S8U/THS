package su.plugin.core.bukkit.api.gui;

import java.util.HashMap;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;

@Getter
@RequiredArgsConstructor
public abstract class Icon {
	
	private final String key;
	
	@Setter
	private ItemStack item;
	
	private HashMap<String, Object> objects = new HashMap<>();
	
	// Constructor
	public Icon() {
		key = null;
	}
	
	public Icon(ItemStack item) {
		key = null;
		this.item = item;
	}
	
	public Icon(String key, ItemStack item) {
		this.key = key;
		this.item = item;
	}
	
	// Update
	protected abstract ItemStack updateItem();
	
	public void update() {
		item = updateItem();
	}
	
	// Amount
	public void setAmount(int amount) {
		item.setAmount(amount);
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
	public void onIconClick(IconClickEvent event) { }
	
}