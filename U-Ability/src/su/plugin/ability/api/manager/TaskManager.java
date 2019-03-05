package su.plugin.ability.api.manager;

import java.util.HashMap;
import lombok.Getter;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.task.EliminateTask;
import su.plugin.ability.api.task.GameStartVoteTask;
import su.plugin.ability.api.task.LocationNotifyTask;
import su.plugin.ability.api.task.ProjectilePassTask;
import su.plugin.ability.api.task.SideBarUpdateTask;
import su.plugin.ability.api.task.SupplyTask;
import su.plugin.ability.api.task.auto.AutoStartTask;
import su.plugin.ability.api.task.auto.DrawSkipTask;
import su.plugin.ability.api.task.auto.TeleportAllTask;
import su.plugin.ability.api.task.game.DrawAbilityTask;
import su.plugin.ability.api.task.game.GameStartCountTask;
import su.plugin.ability.api.task.game.InvincibilityTask;
import su.plugin.ability.api.task.game.NormalStartTask;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

@Getter
public class TaskManager {
	
	private SupplyTask supplyTask;
	private LocationNotifyTask locationNotifyTask;
	private GameStartVoteTask gameStartVoteTask;
	private SideBarUpdateTask sideBarUpdateTask;
	private ProjectilePassTask projectilePassTask;
	
	private NormalStartTask normalStartTask;
	private DrawAbilityTask drawAbilityTask;
	private GameStartCountTask gameStartCountTask;
	private InvincibilityTask invincibilityTask;
	
	private AutoStartTask autoStartTask;
	private DrawSkipTask drawSkipTask;
	private TeleportAllTask teleportAllTask;

	private HashMap<PlayerKey, EliminateTask> eliminateTasks = new HashMap<>();
	
	public boolean runNormalStartTask(int startDelay) {
		if(normalStartTask != null && normalStartTask.getTaskId() != -1) return false;
		normalStartTask = new NormalStartTask();
		normalStartTask.runTaskTimerAsynchronously(startDelay, 20); return true;
	}
	
	public boolean stopNormalStartTask() {
		if(normalStartTask == null || normalStartTask.getTaskId() == -1) return false;
		normalStartTask.cancel();
		return true;
	}
	
	public boolean runDrawAbilityTask(int startDelay, int drawDelay) {
		if(drawAbilityTask != null && drawAbilityTask.getTaskId() != -1) return false;
		drawAbilityTask = new DrawAbilityTask();
		drawAbilityTask.init(drawDelay);
		drawAbilityTask.runTaskTimerAsynchronously(startDelay, 20);
		return true;
	}
	
	public boolean stopDrawAbilityTask() {
		if(drawAbilityTask == null || drawAbilityTask.getTaskId() == -1) return false;
		drawAbilityTask.cancel();
		return true;
	}
	
	public boolean runGameStartCountTask(int startDelay) {
		if(gameStartCountTask != null && gameStartCountTask.getTaskId() != -1) return false;
		gameStartCountTask = new GameStartCountTask();
		gameStartCountTask.runTaskTimerAsynchronously(startDelay, 20); return true;
	}
	
	public boolean stopGameStartCountTask() {
		if(gameStartCountTask == null || gameStartCountTask.getTaskId() == -1) return false;
		gameStartCountTask.cancel();
		return true;
	}
	
	public boolean runInvincibilityTask(int startDelay, int invincibilityCount) {
		if(invincibilityTask != null && invincibilityTask.getTaskId() != -1) return false;
		invincibilityTask = new InvincibilityTask();
		invincibilityTask.init(invincibilityCount);
		invincibilityTask.runTaskTimerAsynchronously(startDelay, 20);
		return true;
	}
	
	public boolean stopInvincbilityTask() {
		if(invincibilityTask == null || invincibilityTask.getTaskId() == -1) return false;
		invincibilityTask.cancel();
		return true;
	}
	
	public boolean runSupplyTask(int count) {
		if(supplyTask != null && supplyTask.getTaskId() != -1) return false;
		supplyTask = new SupplyTask();
		supplyTask.runTaskLater(count * 20); return true;
	}
	
	public boolean stopSupplyTask() {
		if(supplyTask == null || supplyTask.getTaskId() == -1) return false;
		supplyTask.cancel();
		return true;
	}
	
	public boolean runLocationNotifyTask(int delay) {
		if(locationNotifyTask != null && locationNotifyTask.getTaskId() != -1) return false;
		locationNotifyTask = new LocationNotifyTask();
		locationNotifyTask.runTaskLater(delay * 20); return true;
	}
	
	public boolean stopLocationNotifyTask() {
		if(locationNotifyTask == null || locationNotifyTask.getTaskId() == -1) return false;
		locationNotifyTask.cancel();
		return true;
	}
	
	public boolean runAutoStartTask(int startDelay) {
		if(autoStartTask != null && autoStartTask.getTaskId() != -1) return false;
		autoStartTask = new AutoStartTask();
		autoStartTask.runTaskTimer(startDelay, 20); return true;
	}
	
	public boolean stopAutoStartTask() {
		if(autoStartTask == null || autoStartTask.getTaskId() == -1) return false;
		autoStartTask.cancel();
		return true;
	}
	
	public boolean runDrawSkipTask(int startDelay) {
		if(drawSkipTask != null && drawSkipTask.getTaskId() != -1) return false;
		drawSkipTask = new DrawSkipTask();
		drawSkipTask.runTaskTimer(startDelay, 20); return true;
	}
	
	public boolean stopDrawSkipTask() {
		if(drawSkipTask == null || drawSkipTask.getTaskId() == -1) return false;
		drawSkipTask.cancel();
		return true;
	}
	
	public void runTeleportAllTask(int startDelay, int count) {
		if(teleportAllTask != null && teleportAllTask.getTaskId() != -1) {
			stopTeleportAllTask();
		}
		teleportAllTask = new TeleportAllTask(count);
		teleportAllTask.runTaskTimer(startDelay, 20);
	}
	
	public boolean stopTeleportAllTask() {
		if(teleportAllTask == null || teleportAllTask.getTaskId() == -1) return false;
		teleportAllTask.cancel(); return true;
	}
	
	public void runGameStartVoteTask(int count) {
		if(gameStartVoteTask != null && gameStartVoteTask.getTaskId() != -1) {
			stopGameStartVoteTask();
		}
		gameStartVoteTask = new GameStartVoteTask();
		gameStartVoteTask.runTaskLater(count * 20);
	}
	
	public boolean stopGameStartVoteTask() {
		if(gameStartVoteTask == null || gameStartVoteTask.getTaskId() == -1) return false;
		gameStartVoteTask.cancel(); return true;
	}
	
	public void runProjectilePassTask() {
		if(projectilePassTask != null && projectilePassTask.getTaskId() != -1) {
			stopProjectilePassTask();
		}
		projectilePassTask = new ProjectilePassTask();
		projectilePassTask.runTaskTimer(1, 1);
	}
	
	public boolean stopProjectilePassTask() {
		if(projectilePassTask == null || projectilePassTask.getTaskId() == -1) return false;
		projectilePassTask.cancel(); return true;
	}
	
	public void runSideBarUpdateTask() {
		if(sideBarUpdateTask != null && sideBarUpdateTask.getTaskId() != -1) {
			stopSideBarUpdateTask();
		}
		sideBarUpdateTask = new SideBarUpdateTask();
		sideBarUpdateTask.runTaskTimerAsynchronously(0, 20);
	}
	
	public boolean stopSideBarUpdateTask() {
		if(sideBarUpdateTask == null || sideBarUpdateTask.getTaskId() == -1) return false;
		sideBarUpdateTask.cancel(); return true;
	}

	public void runEliminateTask(PlayerKey playerKey) {
		if(eliminateTasks.containsKey(playerKey) && eliminateTasks.get(playerKey).getTaskId() != -1) return;

		EliminateTask task = new EliminateTask(playerKey);
		task.runTaskLaterAsynchronously(AbilityAPI.getReconnectAllowCount() * 20);

		eliminateTasks.put(playerKey, task);
	}

	public void stopEliminateTask(PlayerKey playerKey) {
		if(!eliminateTasks.containsKey(playerKey)) return;

		eliminateTasks.get(playerKey).cancel();

		eliminateTasks.remove(playerKey);
	}
	
}
