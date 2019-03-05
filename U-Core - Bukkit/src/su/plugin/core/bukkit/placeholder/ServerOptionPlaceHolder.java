package su.plugin.core.bukkit.placeholder;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.common.api.Core;

public class ServerOptionPlaceHolder extends EZPlaceholderHook {

	public ServerOptionPlaceHolder() {
		super(KCorePlugin.getInstance(), "serveroption");
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		Object option = Core.getOptionManager().getServerOption(identifier);
		if(option == null) return null;
		
		return option.toString();
	}
	
}