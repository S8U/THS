package su.plugin.core.bukkit.listener;

import java.util.HashMap;
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
import org.bukkit.event.player.PlayerQuitEvent;
import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.gui.GUIClickEvent;
import su.plugin.core.bukkit.api.event.gui.GUICloseEvent;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.event.gui.QuickBarClickEvent;
import su.plugin.core.bukkit.api.gui.FakeIcon;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.PageableGUI;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.DebugUtil;

public class GUIListener implements Listener {
	
	private KCore api = KCorePlugin.getApi();

	private HashMap<String, Long> lastClick = new HashMap<>();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (DebugUtil.isDebugMode(PlayerKey.getPlayerKeyByPlatformPlayer(e.getWhoClicked()).getId())) {
			DebugUtil.log("getRawSlot: " + e.getRawSlot() + " / getSlot: " + e.getSlot() + " / Inv Size: " + e.getInventory().getSize() + " / Action: " + e.getAction());
		}
		
		Player p = (Player) e.getWhoClicked();
		
		if(api.getGUIManager().hasGUI(p.getName())) {
			GUI gui = api.getGUIManager().getPlayerGUI(p);

			if(e.getRawSlot() < 0 || e.getRawSlot() > e.getInventory().getSize()) {
				e.setCancelled(true);

				if (p.isOp() || p.hasPermission("core.admin")) {
					Long last = lastClick.get(p.getName());
					if (last != null && System.currentTimeMillis() - last < 500) {
						gui.getInventory().clear();
						gui.updateAsynchronously();

						Core.msg(p, "GUI를 강제로 새로고침했습니다.");
					}

					lastClick.put(p.getName(), System.currentTimeMillis());
				}
			} else {
				GUIClickEvent ge = new GUIClickEvent(e);
				ge.setPickUpCancel(!gui.isCanPickUp());
				try {
					Bukkit.getPluginManager().callEvent(ge);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				e.setCancelled(ge.isPickUpCancel());

				if(ge.isIconClicked()) {
					IconClickEvent ie = new IconClickEvent(ge, null);
					try {
						ie.getIcon().onIconClick(ie);
						Bukkit.getPluginManager().callEvent(ie);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					ge.setPickUpCancel(ie.isPickUpCancel());

					if (ie.getIcon().existsObject("pageableEvent")) {
						String pageableEvent = ie.getIcon().getObject("pageableEvent").toString();
						if (pageableEvent != null) {
							PageableGUI parent = (PageableGUI) ie.getIcon().getObject("pageableGUIParent");
							int currentIndex =(int) ie.getIcon().getObject("pageableGUIIndex");
							int newIndex = currentIndex;

							if (pageableEvent.equals("previous")) {
								newIndex = currentIndex == 0 ? 0 : currentIndex - 1;
							} else if (pageableEvent.equals("next")) {
								newIndex = (currentIndex == parent.getMaxPage() - 1 ? currentIndex : currentIndex + 1);
							}

							if (currentIndex != newIndex) {
								parent.getPageGUI(newIndex).open(p);
							}
						}
					}
				}

				if (ge.isIconClicked() && ge.getClickedIcon() instanceof FakeIcon && ge.getClickedItem() == null) {
					p.updateInventory();
				}
			}

			Bukkit.getScheduler().runTaskLater(KCorePlugin.getInstance(),() -> gui.updateFakeIcons(p), 1);
		} else if(e.getSlotType() == SlotType.QUICKBAR && api.getGUIManager().hasQuickBar(p.getName())) {
			e.setCancelled(true);
			
			p.updateInventory();
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();

		GUI gui = api.getGUIManager().getPlayerGUI(p);
		if (gui == null) return;

		gui.getIcons().values().forEach(icon -> {
			if (!(icon instanceof FakeIcon)) return;

			((FakeIcon) icon).getItems().remove(PlayerKey.getPlayerKeyByPlatformPlayer(p));
		});

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

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		lastClick.remove(e.getPlayer().getName());
	}
	
}