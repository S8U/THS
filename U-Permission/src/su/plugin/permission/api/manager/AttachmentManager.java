package su.plugin.permission.api.manager;

import java.util.HashMap;

import org.bukkit.permissions.PermissionAttachment;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.common.api.player.PlayerKey;

public class AttachmentManager {
	
	@Setter
	@Getter
	private HashMap<PlayerKey, PermissionAttachment> permissionAttachments = new HashMap<>();
	
	public void setAttachment(PlayerKey playerKey, PermissionAttachment attachment) {
		permissionAttachments.put(playerKey, attachment);
	}
	
	public void removeAttachment(PlayerKey playerKey) {
		permissionAttachments.remove(playerKey);
	}
	
	public boolean hasAttachment(PlayerKey playerKey) {
		return permissionAttachments.containsKey(playerKey);
	}
	
	public PermissionAttachment getAttachment(PlayerKey playerKey) {
		return permissionAttachments.get(playerKey);
	}
	
}