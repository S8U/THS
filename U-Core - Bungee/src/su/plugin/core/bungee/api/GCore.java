package su.plugin.core.bungee.api;

import java.lang.reflect.Method;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.bungee.api.command.GCommandManager;
import su.plugin.core.bungee.api.command.GConsoleSender;
import su.plugin.core.bungee.platform.GHandler;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.platform.PlatformType;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public class GCore extends Core {
	
	public static void init() {
		platformType = PlatformType.BUNGEECORD;
		platformProvider = new GHandler();
		
		UConsoleCommandSender = new GConsoleSender();
		commandManager = new GCommandManager();
	}
	
	public static GCommandManager getCommandManager() {
		return (GCommandManager) commandManager;
	}
	
	public static boolean getOnlineMode(Object initialHandler) {
		if(ProxyServer.getInstance().getConfig().isOnlineMode()) return true;

		try {
			Method method = initialHandler.getClass().getMethod("isOnlineModePlayer", null);
			return (boolean) method.invoke(initialHandler, null);
		} catch (Exception e) {}
		
		return false;
	}
	
	public static ProxiedPlayer getProxiedPlayer(PlayerKey playerKey) {
		UPlayer up = getUPlayer(playerKey);
		return up == null ? null : (ProxiedPlayer) up.getPlatformSender();
	}
	
}