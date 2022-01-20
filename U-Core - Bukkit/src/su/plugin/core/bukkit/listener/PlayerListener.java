package su.plugin.core.bukkit.listener;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.entity.EntityDamageByPlayerEvent;
import su.plugin.core.bukkit.api.event.player.FirstPlayerJoinEvent;
import su.plugin.core.bukkit.api.event.player.LastPlayerQuitEvent;
import su.plugin.core.bukkit.api.event.player.PlayerDamageByPlayerEvent;
import su.plugin.core.bukkit.api.event.player.PlayerDeathDamageEvent;
import su.plugin.core.bukkit.api.event.player.PlayerMoveLocationEvent;
import su.plugin.core.bukkit.api.player.KPlayer;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public class PlayerListener implements Listener {

	private HashMap<PlayerKey, PlayerKey> lastHits = new HashMap<>(); // Player, Damager

	@EventHandler(priority=EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {
		if(KCore.getOnlinePlayers().size() > 1) return;

		for(UPlayer uap : KCore.getOnlineUPlayers()) {
			KPlayer kap = (KPlayer) uap;
			if(!kap.isOnline() || !kap.isHide()) continue;

			e.getPlayer().hidePlayer(kap.getPlayer());
			//e.getPlayer().hidePlayer(KCorePlugin.getInstance(), kap.getPlayer());
		}

		Bukkit.getPluginManager().callEvent(new FirstPlayerJoinEvent(e.getPlayer(), e));
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		
		Location old = e.getFrom();
		Location nw = e.getTo();
		
		if(old != null && old.getWorld().equals(nw.getWorld()) && old.distance(nw) < 0.1) return;
		
		PlayerMoveLocationEvent event = new PlayerMoveLocationEvent(p, e);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			e.setTo(e.getFrom());
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent e) {
		lastHits.remove(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()));

		if(KCore.getOnlinePlayers().size() > 1) return;
		
		Bukkit.getPluginManager().callEvent(new LastPlayerQuitEvent(e.getPlayer(), e));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent e) {
		if(e.isCancelled() || !(e.getEntity() instanceof Player) || (e.getDamager() instanceof Projectile
				&& !(((Projectile) e.getDamager()).getShooter() instanceof Player))) return;

		Player damager = e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player ? (Player) ((Projectile) e.getDamager()).getShooter() : (Player) e.getDamager();
		if(damager == null) return;

		lastHits.put(PlayerKey.getPlayerKeyByPlatformPlayer((Player) e.getEntity()), PlayerKey.getPlayerKeyByPlatformPlayer(damager));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByPlayer(EntityDamageByEntityEvent e) {
		Entity entity = e.getEntity();
		Entity damager = e.getDamager();

		if(damager instanceof Player || (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)) {
			damager = damager instanceof Projectile ? (Entity) ((Projectile) damager).getShooter() : damager;
			Projectile projectile = damager instanceof Projectile ? (Projectile) damager : null;

			EntityDamageByPlayerEvent entityDamageByPlayerEvent = entity instanceof Player ? new PlayerDamageByPlayerEvent((Player) entity, (Player) damager, projectile, e) : new EntityDamageByPlayerEvent(entity, (Player) damager, projectile, e);

			Bukkit.getPluginManager().callEvent(entityDamageByPlayerEvent);

			e.setCancelled(entityDamageByPlayerEvent.isCancelled());
		}
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) return;

		KPlayer kp = (KPlayer) KCore.getUPlayerByPlatformPlayer((Player) e.getEntity());
		if (e.getDamager() instanceof Projectile) {
			if (!(((Projectile) e.getDamager()).getShooter() instanceof LivingEntity)) return;

			kp.setLastHit((LivingEntity) ((Projectile) e.getDamager()).getShooter());
		} else {
			if (!(e.getDamager() instanceof LivingEntity)) return;
			kp.setLastHit((LivingEntity) e.getDamager());
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player) || ((Player) e.getEntity()).getHealth() - e.getFinalDamage() > 0 || e.getEntity().getLastDamageCause() == null) return;

		PlayerKey killerKey = lastHits.get(PlayerKey.getPlayerKeyByPlatformPlayer((Player) e.getEntity()));
		Player killer = killerKey == null ? null : (Player) killerKey.getPlatformPlayer();

		PlayerDeathDamageEvent event = new PlayerDeathDamageEvent(((Player) e.getEntity()), killer, e);
		Bukkit.getPluginManager().callEvent(event);
		
		e.setCancelled(event.isCancelled());
	}

}