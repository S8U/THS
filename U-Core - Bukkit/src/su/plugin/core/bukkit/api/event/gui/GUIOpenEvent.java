package su.plugin.core.bukkit.api.event.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

import lombok.Getter;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.core.bukkit.api.gui.GUI;

public class GUIOpenEvent extends UKEvent {
	
	@Getter
	private final Player player;
	
	@Getter
	private final InventoryOpenEvent inventoryOpenEvent;
	
	@Getter
	private GUI GUI;
	
	public GUIOpenEvent(InventoryOpenEvent inventoryOpenEvent) {
		this.inventoryOpenEvent = inventoryOpenEvent;
		player = (Player) inventoryOpenEvent.getPlayer();
		GUI = KCore.getGUIManager().getPlayerGUI(player);
	}
	
}