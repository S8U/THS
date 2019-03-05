package su.plugin.core.bukkit.api.command;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import su.plugin.core.common.api.command.UConsoleSender;

public class KConsoleSender extends UConsoleSender {
	
	@Override
	public ConsoleCommandSender getPlatformSender() {
		return Bukkit.getConsoleSender();
	}

}