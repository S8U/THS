package su.plugin.core.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.gui.GUIClickEvent;
import su.plugin.core.bukkit.api.event.gui.GUICloseEvent;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.event.gui.QuickBarClickEvent;
import su.plugin.core.bukkit.api.gui.GUI;

public class GUIListener implements Listener {
	
	private KCore api = KCorePlugin.getApi();
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getRawSlot() < 0 || e.getRawSlot() > e.getInventory().getSize()) return;
		
		Player p = (Player) e.getWhoClicked();
		
		if(api.getGUIManager().hasGUI(p.getName())) {
			GUI gui = api.getGUIManager().getPlayerGUI(p);
			
			GUIClickEvent ge = new GUIClickEvent(e);
			ge.setPickUpCancel(!gui.isCanPickUp());
			Bukkit.getPluginManager().callEvent(ge);
			
			if(ge.isIconClicked()) {
				IconClickEvent ie = new IconClickEvent(ge, null);
				ie.getIcon().onIconClick(ie);
				Bukkit.getPluginManager().callEvent(ie);
				
				ge.setPickUpCancel(ie.isPickUpCancel());
			}
			
			e.setCancelled(ge.isPickUpCancel());
		} else if(e.getSlotType() == SlotType.QUICKBAR && api.getGUIManager().hasQuickBar(p.getName())) {
			e.setCancelled(true);
			
			p.updateInventory();
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		
		if(!api.getGUIManager().hasGUI(p.getName())) return;
		
		GUICloseEvent event = new GUICloseEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		
		api.getGUIManager().removePlayerGUI(p);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.PHYSICAL || !api.getGUIManager().hasQuickBar(e.getPlayer())) return;
		
		QuickBarClickEvent qe = new QuickBarClickEvent(e);
		Bukkit.getPluginManager().callEvent(qe);
		
		if(qe.isIconClicked()) {
			IconClickEvent ie = new IconClickEvent(null, qe);
			ie.getIcon().onIconClick(ie);
			Bukkit.getPluginManager().callEvent(ie);
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if(!api.getGUIManager().hasQuickBar(e.getPlayer())) return;
		
		api.getGUIManager().getQuickBar(e.getPlayer()).setTo(e.getPlayer());
		e.getItemDrop().remove();
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if(!api.getGUIManager().hasQuickBar(e.getPlayer())) return;
		
		e.setCancelled(true);
	}
	
}