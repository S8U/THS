package su.plugin.core.bungee.api.command;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.command.ConsoleCommandSender;
import su.plugin.core.common.api.command.UConsoleSender;

public class GConsoleSender extends UConsoleSender {
	
	@Override
	public ConsoleCommandSender getPlatformSender() {
		return (ConsoleCommandSender) ProxyServer.getInstance().getConsole();
	}

}