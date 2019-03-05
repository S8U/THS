package su.plugin.core.bukkit.api.event.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.enumeration.InventoryAction;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;

public class GUIClickEvent extends UKEvent {
	
	@Setter
	@Getter
	private boolean pickUpCancel = true;
	
	@Getter
	private final Player player;
	
	@Getter
	private final InventoryAction action;
	
	@Getter
	private GUI GUI;
	
	@Getter
	private InventoryClickEvent inventoryClickEvent;
	
	public GUIClickEvent(InventoryClickEvent inventoryClickEvent) {
		this.inventoryClickEvent = inventoryClickEvent;
		player = (Player) inventoryClickEvent.getWhoClicked();
		switch(inventoryClickEvent.getAction()) {
		case PICKUP_ALL:
			action = InventoryAction.LEFT_HOLD; break;
		case PLACE_ALL:
			action = InventoryAction.LEFT_PLACE; break;
		case PICKUP_HALF:
			action = InventoryAction.RIGHT_HOLD; break;
		case PLACE_ONE:
			action = InventoryAction.RIGHT_PLACE; break;
		case MOVE_TO_OTHER_INVENTORY:
			action = InventoryAction.SHIFT_CLICK; break;
		case COLLECT_TO_CURSOR:
			action = InventoryAction.DOUBLE_CLICK; break;
		case CLONE_STACK:
			action = InventoryAction.WHEEL; break;
		case DROP_ONE_SLOT:
			action = InventoryAction.DROP; break;
		case NOTHING:
			action = InventoryAction.NOTHING; break;
		default:
			action = InventoryAction.OTHER; break;
		}
		GUI = KCore.getGUIManager().getPlayerGUI(player);
	}
	
	public int getClickedX() {
		return inventoryClickEvent.getSlot() % 9 + 1;
	}
	
	public int getClickedY() {
		return inventoryClickEvent.getSlot() / 9 + 1;
	}
	
	public int getClickedSlot() {
		return inventoryClickEvent.getSlot();
	}
	
	public ItemStack getClickedItem() {
		return GUI.getInventory().getItem(getClickedSlot());
	}
	
	public Icon getClickedIcon() {
		return GUI.getIcon(getClickedSlot());
	}
	
	public boolean isIconClicked() {
		return getClickedIcon() != null;
	}
	
}