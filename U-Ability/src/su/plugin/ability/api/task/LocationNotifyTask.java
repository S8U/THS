package su.plugin.ability.api.task;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class LocationNotifyTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	public LocationNotifyTask() {
		super(AbilityPlugin.getInstance());
	}
	
	@Override
	public void run() {
		for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
			Location location = gp.getPlayer().getLocation();
			if(api.isUseLocationNotifyMessage()) {
				Core.cbc(ChatColor.DARK_GREEN, gp.getDisplayName() + " §a님의 좌표: (X: §f" + Math.round(location.getX()) + "§a, Y: §f" + Math.round(location.getY()) + "§a, Z: §f" + Math.round(location.getZ()) + "§a)");
			}
			if(api.isUseLocationNotifyFirework()) {
				KCore.spawnFirework(location, false, false, Type.BALL_LARGE, Color.GREEN, Color.GREEN, 3);
			}
		}
		api.getTaskManager().runLocationNotifyTask(api.getLocationNotifyCount());
	}
	
}