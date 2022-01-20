package su.plugin.core.bukkit.api.scheduler;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class UKRunnable implements Runnable {
	
	private final Plugin plugin;
	
	private int taskId = -1;
	
	public synchronized void cancel() throws IllegalStateException {
		if (taskId == -1) return;

		Bukkit.getScheduler().cancelTask(getTaskId());
		taskId = -1;
	}

	public synchronized BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(Bukkit.getScheduler().runTask(plugin, this));
	}

	public synchronized BukkitTask runTaskAsynchronously()
			throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(Bukkit.getScheduler().runTaskAsynchronously(plugin, this));
	}

	public synchronized BukkitTask runTaskLater(long delay)
			throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(Bukkit.getScheduler().runTaskLater(plugin, this, delay));
	}

	public synchronized BukkitTask runTaskLaterAsynchronously(long delay)
			throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, delay));
	}

	public synchronized BukkitTask runTaskTimer(long delay, long period)
			throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period));
	}
	
	public synchronized BukkitTask runTaskTimerAsynchronously(long delay, long period)
			throws IllegalArgumentException, IllegalStateException {
		checkState();
		return setupId(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period));
	}
	
	public BukkitTask schedule(Date time) {
		checkState();
		return setupId(runTaskLater((long) Math.ceil((time.getTime() - System.currentTimeMillis()) / 50)));
	}
	
	public BukkitTask schedule(Date firstTime, long period) {
		checkState();
		return setupId(runTaskTimer((long) Math.ceil((firstTime.getTime() - System.currentTimeMillis()) / 50), period));
	}

	public BukkitTask scheduleAsynchronously(Date time) {
		checkState();
		return setupId(runTaskLaterAsynchronously((long) Math.ceil((time.getTime() - System.currentTimeMillis()) / 50)));
	}
	
	public BukkitTask scheduleAsynchronously(Date firstTime, long period) {
		checkState();
		return setupId(runTaskTimerAsynchronously((long) Math.ceil((firstTime.getTime() - System.currentTimeMillis()) / 50), period));
	}
	
	public synchronized int getTaskId() throws IllegalStateException {
		return this.taskId;
	}
	
	public synchronized boolean isRunning() {
		return this.taskId != -1;
	}

	private void checkState() {
		if (this.taskId != -1)
			throw new IllegalStateException("Already scheduled as " + this.taskId);
	}

	private BukkitTask setupId(BukkitTask task) {
		this.taskId = task.getTaskId();
		return task;
	}
	
}