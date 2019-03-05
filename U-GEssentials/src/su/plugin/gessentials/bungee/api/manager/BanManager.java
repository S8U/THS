package su.plugin.gessentials.bungee.api.manager;

import java.util.HashMap;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.object.ban.EBan;
import su.plugin.gessentials.bungee.api.object.ban.EIpBan;
import su.plugin.gessentials.bungee.api.object.ban.EPlayerKeyBan;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public class BanManager {
	
	private GGEssentialsAPI api = GGEssentialsPlugin.getApi();
	
	@Getter
	private HashMap<String, EBan> banDatas = new HashMap<>();
	
	public void setBanData(String key, EBan ban) {
		banDatas.put(key.toLowerCase(), ban);
	}
	
	public void deleteBanData(String key) {
		banDatas.remove(key.toLowerCase());
	}
	
	public boolean hasBanData(String key) {
		return banDatas.containsKey(key.toLowerCase());
	}
	
	public EBan getBanData(String key) {
		return banDatas.get(key.toLowerCase());
	}
	
	// BAN
	
	public boolean isBannedPlayerKey(PlayerKey playerKey) {
		EPlayerKeyBan ban = api.isLoadAllBanData() ? (EPlayerKeyBan) getBanData(playerKey.getId() + "") : api.getSQLManager().getPlayerKeyBanData(playerKey);
		return ban != null && ban.isEffective();
	}
	
	public boolean banPlayerKey(PlayerKey playerKey, UCommandSender sender, String reason) {
		return banPlayerKey(playerKey, sender, reason, 0);
	}
	
	public boolean banPlayerKey(PlayerKey playerKey, UCommandSender sender, String reason, long duration) {
		if(isBannedPlayerKey(playerKey)) return false;
		
		EPlayerKeyBan ban = new EPlayerKeyBan(playerKey, sender == null ? -2 : (sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId()), System.currentTimeMillis(), duration, reason);
		
		setBanData(playerKey.getId() + "", ban);
		
		ProxyServer.getInstance().getScheduler().runAsync(GGEssentialsPlugin.getInstance(), () -> {
			api.getSQLManager().savePlayerKeyBanData(ban);
			api.getSQLManager().writeBanLog(ban);
		});
		
		return true;
	}
	
	public boolean banPlayer(String name, UCommandSender sender, String reason) {
		return banPlayer(name, sender, reason, 0);
	}
	
	public boolean banPlayer(String name, UCommandSender sender, String reason, long duration) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(name);
		if(playerKey == null) return false;
		
		return banPlayerKey(playerKey, sender, reason, duration);
	}
	
	public boolean isBannedIp(String ip) {
		EIpBan ban = api.isLoadAllBanData() ? (EIpBan) getBanData(ip) : api.getSQLManager().getIpBanData(ip);
		return ban != null && ban.isEffective();
	}
	
	public boolean banIp(String ip, UCommandSender sender, String reason) {
		return banIp(ip, sender, reason, 0);
	}
	
	public boolean banIp(String ip, UCommandSender sender, String reason, long duration) {
		if(isBannedIp(ip)) return false;
		EIpBan ban = new EIpBan(ip, sender == null ? -2 : (sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId()), System.currentTimeMillis(), duration, reason);
		
		setBanData(ip, ban);
		
		ProxyServer.getInstance().getScheduler().runAsync(GGEssentialsPlugin.getInstance(), () -> {
			api.getSQLManager().saveIpBanData(ban);
			api.getSQLManager().writeIpBanLog(ban);
		});
		
		return true;
	}
	
	public boolean banPlayerIp(String name, UCommandSender sender, String reason) {
		return banIp(name, sender, reason, 0);
	}
	
	public boolean banPlayerIp(String name, UCommandSender sender, String reason, long duration) {
		String ip = api.isLoadAllPlayerData() ? api.getPlayerManager().getPlayerIp(name) : api.getSQLManager().getEPlayerIp(name);
		if(ip == null) return false;
		
		return banIp(ip, sender, reason, duration);
	}
	
	// UNBAN
	
	public boolean unBanPlayerKey(PlayerKey playerKey, UCommandSender sender) {
		if(!isBannedPlayerKey(playerKey)) return false;
		
		deleteBanData(playerKey.getId() + "");
		
		ProxyServer.getInstance().getScheduler().runAsync(GGEssentialsPlugin.getInstance(), () -> {
			api.getSQLManager().deletePlayerKeyBanData(playerKey);
			api.getSQLManager().writeUnBanLog(playerKey, sender == null ? -2 : (sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId()));
		});
		
		return true;
	}
	
	public boolean unBanPlayer(String name, UCommandSender sender) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(name);
		if(playerKey == null) return false;
		
		return unBanPlayerKey(playerKey, sender);
	}
	
	public boolean unBanIp(String ip, UCommandSender sender) {
		if(!isBannedIp(ip)) return false;
		
		deleteBanData(ip);
		
		ProxyServer.getInstance().getScheduler().runAsync(GGEssentialsPlugin.getInstance(), () -> {
			api.getSQLManager().deleteIpBanData(ip);
			api.getSQLManager().writeUnIpBanLog(ip, sender == null ? -2 : (sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId()));
		});
		
		return true;
	}
	
	public boolean unBanPlayerIp(String name, UCommandSender sender) {
		String ip = api.isLoadAllPlayerData() ? api.getPlayerManager().getPlayerIp(name) : api.getSQLManager().getEPlayerIp(name);
		
		return unBanIp(ip, sender);
	}
	
}