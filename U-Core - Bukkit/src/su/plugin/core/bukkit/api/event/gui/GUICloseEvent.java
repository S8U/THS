package su.plugin.core.bukkit.api.event.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import lombok.Getter;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.core.bukkit.api.gui.GUI;

public class GUICloseEvent extends UKEvent {
	
	@Getter
	private final Player player;
	
	@Getter
	private final InventoryCloseEvent inventoryCloseEvent;
	
	@Getter
	private GUI GUI;
	
	public GUICloseEvent(InventoryCloseEvent inventoryCloseEvent) {
		this.inventoryCloseEvent = inventoryCloseEvent;
		player = (Player) inventoryCloseEvent.getPlayer();
		GUI = KCore.getGUIManager().getPlayerGUI(player);
	}

}