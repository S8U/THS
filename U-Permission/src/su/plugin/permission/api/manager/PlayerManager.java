package su.plugin.permission.api.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.util.ChannelMessageUtil;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.permission.PermissionPlugin;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionPlayer;

public class PlayerManager {
	
	private PermissionAPI api = PermissionPlugin.getApi();
	
	@Setter
	@Getter
	private HashMap<PlayerKey, PermissionPlayer> permissionPlayers = new HashMap<>();
	
	public void setPermissionPlayer(PlayerKey playerKey, PermissionPlayer pp) {
		permissionPlayers.put(playerKey, pp);
	}
	
	public void removePermissionPlayer(PlayerKey playerKey) {
		permissionPlayers.remove(playerKey);
	}
	
	public boolean existsPermissionPlayer(PlayerKey playerKey) {
		return permissionPlayers.containsKey(playerKey);
	}
	
	public PermissionPlayer getPermissionPlayer(PlayerKey playerKey) {
		return permissionPlayers.get(playerKey);
	}
	
	public void sendPlayerChange(PlayerKey playerKey) {
		if(!PermissionAPI.isUseBungeecord() || Bukkit.getPlayer(playerKey.getName()) != null) return;
		
		ChannelMessageUtil.sendToAllChannelExistsPlayers("U-Permission", "PlayerChange", playerKey.getId());
	}
	
	public void registerPlayer(Player player) {
		PlayerKey playerKey = PlayerKey.getPlayerKey(player.getName());
		
		unRegisterPlayer(playerKey);
		
		api.getSQLManager().loadPermissionPlayer(playerKey);
		
		PermissionPlayer pp = api.getPlayerManager().existsPermissionPlayer(playerKey) ? api.getPlayerManager().getPermissionPlayer(playerKey) : new PermissionPlayer(playerKey, player.getName(), null, null, null, new ArrayList<>());
		
		pp.setName(player.getName());

		api.getPlayerManager().setPermissionPlayer(playerKey, pp);

		if(api.getGroupManager().getDefaultGroupName() != null && !pp.hasGroup()) {
			pp.setGroupName(api.getGroupManager().getDefaultGroupName());
		}

		pp.updatePermissionAttachment();
		
		Bukkit.getScheduler().runTaskAsynchronously(PermissionPlugin.getInstance(), () -> {
			api.getSQLManager().savePlayer(pp);
		});
	}
	
	public void registerAllPlayer() {
		for(Player player : KCore.getOnlinePlayers()) {
			registerPlayer(player);
		}
	}
	
	public void unRegisterPlayer(PlayerKey playerKey) {
		PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(playerKey);
		if(pp == null) return;
		else if(pp.getPermissionAttachment() != null) {
			pp.getBukkitPlayer().removeAttachment(pp.getPermissionAttachment());
			
			api.getAttachmentManager().removeAttachment(playerKey);
		}
		
		api.getPlayerManager().removePermissionPlayer(playerKey);
	}
	
}