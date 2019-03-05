package su.plugin.pvpstats.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.pvpstats.PVPStatsPlugin;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.api.object.PSPlayer;

public class PlayerListener implements Listener {
	
	private PVPStatsAPI api = PVPStatsPlugin.getApi();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		api.getPlayerManager().registerPlayer(e.getPlayer());

		if(api.isUseHolographicDisplays() && api.getRankingManager().getDailyRankingHologram() != null) {
			api.getRankingManager().getDailyRankingHologram().getVisibilityManager().showTo(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(api.isUseHolographicDisplays() && api.getRankingManager().getDailyRankingHologram() != null) {
			api.getRankingManager().getWeeklyRankingHologram().getVisibilityManager().hideTo(e.getPlayer());
			api.getRankingManager().getMonthlyRankingHologram().getVisibilityManager().hideTo(e.getPlayer());
			api.getRankingManager().getAllRankingHologram().getVisibilityManager().hideTo(e.getPlayer());
		}

		api.getPlayerManager().removePSPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()));
	}
	
	//
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(!api.isWritePVPStats() || api.isUseAbility()) return;

		Player p = e.getEntity();
		if(p.getKiller() == null && !api.isIncludeNatureDeath()) return;

		PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);
		
		PSPlayer pp = api.getPlayerManager().getPSPlayer(playerKey);
		pp.addDeathCount();
		pp.setKillStreak(0);
		pp.addDeathStreak();

		pp.savePlayerAsynchronously();

		PlayerKey killerKey = p.getKiller() == null ? null : PlayerKey.getPlayerKeyByPlatformPlayer(p.getKiller());

		if(p.getKiller() != null) {
			PSPlayer kp = api.getPlayerManager().getPSPlayer(killerKey);
			kp.addKillCount();
			kp.addKillStreak();
			kp.setDeathStreak(0);

			kp.savePlayerAsynchronously();

			for(PlayerKey pk : pp.getLastHitTimes().keySet()) {
				if(killerKey.equals(pk) || playerKey.equals(pk) || System.currentTimeMillis() - pp.getLastHitTime(pk) > PVPStatsAPI.getAssistEffectiveTime() * 1000) continue;

				PSPlayer ap = api.getPlayerManager().getPSPlayer(playerKey);
				ap.addAssistCount();
				ap.setDeathStreak(0);

				ap.savePlayerAsynchronously();
			}
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(PVPStatsPlugin.getInstance(), () -> {
			api.getSQLManager().writePVPLog(p.getKiller() == null ? null : killerKey, pp.getPlayerKey());
		});
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onHit(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player) || (e.getDamager() instanceof Projectile && !(((Projectile) e.getDamager()).getShooter() instanceof Player))) return;

		Player damager = e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player ? (Player) ((Projectile) e.getDamager()).getShooter() : (Player) e.getDamager();
		if(damager == null) return;
		
		Player tp = (Player) e.getEntity();
		PlayerKey tpk = PlayerKey.getPlayerKeyByPlatformPlayer(tp);
		if(tpk == null) return;

		PSPlayer tpp = api.getPlayerManager().getPSPlayer(tpk);
		
		tpp.setLastHitTime(PlayerKey.getPlayerKeyByPlatformPlayer(damager), System.currentTimeMillis());
	}
	
}