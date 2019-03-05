package su.plugin.glogin.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import su.plugin.glogin.bukkit.KGLoginPlugin;
import su.plugin.glogin.bukkit.api.KGLoginAPI;
import su.plugin.glogin.common.api.object.Account;
import su.plugin.core.common.api.player.PlayerKey;

public class ControlListener implements Listener {
	
	private KGLoginAPI api = KGLoginPlugin.getApi();
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		if(account == null || !account.isLogin()) e.setTo(e.getFrom());
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onPickup(PlayerPickupItemEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onDrop(PlayerDropItemEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onBed(PlayerBedEnterEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onSignChange(SignChangeEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onRegain(EntityRegainHealthEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getEntity().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getEntity().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onEntityTarget(EntityTargetEvent e) {
		if(!(e.getTarget() instanceof Player)) return;
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getTarget().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getEntity().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getWhoClicked().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
	@EventHandler (priority=EventPriority.LOWEST)
	public void onInventoryOpen(InventoryOpenEvent e) {
		Account account = api.getAccountManager().getAccount(PlayerKey.getPlayerKey(e.getPlayer().getName()));
		if(account == null || !account.isLogin()) e.setCancelled(true);
	}
	
}