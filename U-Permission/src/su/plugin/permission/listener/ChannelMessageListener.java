package su.plugin.permission.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.event.ChannelMessageEvent;
import su.plugin.core.bukkit.api.player.KPlayer;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.permission.PermissionPlugin;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionGroup;
import su.plugin.permission.api.object.PermissionPlayer;

public class ChannelMessageListener implements Listener {
	
	private PermissionAPI api = PermissionPlugin.getApi();
	
	@EventHandler
	public void onChannelMessage(ChannelMessageEvent e) {
		if(!e.getKey().equals("U-Permission")) return;
		
		String task = e.getTask();
		
		if(task.equals("GroupUpdate")) {
			String groupName = e.getByteArrayDataInput().readUTF();
			
			api.getSQLManager().loadGroup(groupName);
		} else if(task.equals("GroupDelete")) {
			String groupName = e.getByteArrayDataInput().readUTF();
			
			if(!api.getGroupManager().existsGroup(groupName)) return;
			
			PermissionGroup group = api.getGroupManager().getGroup(groupName);
			
			for(PermissionPlayer pp : group.getOnlinePlayers()) {
				pp.setGroupName(null);
				
				pp.updatePermissionAttachment();
			}
			
			api.getGroupManager().removeGroup(groupName);
		} else if(task.equals("DefaultGroupChange")) {
			api.getSQLManager().loadConfig();
		} else if(task.equals("PlayerChange")) {
			KPlayer p = (KPlayer) KCore.getPlayer(PlayerKey.getPlayerKey(e.getByteArrayDataInput().readInt()));
			if(p == null) return;
			
			api.getPlayerManager().registerPlayer(p.getPlatformSender());
		}
	}
	
}