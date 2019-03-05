package su.plugin.core.bungee.listener;

import su.plugin.core.bungee.api.util.ChannelMessageUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.option.UPlayerOptionChangeEvent;
import su.plugin.core.common.api.event.c.option.UPlayerOptionDeleteEvent;
import su.plugin.core.common.api.event.c.option.UServerOptionChangeEvent;
import su.plugin.core.common.api.event.c.option.UServerOptionDeleteEvent;

public class OptionListener implements UEventListener {
	
	@UEventHandler
	public void onUPlayerOptionChangeEvent(UPlayerOptionChangeEvent e) {
		if(!Core.getOptionSQLManager().isUseBungeeSync()) return;

		ChannelMessageUtil.sendToChannelHasPlayer("PlayerOptionChange", e.getPlayer().getName(), e.getPlayer().getName(), e.getName());
	}
	
	@UEventHandler
	public void onUPlayerOptionDeleteEvent(UPlayerOptionDeleteEvent e) {
		if(!Core.getOptionSQLManager().isUseBungeeSync()) return;

		ChannelMessageUtil.sendToChannelHasPlayer("PlayerOptionDelete", e.getPlayer().getName(), e.getPlayer().getName(), e.getName());
	}
	
	@UEventHandler
	public void onUServerOptionChangeEvent(UServerOptionChangeEvent e) {
		if(!Core.getOptionSQLManager().isUseBungeeSync()) return;

		ChannelMessageUtil.sendToAllChannel("ServerOptionChange", e.getName());
	}
	
	@UEventHandler
	public void onUServerOptionDeleteEvent(UServerOptionDeleteEvent e) {
		if(!Core.getOptionSQLManager().isUseBungeeSync()) return;

		ChannelMessageUtil.sendToAllChannel("ServerOptionDelete", e.getName());
	}
	
}