package su.plugin.lobbysystem;

import lombok.Getter;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.lobbysystem.api.LobbySystemAPI;
import su.plugin.lobbysystem.api.task.SideBarTask;
import su.plugin.lobbysystem.api.task.TimeLockTask;
import su.plugin.lobbysystem.listener.ChannelListener;

public class LobbySystemPlugin extends UKPlugin {
	
	@Getter
	private static LobbySystemPlugin instance;
	@Getter
	private static LobbySystemAPI api = new LobbySystemAPI();
	
	public void onUEnable() {
		instance = this;
		setPrefix("§e[ U-LobbySystem ]");
		setColor(ChatColor.YELLOW);

		api.loadConfig();
		
		registerListeners();
		registerCommands();
		Core.getCommandManager().getMainCommand("속도").setAdditional("<1 ~ " + api.getMaxSpeed() + ">");
		
		registerPermissions();

		api.registerPlugins();

		if(api.isUseTimeLock()) {
			api.setTimeLockTask(new TimeLockTask(this));
			api.getTimeLockTask().runTaskTimer(0, api.getLockInterval());
		}

		if(api.isUseSideBar()) {
			boolean b = StringUtil.hasValue("time", api.getSideBarTitle());
			for(String text : api.getSideBarTexts()) {
				if(!StringUtil.hasValue("time", text)) continue;

				b = true;
			}

			if(b) {
				api.setSideBarTask(new SideBarTask(this));
				api.getSideBarTask().runTaskTimer(0, 20);
			}

			if(api.isUseChannel()) {
				for(String text : api.getSideBarTexts()) {
					if(!(text.contains("<channel_name>") || text.contains("<channel_displayname>") || text.contains("<channel_group_name>") || text.contains("<channel_group_displayname>") || text.contains("<channel_group_player_count>")))

					b = true;
				}

				if(b) {
					registerListener(new ChannelListener());
				}
			}
		}
	}
	
	public void onUDisable() {
		api.getTimeLockTask().cancel();
		api.getSideBarTask().cancel();
	}
	
}
