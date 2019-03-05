package su.plugin.ability.api.task;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.Core;

public class ProjectilePassTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	public ProjectilePassTask() {
		super(AbilityPlugin.getInstance());
	}
	
	@Override
	public void run() {
		List<GamePlayer> watch = api.getPlayerManager().getOnlineWatchPlayers();
		
		if(watch.size() < 1) return;
		
		for(GamePlayer gp : watch) {
			Player p = gp.getPlayer();
			for(Entity e : p.getWorld().getEntities()) {
				if(!(e instanceof Projectile) || !isOverLap(e.getLocation(), p.getLocation()) || (e instanceof Arrow && (((Arrow) e).isInBlock() || ((Arrow) e).isCritical()))) continue;
				KCore.teleport(p, getEmptyLocation(p.getLocation()));
				Core.wmsg(p, "게임 중인 플레이어에게 방해되어 텔레포트되었습니다.");
			}
		}
	}
	
	private boolean isOverLap(Location loc, Location player) {
		return Math.pow(loc.getY() - player.getY(), 2) < 9 && Math.pow(loc.getX() - player.getX(), 2) < 9 && Math.pow(loc.getZ() - player.getZ(), 2) < 9;
	}
	
	private Location getEmptyLocation(Location loc) {
		double farDistance = 0;
		Location farLoc = null;
		for(int i = 2; i < 5; i++) {
			for(int j = 2; j < 5; j++) {
				for(int k = 0; k < 3; k++) {
					if(loc.getWorld().getBlockTypeIdAt((int) loc.getX() + i, (int) loc.getY() + k, (int) loc.getZ() + j) == 0 && loc.getWorld().getBlockTypeIdAt((int) loc.getX() + i, (int) loc.getY() + k + 1, (int) loc.getZ() + j) == 0) {
						Location tLoc = new Location(loc.getWorld(), loc.getX() + i, loc.getY() + k, loc.getZ() + j, loc.getYaw(), loc.getPitch());
						double tDistance = Math.pow(loc.getX() - tLoc.getX(), 2) + Math.pow(loc.getZ() - tLoc.getZ(), 2);
						if(tDistance > farDistance) {
							farDistance = tDistance;
							farLoc = tLoc;
						}
					}
				}
			}
		}
		if(farLoc == null || farDistance < 6) return api.getGameManager().isGameStarted() ? (api.getGameManager().isTeleportedAll() ? api.getMapManager().getPlayingMap().getTPAllLocation() : api.getMapManager().getPlayingMap().getMapLocation()) : api.getMapManager().getSpawn();
		return farLoc;
	}
	
}