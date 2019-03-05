package su.plugin.core.common.platform;

import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;

public interface PlatformProvider {
	
	UCommandSender getUCommandSender(Object sender);
	
	UPlayer getUPlayer(Object player);
	
	String getPlatformPlayerName(Object platformPlayer);
	
	void nlog(Object message);
	
	void nmsg(Object sender, Object message);
	
	void nbc(Object message);
	
	Object makeComponent(boolean useColor, Object... messages);
	
}