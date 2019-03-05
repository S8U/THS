package su.plugin.lobbysystem.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import su.plugin.lobbysystem.LobbySystemPlugin;
import su.plugin.lobbysystem.api.LobbySystemAPI;

public class BlockListener implements Listener {
	
	private LobbySystemAPI api = LobbySystemPlugin.getApi();
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(!api.isBlockProtect() || p.isOp() || api.canBreak(e.getBlock())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(!api.isBlockProtect() || p.isOp() || api.canPlace(e.getBlock())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(api.isBlockProtect() && e.getAction().equals(Action.PHYSICAL)) {
			e.setCancelled(true);
		}
		if(!api.isBlockProtect() || p.isOp() || !e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getClickedBlock() == null || api.canBreak(e.getClickedBlock())) return;
		e.setCancelled(true);
	}
	
	
	@EventHandler
	public void onBucketFille(PlayerBucketFillEvent e) {
		Player p = e.getPlayer();
		if(!api.isBlockProtect() || p.isOp() || api.canPlace(e.getBlockClicked())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		Player p = e.getPlayer();
		if(!api.isBlockProtect() || p.isOp() || api.canPlace(e.getBlockClicked())) return;
		e.setCancelled(true);
	}
	
}
