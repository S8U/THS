package su.plugin.core.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.bukkit.api.event.player.FirstPlayerJoinEvent;
import su.plugin.core.bukkit.api.util.BungeeUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.option.UPlayerOptionChangeEvent;
import su.plugin.core.common.api.event.c.option.UPlayerOptionDeleteEvent;
import su.plugin.core.common.api.event.c.option.UServerOptionChangeEvent;
import su.plugin.core.common.api.event.c.option.UServerOptionDeleteEvent;
import su.plugin.core.common.api.player.PlayerKey;

public class BungeeOptionListener implements PluginMessageListener, UEventListener {
	
	@EventHandler
	public void onFirstPlayerJoin(FirstPlayerJoinEvent e) {
		Core.getOptionSQLManager().loadServerOptions();
	}
	
	//
	
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		if(!channel.equals("ucore:main")) return;
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		
		String task = in.readUTF();
		
		if(task.equals("PlayerOptionChange")) {
			String player = in.readUTF();
			String option = in.readUTF();
			
			if(Bukkit.getPlayer(player) == null) return;
			
			Core.getOptionSQLManager().loadPlayerOption(PlayerKey.getPlayerKey(player), option);
		} else if(task.equals("PlayerOptionDelete")) {
			String player = in.readUTF();
			String option = in.readUTF();
			
			if(Bukkit.getPlayer(player) == null) return;
			
			Core.getOptionManager().deletePlayerOption(PlayerKey.getPlayerKey(player), option, false);
		} else if(task.equals("ServerOptionChange")) {
			String option = in.readUTF();
			
			Core.getOptionSQLManager().loadServerOption(option);
		} else if(task.equals("ServerOptionDelete")) {
			String option = in.readUTF();
			
			Core.getOptionManager().deleteServerOption(option, false);
		}
		
	}
	
	//
	
	@UEventHandler
	public void onUPlayerOptionChangeEvent(UPlayerOptionChangeEvent e) {
		if(!Core.getOptionSQLManager().isUseBungeeSync()) return;

		BungeeUtil.sendMessageToBungeeCord(KCorePlugin.getInstance(), "ucore:main", "PlayerOptionChange", e.getPlayer().getName(), e.getName());
	}
	
	@UEventHandler
	public void onUPlayerOptionDeleteEvent(UPlayerOptionDeleteEvent e) {
		if(!Core.getOptionSQLManager().isUseBungeeSync()) return;

		BungeeUtil.sendMessageToBungeeCord(KCorePlugin.getInstance(), "ucore:main", "PlayerOptionDelete", e.getPlayer().getName(), e.getName());
	}
	
	@UEventHandler
	public void onUServerOptionChangeEvent(UServerOptionChangeEvent e) {
		if(!Core.getOptionSQLManager().isUseBungeeSync()) return;

		BungeeUtil.sendMessageToBungeeCord(KCorePlugin.getInstance(), "ucore:main", "ServerOptionChange", e.getName());
	}
	
	@UEventHandler
	public void onUServerOptionDeleteEvent(UServerOptionDeleteEvent e) {
		if(!Core.getOptionSQLManager().isUseBungeeSync()) return;

		BungeeUtil.sendMessageToBungeeCord(KCorePlugin.getInstance(), "ucore:main", "ServerOptionDelete", e.getName());
	}

}