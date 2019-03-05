package su.plugin.glogin.bungee.api;

import java.util.ArrayList;
import java.util.List;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.glogin.bungee.GGLoginPlugin;
import su.plugin.glogin.bungee.api.manager.TaskManager;
import su.plugin.glogin.bungee.api.manager.TitleManager;
import su.plugin.glogin.common.api.GLoginAPI;
import su.plugin.glogin.common.api.manager.AccountManager;
import su.plugin.glogin.common.api.manager.SQLManager;
import su.plugin.core.bungee.api.GCore;
import su.plugin.core.bungee.api.task.PluginMessageTask;
import su.plugin.core.common.api.player.PlayerKey;

public class GGLoginAPI extends GLoginAPI {
	
	@Setter
	@Getter
	private static String allowNicknameRegex;
	
	@Setter
	@Getter
	private static boolean forceLoginOnConnect, excludeLoginIfOnlineMode;
	
	@Setter
	@Getter
	private static int maxAccountPerIp, loginTimeout;
	
	@Setter
	@Getter
	private static List<String> exceptionCommands = new ArrayList<>();
	
	@Getter
	private static TitleManager titleManager;
	@Getter
	private static TaskManager taskManager;
	
	public void init() {
		accountManager = new AccountManager();
		titleManager = new TitleManager();
		taskManager = new TaskManager();
		SQLManager = new SQLManager();
	}
	
	public void sendLoginToServer(PlayerKey playerKey, boolean login) {
		ProxiedPlayer p = GCore.getProxiedPlayer(playerKey);
		if(p == null || p.getServer() == null) return;
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Login");
		out.writeInt(playerKey.getId());
		out.writeBoolean(login);
		
		new PluginMessageTask(GGLoginPlugin.getInstance(), p.getServer().getInfo(), "U-GLogin", out.toByteArray()).runAsync();
	}
	
}