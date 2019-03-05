package su.plugin.core.common.api.player;

import lombok.Getter;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.UCommandSender;

public abstract class UPlayer extends UCommandSender {
	
	@Getter
	protected String name, displayName;
	
	public abstract PlayerKey getPlayerKey();
	
	public abstract void setDisplayName(String displayName, boolean sql);
	
	public void setDisplayName(String displayName) {
		setDisplayName(displayName, true);
	}
	
	public String getDisplayName() {
		return displayName == null ? getName() : displayName;
	}
	
	public boolean hasDisplayName() {
		return displayName != null;
	}
	
	public abstract String getIp();
	
	public abstract boolean isOnline();

	public boolean isOnlineMode() {
		return getPlayerKey().isOnlineMode();
	}
	
	public abstract void kickPlayer(String message);
	
	public abstract void sendPluginMessage(String channel, byte... data);
	
	public void setOption(String optionName, Object value) {
		Core.getOptionManager().setPlayerOption(getPlayerKey(), optionName, value);
	}
	
	public boolean existsOption(String optionName) {
		return Core.getOptionManager().existsPlayerOption(getPlayerKey(), optionName);
	}
	
	public Object getOption(String optionName) {
		return Core.getOptionManager().getPlayerOption(getPlayerKey(), optionName);
	}
	
}