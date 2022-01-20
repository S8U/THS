package su.plugin.ability.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.event.RangeOutEvent;
import su.plugin.ability.api.object.GameMap;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.common.api.Core;

public class ControlListener implements Listener {
	
	private AbilityAPI api = AbilityPlugin.getApi();

	@EventHandler
	public void onInteract(PlayerBedEnterEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if(!api.isUseInfinityFoodLevel()) return;
		
		((Player) e.getEntity()).setFoodLevel(20);
		
		e.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		if(api.isRainOff() && e.toWeatherState()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(gp.isEliminate()) {
			Core.wmsg(p, "게임에서 탈락하여 블럭 조작이 불가능합니다.");
			e.setCancelled(true);
			return;
		} else if(!api.isUseBlockProtectOnWait() || api.getGameManager().isTeleportedAll() || api.getGameManager().getGameState().getProgress() >= GameState.DRAWING.getProgress()) return;
		else if(api.isAdmin(p, false)) return;
		e.setCancelled(true);
		p.updateInventory();
		Core.wmsg(p, "게임 시작 전에는 블럭 조작이 불가능합니다.");
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(gp.isEliminate()) {
			Core.wmsg(p, "게임에서 탈락하여 블럭 조작이 불가능합니다.");
			e.setCancelled(true);
			return;
		} else if(!api.isUseBlockProtectOnWait() || api.getGameManager().isTeleportedAll() || api.getGameManager().getGameState().getProgress() >= GameState.DRAWING.getProgress()) return;
		else if(api.isAdmin(p, false)) return;
		e.setCancelled(true);
		p.updateInventory();
		Core.wmsg(p, "게임 시작 전에는 블럭 조작이 불가능합니다.");
	}
	
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent e) {
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(gp.isEliminate()) {
			Core.wmsg(p, "게임에서 탈락하여 블럭 조작이 불가능합니다.");
			e.setCancelled(true);
			return;
		} else if(!api.isUseBlockProtectOnWait() || api.getGameManager().isTeleportedAll() || api.getGameManager().getGameState().getProgress() >= GameState.DRAWING.getProgress()) return;
		else if(api.isAdmin(p, false)) return;
		e.setCancelled(true);
		p.updateInventory();
		Core.wmsg(p, "게임 시작 전에는 블럭 조작이 불가능합니다.");
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		Player p = e.getPlayer();
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(gp.isEliminate()) {
			Core.wmsg(p, "게임에서 탈락하여 블럭 조작이 불가능합니다.");
			e.setCancelled(true);
			return;
		} else if(!api.isUseBlockProtectOnWait() || api.getGameManager().isTeleportedAll() || api.getGameManager().getGameState().getProgress() >= GameState.DRAWING.getProgress()) return;
		else if(api.isAdmin(p, false)) return;
		e.setCancelled(true);
		p.updateInventory();
		Core.wmsg(p, "게임 시작 전에는 블럭 조작이 불가능합니다.");
	}
	
	@EventHandler
	public void onPvp(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		
		Player p = null;
		
		if(e.getDamager() instanceof Projectile) {
			Projectile pj = (Projectile) e.getDamager();

			if(!(pj.getShooter() instanceof Player)) return;
			p = (Player) pj.getShooter();
		} else if(!(e.getDamager() instanceof Player)) return;
		
		if(p == null) {
			p = (Player) e.getDamager();
		}
		
		Player target = (Player) e.getEntity();
		GamePlayer tp = api.getPlayerManager().getGamePlayer(target);
		GamePlayer gp = api.getPlayerManager().getGamePlayer(p);
		if(api.isInvincibilityTime() || gp.isEliminate() || tp.isEliminate() || (api.isUsePvpProtectOnWait() && api.getGameManager().getGameState().getProgress() < GameState.PLAYING.getProgress())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(api.isInvincibilityTime() || (api.isUseInvincibilityOnWait() && api.getGameManager().getGameState().getProgress() < GameState.PLAYING.getProgress()) || api.getGameManager().getGameState() == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if(api.isAdmin(p, false)) return;
		else if(!api.isUseCommandProtectOnWait() || api.getGameManager().isGameStarted()) return;
		String[] cmd = e.getMessage().split(" ");
		for(String ec : api.getProtectExceptionCommands()) {
			String[] ecmd = ec.split(" ");
			if(cmd[0].equals("/" + ecmd[0])) return;
		}
		Core.wmsg(p, "게임 시작 전 사용이 금지된 명령어입니다.");
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(!api.getGameManager().isAutoMode() || !api.getGameManager().isTeleportedInMap() || api.getMapManager().getPlayingMap() == null) return;
		
		GameMap map = api.getMapManager().getPlayingMap();
		Location to;
		
		if(api.getGameManager().isTeleportedAll()) {
			if(api.isUseAutoTpAllMapLimit() && map.isInMap(e.getTo(), api.getTpAllLimitRange(), true)) return;
			else if(map.getMinTPAllLocation() == null || map.getMaxTPAllLocation() == null || map.isInMap(e.getTo(), true)) return;
			to = api.isUseAutoTpAllMapLimit() && map.isInMap(e.getFrom(), true) ? e.getFrom() : map.getTPAllLocation();
		} else {
			if(api.isUseAutoMapLimit() && map.isInMap(e.getTo(), api.getMapLimitRange(), false)) return;
			else if(map.getMinMapLocation() == null || map.getMaxMapLocation() == null || map.isInMap(e.getTo(), false)) return;
			to = api.isUseAutoMapLimit() && map.isInMap(e.getFrom(), false) ? e.getFrom() : map.getMapLocation();
		}
		
		if(to == null || to.getWorld() == null) return;
		
		RangeOutEvent event = new RangeOutEvent(e.getPlayer(), map);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) return;
		
		e.setTo(to);
	}
	
}