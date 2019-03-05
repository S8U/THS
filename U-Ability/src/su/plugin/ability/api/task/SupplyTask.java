package su.plugin.ability.api.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class SupplyTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	public SupplyTask() {
		super(AbilityPlugin.getInstance());
	}
	
	public void run() {
		Bukkit.getScheduler().runTask(AbilityPlugin.getInstance(), () -> {
			Location location = api.getSupplyManager().createRandomSupplyAtRandomLocation(api.getMapManager().getPlayingMap(), api.getGameManager().isTeleportedAll());
			Core.nbc(" ");
			Core.cbc(ChatColor.AQUA, "§b(X: §f" + Math.round(location.getX()) + "§b, Y: §f" + Math.round(location.getY()) + "§b, Z: §f" + Math.round(location.getZ()) + "§b) 에 보급품이 생성되었습니다.");
			Core.cbc(ChatColor.AQUA, "§b보급품 좌표는 §f'/보급품 기록' §b명령어로 다시 확인할 수 있습니다.");
		});
	}

}
