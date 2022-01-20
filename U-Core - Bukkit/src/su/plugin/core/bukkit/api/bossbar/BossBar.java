package su.plugin.core.bukkit.api.bossbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import lombok.Getter;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;
import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.bossbar.entity.EntityBar;
import su.plugin.core.bukkit.api.bossbar.entity.LEntityBar;
import su.plugin.core.bukkit.api.bossbar.entity.OEntityBar;
import su.plugin.core.bukkit.api.enumeration.NMSVersion;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.event.UnregisterableListener;

public class BossBar implements Listener, UnregisterableListener {
	
	@Getter
	private String text;
	
	@Getter
	private double progress;
	
	//@Getter
	//private org.bukkit.boss.BossBar bukkitBar;
	
	@Getter
	private int timerTaskId = -1;
	
	@Getter
	private HashMap<UUID, EntityBar> entityBars = new HashMap<>();
	
	@Getter
	private List<UUID> playerUuids = new ArrayList<>();
	
	public BossBar() {
		if(KCore.getNMSVersion().isBefore(NMSVersion.v1_9_R1)) return;
		//bukkitBar = Bukkit.createBossBar(null, BarColor.PINK, BarStyle.SOLID);
	}
	
	public BossBar(String text) {
		if(KCore.getNMSVersion().isBefore(NMSVersion.v1_9_R1)) return;
		//bukkitBar = Bukkit.createBossBar(text, BarColor.PINK, BarStyle.SOLID);
	}
	
	/*public BossBar(String text, BarColor color) {
		if(KCore.getNMSVersion().isBefore(NMSVersion.v1_9_R1)) return;
		bukkitBar = Bukkit.createBossBar(text, color, BarStyle.SOLID);
	}

	public BossBar(String text, BarColor color, BarStyle style) {
		if(KCore.getNMSVersion().isBefore(NMSVersion.v1_9_R1)) return;
		bukkitBar = Bukkit.createBossBar(text, color, style);
	}*/
	
	public void addPlayers(Player... players) {
		addPlayers(Arrays.asList(players));
	}
	
	public void addPlayers(List<Player> players) {
		for(Player player : players) {
			if(hasPlayer(player)) continue;
			
			playerUuids.add(player.getUniqueId());
			
			if(isBefore1_9(player)) {
				registerListener();
				
				EntityBar eb = createEntityBar(player);
				eb.sendPacket(false);
				
				entityBars.put(player.getUniqueId(), eb);
			} else {
				//bukkitBar.addPlayer(player);
			}
		}
	}
	
	private void addBar(List<Player> players) {
		for(Player player : players) {
			if(hasBar(player)) continue;
			else if(isBefore1_9(player)) {
				registerListener();
				
				EntityBar eb = createEntityBar(player);
				eb.sendPacket(false);
				
				entityBars.put(player.getUniqueId(), eb);
			} else {
				//bukkitBar.addPlayer(player);
			}
		}
	}
	
	private void addBars() {
		addBar(getOnlinePlayers());
	}
	
	public void addAllPlayers() {
		addPlayers(KCore.getOnlinePlayers());
	}
	
	public void removePlayer(UUID uuid) {
		removePlayers(Bukkit.getPlayer(uuid));
	}
	
	public void removePlayers(Player... players) {
		removePlayers(Arrays.asList(players));
	}
	
	public void removePlayers(List<Player> players) {
		for(Player player : players) {
			playerUuids.add(player.getUniqueId());
			
			if(entityBars.containsKey(player.getUniqueId())) {
				
				entityBars.get(player.getUniqueId()).sendDestroyPacket();
				entityBars.remove(player.getUniqueId());
				
				unRegisterListener();
			} else {
				//bukkitBar.removePlayer(player);
			}
		}
	}
	
	public void removeAllPlayers() {
		playerUuids.clear();
		
		//bukkitBar.removeAll();
		
		for(UUID uuid : entityBars.keySet()) {
			if(Bukkit.getPlayer(uuid) != null) {
				entityBars.get(uuid).sendDestroyPacket();
			}
			
			entityBars.remove(uuid);
		}
	}
	
	public boolean hasPlayer(Player player) {
		return playerUuids.contains(player.getUniqueId());
	}
	
	public boolean hasBar(Player player) {
		return entityBars.containsKey(player.getUniqueId()); //|| bukkitBar.getPlayers().contains(player);
	}
	
	public List<Player> getOnlinePlayers() {
		List<Player> l = new ArrayList<>();
		
		for(UUID uuid : playerUuids) {
			Player p = Bukkit.getPlayer(uuid);
			if(p == null) continue;
			
			l.add(p);
		}
		
		return l;
	}
	
	public void setText(String text) {
		this.text = text;
		
		addBars();
		
		//bukkitBar.setTitle(text);
		
		for(EntityBar eb : entityBars.values()) {
			eb.setText(text);
		}
	}
	
	public void setProgress(double progress) {
		this.progress = progress;
		
		addBars();
		
		//bukkitBar.setProgress(progress / 100);
		
		for(EntityBar eb : entityBars.values()) {
			eb.setProgress(progress / 100);
		}
	}
	
	/*public void setBarColor(BarColor color) {
		if(KCore.getNMSVersion().isBefore(NMSVersion.v1_9_R1)) return;
		
		addBars();
		
		bukkitBar.setColor(color);
	}*/
	
	/*public void setBarStyle(BarStyle style) {
		if(KCore.getNMSVersion().isBefore(NMSVersion.v1_9_R1)) return;
		
		addBars();
		
		bukkitBar.setStyle(style);
	}*/
	
	public void startTimer(int seconds) {
		if(timerTaskId != -1) return;
		
		timerTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(KCorePlugin.getInstance(), new Runnable() {
			int time = 0;
			@Override
			public void run() {
				time++;
				
				setProgress((seconds - time) / seconds);
				
				if(time == seconds) {
					Bukkit.getScheduler().cancelTask(timerTaskId);
				}
			}
		}, 20, 20).getTaskId();
	}
	
	public void cancelTimer() {
		if(timerTaskId == -1) return;
		
		Bukkit.getScheduler().cancelTask(timerTaskId);
	}
	
	public void clearBar() {
		for(Player player : getOnlinePlayers()) {
			if(entityBars.containsKey(player.getUniqueId())) {
				
				entityBars.get(player.getUniqueId()).sendDestroyPacket();
				entityBars.remove(player.getUniqueId());
			} else {
				//bukkitBar.removePlayer(player);
			}
		}
		
		unRegisterListener();
	}
	
	//
	
	private EntityBar createEntityBar(Player player) {
		return KCore.getNMSVersion().isBefore(NMSVersion.v1_7_R1) ? new OEntityBar(player.getUniqueId(), player.getLocation(), text) : new LEntityBar(player.getUniqueId(), player.getLocation(), text);
	}
	
	private boolean isBefore1_9(Player player) {
		return KCore.isUseProtocolSupport() ? ProtocolSupportAPI.getProtocolVersion(player).isBefore(ProtocolVersion.MINECRAFT_1_9) : KCore.getNMSVersion().isBefore(NMSVersion.v1_9_R1);
	}
	
	//
	
	private void registerListener() {
		if(entityBars.size() > 0) return;
		
		Bukkit.getPluginManager().registerEvents(this, KCorePlugin.getInstance());
	}
	
	private void unRegisterListener() {
		if(entityBars.size() > 0) return;
		
		PluginUtil.unRegisterListener(this);
	}
	
	//
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		EntityBar eb = entityBars.get(e.getPlayer().getUniqueId());
		if(eb == null) return;
		
		eb.setLocation(e.getTo());
	}
	
	@EventHandler
	public void onWorldChanged(PlayerChangedWorldEvent e) {
		EntityBar eb = entityBars.get(e.getPlayer().getUniqueId());
		if(eb == null) return;
		
		eb.setLocation(e.getPlayer().getLocation());
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		EntityBar eb = entityBars.get(e.getPlayer().getUniqueId());
		if(eb == null) return;
		
		eb.setLocation(e.getTo());
	}
	
}