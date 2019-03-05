package su.plugin.core.common.api.command;

import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public abstract class UCommandSender {
	
	protected String displayName;
	
	public abstract String getName();
	
	public String getDisplayName() {
		return displayName == null ? getName() : displayName;
	}
	
	public boolean hasDisplayName() {
		return displayName != null;
	}
	
	public abstract boolean isConsole();
	
	public abstract boolean hasPermission(String node);
	
	public abstract Object getPlatformSender();
	
	public void msg(Object... messages) {
		Core.msg(getPlatformSender(), messages);
	}

	public void nmsg(Object... messages) {
		Core.nmsg(getPlatformSender(), messages);
	}

	public void wmsg(Object... messages) {
		Core.wmsg(getPlatformSender(), messages);
	}
	
	public void cmsg(ChatColor color, Object... messages) {
		Core.cmsg(getPlatformSender(), color, messages);
	}
	
}