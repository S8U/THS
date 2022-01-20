package su.plugin.ability.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.common.api.Core;

public class WatchListener implements Listener {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(!gp.isWatchMode()) return;
		e.setCancelled(true);
		p.updateInventory();
		// Core.wmsg(p, "관전 중에는 블럭 조작이 불가능합니다.");
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(!gp.isWatchMode()) return;
		e.setCancelled(true);
		p.updateInventory();
		// Core.wmsg(p, "관전 중에는 블럭 조작이 불가능합니다.");
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(!gp.isWatchMode()) return;
		e.setCancelled(true);
		p.updateInventory();
		// Core.wmsg(p, "관전 중에는 블럭 조작이 불가능합니다.");
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onBucketFill(PlayerBucketFillEvent e) {
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(!gp.isWatchMode()) return;
		e.setCancelled(true);
		p.updateInventory();
		// Core.wmsg(p, "관전 중에는 블럭 조작이 불가능합니다.");
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onPvp(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player)) return;
		Player p = (Player) e.getDamager();
		if(!api.getPlayerManager().getGamePlayer(p).isWatchMode()) return;
		e.setCancelled(true);
		// Core.wmsg(p, "관전 중에는 PVP가 불가능합니다.");
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(!api.getPlayerManager().getGamePlayer((Player) e.getEntity()).isWatchMode()) return;
		e.setCancelled(true);
	}
	
	/*@EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
	public void onGamePlayerInteractWatchPlayer(PlayerInteractEvent e) {
		GamePlayer gp = api.getPlayerManager().getGamePlayer(e.getPlayer());
		if(e.getClickedBlock() == null || gp.isEliminate() || gp.isWatchMode()) return;
		for(GamePlayer wp : api.getPlayerManager().getOnlineWatchPlayers()) {
			if(wp.getPlayer() == null || !isOverLap(e.getClickedBlock().getLocation(), wp.getPlayer().getLocation())) continue;
			KCore.teleport(wp.getPlayer(), getEmptyLocation(e.getPlayer().getLocation()));
			// Core.wmsg(wp.getPlayers(), "게임 중인 플레이어에게 방해되어 텔레포트되었습니다.");
		}
	}*/
	
	@EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e) {
		if(!(e.getPlayer() instanceof Player)) return;
		if(!api.getPlayerManager().getGamePlayer((Player) e.getPlayer()).isWatchMode()) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		if(api.isUseWatchModeQuickBar()) return;
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(!gp.isWatchMode()) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(!gp.isWatchMode()) return;
		e.setCancelled(true);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if(api.isAdmin(p, false)) return;
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(gp == null || !gp.isWatchMode() || !api.isUseCommandProtectOnWatchMode() || !api.getGameManager().isGameStarted()) return;
		String[] cmd = e.getMessage().split(" ");
		for(String ec : api.getWatchExceptionCommands()) {
			String[] ecmd = ec.split(" ");
			if(cmd[0].equals("/" + ecmd[0])) return;
		}
		Core.wmsg(p, "관전 중에 사용이 불가능한 명령어입니다.");
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(!gp.isWatchMode()) return;
		
		e.getDrops().clear();
		e.setDroppedExp(0);
	}

	@EventHandler
	public void onFly(PlayerToggleFlightEvent e) {
		if(api.getGameManager().getGameState().getProgress() > GameState.PREPARING.getProgress()) return;
		if(!api.getPlayerManager().getGamePlayer(e.getPlayer()).isWatchMode() || (e.getPlayer().getGameMode() != GameMode.SURVIVAL && e.getPlayer().getGameMode() != GameMode.ADVENTURE)) return;
		if(!e.isFlying()) return;

		Core.wmsg(e.getPlayer(), "게임 시작 전에는 날 수 없습니다.");

		e.setCancelled(true);
	}
	
/*	private boolean isOverLap(Location block, Location player) {
		return Math.pow(block.getY() - player.getY(), 2) <= 4 && Math.pow(block.getX() - player.getX(), 2) < Math.pow(1.3, 2) && Math.pow(block.getZ() - player.getZ(), 2) < Math.pow(1.3, 2);
	}
	
	private Location getEmptyLocation(Location loc) {
		List<Location> locs = new ArrayList<>();
		for(int i = 1; i < 3; i++) {
			for(int j = 1; j < 3; j++) {
				if(loc.getWorld().getBlockTypeIdAt((int) loc.getX() + i, (int) loc.getY(), (int) loc.getZ() + j) == 0 && loc.getWorld().getBlockTypeIdAt((int) loc.getX() + i, (int) loc.getY() + 1, (int) loc.getZ() + j) == 0) {
					locs.add(new Location(loc.getWorld(), loc.getX() + i, loc.getY(), loc.getZ() + j, loc.getYaw(), loc.getPitch()));
				}
			}
		}
		if(locs.size() == 0) {
			locs.add(loc);
		}
		if(locs.size() < 1) return api.getGameManager().isGameStarted() ? (api.getGameManager().isTeleportedAll() ? api.getMapManager().getPlayingMap().getTPAllLocation() : api.getMapManager().getPlayingMap().getMapLocation()) : api.getMapManager().getSpawn();
		return locs.get(NumberUtil.random(0, locs.size() - 1));
	}*/
	
}