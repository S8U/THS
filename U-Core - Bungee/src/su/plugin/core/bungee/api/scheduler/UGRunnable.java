package su.plugin.core.bungee.api.scheduler;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

@RequiredArgsConstructor
public abstract class UGRunnable implements Runnable {
	
	@Getter
	private int taskId = -1;
	
	@Getter
	private boolean scheduled;
	
	@Getter
	@NonNull
	private Plugin plugin;
	
	public boolean schedule(long delay, TimeUnit timeUnit) {
		if(taskId != -1) return false;
		taskId = ProxyServer.getInstance().getScheduler().schedule(plugin, this, delay, timeUnit).getId();
		scheduled = true;
		return true;
	}
	
	public boolean schedule(long delay, long period, TimeUnit timeUnit) {
		if(taskId != -1) return false;
		taskId = ProxyServer.getInstance().getScheduler().schedule(plugin, this, delay, period, timeUnit).getId();
		scheduled = true;
		return true;
	}
	
	public boolean schedule(Date time) {
		return schedule(time.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}
	
	public boolean schedule(Date firstTime, long period, TimeUnit timeUnit) {
		return schedule(firstTime.getTime() - System.currentTimeMillis(), period, timeUnit);
	}
	
	public boolean runAsync() {
		if(taskId != -1) return false;
		taskId = ProxyServer.getInstance().getScheduler().runAsync(plugin, this).getId();
		scheduled = true;
		return true;
	}
	
	public boolean cancel() {
		if(taskId == -1) return false;
		ProxyServer.getInstance().getScheduler().cancel(taskId);
		taskId = -1;
		scheduled = false;
		return true;
	}
	
	public boolean isRunning() {
		return taskId != -1;
	}
	
	public boolean hasPlugin() {
		return this.plugin != null;
	}
	
}