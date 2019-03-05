package su.plugin.gcmdlocker.api;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;
import su.plugin.gcmdlocker.api.manager.SQLManager;

public class GCMDLockerAPI {
	
	@Setter
	@Getter
	private static String password;
	
	@Setter
	@Getter
	private static boolean useGLogin, useNotify, useKeepLogin;
	
	@Setter
	@Getter
	private static List<PlayerKey> logged = new NotDuplicatedArrayList<>();
	@Setter
	@Getter
	private static List<String> loggedIp = new NotDuplicatedArrayList<>();
	@Setter
	@Getter
	private static List<String> blacklistedCommand = new NotDuplicatedArrayList<>();

	@Getter
	private static SQLManager SQLManager;
	
	public void init() {
		SQLManager = new SQLManager();
	}
	
	public static boolean isCorrectPassword(String pw) {
		return password.equals(pw);
	}
	
	public static boolean isLogged(PlayerKey playerKey) {
		return logged.contains(playerKey);
	}
	
	public static boolean login(ProxiedPlayer p) {
		PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);

		if(!logged.add(playerKey)) return  false;
		if(useKeepLogin) {
			loginIp(playerKey.getUPlayer().getIp());
		}

		Core.msg(p, "§bCMDLocker에 로그인되었습니다.");

		if(useNotify) {
			for(PlayerKey lpk : logged) {
				if(playerKey.equals(lpk) || lpk.getPlatformPlayer() == null) continue;

				Core.msg(lpk.getPlatformPlayer(), playerKey.getDisplayName() + (playerKey.getUPlayer().hasDisplayName() ? "(" + playerKey.getName() + ")" : "") + "님께서 로그인했습니다.");
			}
		}

		SQLManager.writeLog(playerKey, p.getAddress().getAddress().getHostAddress(), "login");

		return true;
	}
	
	public static boolean logout(ProxiedPlayer p) {
		PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);

		if(!logged.remove(playerKey)) return  false;

		Core.msg(p, "§bCMDLocker에서 로그아웃되었습니다.");

		if(useNotify) {
			for(PlayerKey lpk : logged) {
				if(playerKey.equals(lpk) || lpk.getPlatformPlayer() == null) continue;

				Core.msg(lpk.getPlatformPlayer(), playerKey.getDisplayName() + (playerKey.getUPlayer().hasDisplayName() ? "(" + playerKey.getName() + ")" : "") + "님께서 로그아웃했습니다.");
			}
		}

		SQLManager.writeLog(playerKey, p.getAddress().getAddress().getHostAddress(), "logout");

		return true;
	}
	
	public static boolean isLoggedIp(String ip) {
		return loggedIp.contains(ip);
	}
	
	public static boolean loginIp(String ip) {
		return loggedIp.add(ip);
	}
	
	public static boolean logoutIp(String ip) {
		return loggedIp.remove(ip);
	}
	
	public static boolean isBlackListedCommand(String command) {
		command = command.substring(1, command.length()).toLowerCase();
		for(String tc : blacklistedCommand) {
			if(command.startsWith(tc.toLowerCase())) return true;
		}
		return false;
	}
	
}