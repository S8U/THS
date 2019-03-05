package su.plugin.glogin.bungee.api.manager;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.glogin.bungee.api.GGLoginAPI;
import su.plugin.glogin.bungee.api.task.LoginTimeoutTask;
import su.plugin.core.common.api.player.PlayerKey;

@Getter
public class TaskManager {
	
	private HashMap<PlayerKey, LoginTimeoutTask> loginTimeoutTasks = new HashMap<>(); // <PlayerKey, Task>
	
	public boolean hasLoginTimeoutTask(PlayerKey playerKey) {
		return loginTimeoutTasks.containsKey(playerKey);
	}
	
	public boolean hasLoginTimeoutTask(ProxiedPlayer player) {
		return hasLoginTimeoutTask(PlayerKey.getPlayerKey(player.getName()));
	}
	
	public void startLoginTimeoutTask(PlayerKey playerKey) {
		if(hasLoginTimeoutTask(playerKey)) return;
		LoginTimeoutTask task = new LoginTimeoutTask(playerKey);
		task.schedule(GGLoginAPI.getLoginTimeout(), TimeUnit.SECONDS);
		loginTimeoutTasks.put(playerKey, task);
	}
	
	public void startLoginTimeoutTask(ProxiedPlayer player) {
		startLoginTimeoutTask(PlayerKey.getPlayerKey(player.getName()));
	}
	
	public void stopLoginTimeoutTask(PlayerKey playerKey) {
		if(!hasLoginTimeoutTask(playerKey)) return;
		loginTimeoutTasks.get(playerKey).cancel();
		loginTimeoutTasks.remove(playerKey);
	}
	
	public void stopLoginTimeoutTask(ProxiedPlayer player) {
		stopLoginTimeoutTask(PlayerKey.getPlayerKey(player.getName()));
	}
	
}