package su.plugin.core.bukkit.placeholder;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class PlayerOptionPlaceHolder extends EZPlaceholderHook {
	
	public PlayerOptionPlaceHolder() {
		super(KCorePlugin.getInstance(), "playeroption");
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		Object option = Core.getOptionManager().getPlayerOption(PlayerKey.getPlayerKey(p.getName()), identifier);
		if(option == null) return null;
		
		return option.toString();
	}
	
}