package su.plugin.prefixer.api;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.api.util.ChannelMessageUtil;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.plugin.UPlugin;
import su.plugin.prefixer.PrefixerPlugin;
import su.plugin.prefixer.api.category.ChangeAction;
import su.plugin.prefixer.api.event.MainPrefixChangeEvent;
import su.plugin.prefixer.api.event.PrefixChangedEvent;
import su.plugin.prefixer.api.manager.HologramManager;
import su.plugin.prefixer.api.manager.PlayerManager;
import su.plugin.prefixer.api.manager.SQLManager;
import su.plugin.prefixer.api.object.PrefixPlayer;

public class PrefixerAPI {
	
	@Setter
	@Getter
	private static int maxMainPrefixCount, hologramShowInterval;
	
	@Getter
	private static double hologramY = 2.56;
	
	@Getter
	private static boolean hideHologramOnMove, useHologram, useBungeecord, usePermission, useProtocolLib;
	
	
	@Getter
	private static HologramManager hologramManager;
	@Getter
	private static PlayerManager playerManager;
	@Getter
	private static SQLManager SQLManager;
	
	public void init() {
		hologramManager = new HologramManager();
		playerManager = new PlayerManager();
		SQLManager= new SQLManager();
	}
	
	public void registerPlugins() {
		if(usePermission = PluginUtil.existsPlugin("U-Permission")) {
			Core.log("U-Permisison 플러그인과 연동되었습니다.");
		}
		
		if(useProtocolLib = PluginUtil.existsPlugin("ProtocolLib")) {
			Core.log("ProtocolLib 플러그인과 연동되었습니다.");
		}
	}
	
	public void loadConfig(UPlugin plugin) {
		plugin.getJsonConfig().addDefault("칭호 최대 착용 수", 3);
		plugin.getJsonConfig().addDefault("번지코드 채널 연동", false);
		plugin.getJsonConfig().addDefault("홀로그램 사용", true);
		plugin.getJsonConfig().addDefault("이동 중에 자기 홀로그램 가리기.사용", true);
		plugin.getJsonConfig().addDefault("이동 중에 자기 홀로그램 가리기.다시 보기 간격(ms)", 500);
		
		plugin.getJsonConfig().save();
		
		maxMainPrefixCount = plugin.getJsonConfig().getInt("칭호 최대 착용 수");
		useBungeecord = plugin.getJsonConfig().getBoolean("번지코드 채널 연동");
		useHologram = plugin.getJsonConfig().getBoolean("홀로그램 사용");
		hideHologramOnMove = plugin.getJsonConfig().getBoolean("이동 중에 자기 홀로그램 가리기.사용");
		hologramShowInterval = plugin.getJsonConfig().getInt("이동 중에 자기 홀로그램 가리기.다시 보기 간격(ms)");
		
		if(!useProtocolLib && useHologram) {
			useHologram = false;
			Core.log("ProtocolSupport 플러그인을 찾을 수 없어 홀로그램 기능이 비활성화됩니다.");
		}
		
		Core.log("설정을 불러왔습니다.");
	}
	
	public static boolean addPrefix(PlayerKey playerKey, String prefix) {
		PrefixPlayer pp = playerManager.getPrefixPlayer(playerKey);
		
		if(pp == null ? SQLManager.hasPrefix(playerKey, prefix) : pp.hasPrefix(prefix)) return false;
		
		PrefixChangedEvent event = new PrefixChangedEvent(pp, prefix, ChangeAction.ADD);
		Bukkit.getPluginManager().callEvent(event);
		
		String ePrefix = event.getPrefix();
		
		if(pp != null) {
			pp.addPrefix(ePrefix);
		} else if(useBungeecord) {
			sendUpdatePrefixToPlayerChannel(playerKey);
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(PrefixerPlugin.getInstance(), () -> {
			SQLManager.addPrefix(playerKey, ePrefix);
			
			SQLManager.writePrefixLog(playerKey, "add", ePrefix);
		});
		
		
		return true;
	}
	
	public static void deletePrefix(PlayerKey playerKey, String prefix) {
		PrefixPlayer pp = playerManager.getPrefixPlayer(playerKey);
		
		PrefixChangedEvent event = new PrefixChangedEvent(pp, prefix, ChangeAction.DELETE);
		Bukkit.getPluginManager().callEvent(event);
		
		if(pp != null) {
			pp.deletePrefix(prefix);
		}
		
		removeMainPrefix(playerKey, prefix);
		
		Bukkit.getScheduler().runTaskAsynchronously(PrefixerPlugin.getInstance(), () -> {
			SQLManager.deletePrefix(playerKey, prefix);
			
			SQLManager.writePrefixLog(playerKey, "delete", prefix);
		});
	}
	
	public static boolean hasPrefix(PlayerKey playerKey, String prefix) {
		PrefixPlayer pp = playerManager.getPrefixPlayer(playerKey);
		
		return pp == null ? SQLManager.hasPrefix(playerKey, prefix) : pp.hasPrefix(prefix);
	}
	
	public static void setMainPrefix(PlayerKey playerKey, int priority, String prefix) {
		PrefixPlayer pp = playerManager.getPrefixPlayer(playerKey);
		
		MainPrefixChangeEvent event = new MainPrefixChangeEvent(pp, priority, prefix);
		Bukkit.getPluginManager().callEvent(event);
		
		int ePriority = event.getPriority();
		String ePrefix = event.getPrefix();
		
		if(pp == null ? SQLManager.isMainPrefixes(playerKey, prefix) : pp.isMainPrefix(prefix)) return;
		else if(pp != null) {
			if(pp.isMainPrefix(ePrefix)) return;
			pp.setMainPrefix(ePriority, ePrefix);
			
			if(isUseHologram()) {
				Hologram holo = pp.getHologram();
				
				if(holo == null) {
					holo = HologramsAPI.createHologram(PrefixerPlugin.getInstance(), pp.getMainPrefixLocation());
					
					for(String pf : pp.getMainPrefixList()) {
						holo.appendTextLine(pf);
					}
					
					holo.getVisibilityManager().setVisibleByDefault(true);
					
					pp.setHologram(holo);
				} else {
					holo.clearLines();
					
					for(String pf : pp.getMainPrefixList()) {
						holo.appendTextLine(pf);
					}
				}
			}
		} else if(useBungeecord) {
			sendUpdatePrefixToPlayerChannel(playerKey);
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(PrefixerPlugin.getInstance(), () -> {
			SQLManager.setMainPrefix(playerKey, ePriority, ePrefix);
			
			SQLManager.writeMainPrefixLog(playerKey, "delete", prefix);
		});
	}
	
	public static void removeMainPrefix(PlayerKey playerKey, String prefix) {
		PrefixPlayer pp = playerManager.getPrefixPlayer(playerKey);
		
		if(pp != null) {
			pp.removeMainPrefix(prefix);
			
			if(isUseHologram()) {
				Hologram holo = pp.getHologram();
				
				if(pp.getMainPrefixes().size() < 1) {
					pp.removeHologram();
				} else {
					holo.clearLines();
					
					for(String pf : pp.getMainPrefixList()) {
						holo.appendTextLine(pf);
					}
				}
			}

		}
		
		Bukkit.getScheduler().runTaskAsynchronously(PrefixerPlugin.getInstance(), () -> {
			SQLManager.removeMainPrefix(playerKey, prefix);
			
			SQLManager.writeMainPrefixLog(playerKey, "remove", prefix);
		});
	}
	
	public static HashMap<Integer, String> getMainPrefix(PlayerKey playerKey) {
		PrefixPlayer pp = playerManager.getPrefixPlayer(playerKey, true);
		
		return pp == null ? null : pp.getMainPrefixes();
	}
	
	public static List<String> getMainPrefixList(PlayerKey playerKey) {
		PrefixPlayer pp = playerManager.getPrefixPlayer(playerKey, true);
		
		return pp == null ? null : pp.getMainPrefixList();
	}
	
	public static void sendUpdatePrefixToPlayerChannel(PlayerKey playerKey) {
		if(!useBungeecord) return;
		
		ChannelMessageUtil.sendToChannelHasPlayer(playerKey.getName(), "U-Prefix", "PrefixUpdate", playerKey);
	}
	
}