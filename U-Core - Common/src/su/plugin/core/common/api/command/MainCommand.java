package su.plugin.core.common.api.command;

import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.plugin.UPlugin;

public class MainCommand extends Command {
	
	public MainCommand(UPlugin plugin, String name) {
		super(plugin, name);
	}
	
	@Override
	public void sendUsage(UCommandSender sender, boolean format) {
		sendUsage(sender, name, format, plugin.getColor());
	}

	@Override
	public void sendUsage(UCommandSender sender, String entered, boolean format) {
		sendUsage(sender, entered, format, plugin.getColor());
	}

	@Override
	public void sendUsage(UCommandSender sender, boolean format, ChatColor color) {
		sendUsage(sender, name, format, color);
	}
	
	@Override
	public void sendUsage(UCommandSender sender, String entered, boolean format, ChatColor color) {
		String r = "§f/" + entered + (additional == null ? "" : " " + additional) + color + " - " + usage;
		
		if(format) {
			Core.msgc(sender, getPlugin().getPluginPackage(), r);
			return;
		}
		
		Core.nmsg(sender, r);
	}
	
	@Override
	public boolean sendUsageIfHasPermission(UCommandSender sender, boolean format) {
		return sendUsageIfHasPermission(sender, name, format, plugin.getColor());
	}

	@Override
	public boolean sendUsageIfHasPermission(UCommandSender sender, String entered, boolean format) {
		return sendUsageIfHasPermission(sender, entered, format, plugin.getColor());
	}

	@Override
	public boolean sendUsageIfHasPermission(UCommandSender sender, boolean format, ChatColor color) {
		return sendUsageIfHasPermission(sender, name, format, color);
	}
	
	@Override
	public boolean sendUsageIfHasPermission(UCommandSender sender, String entered, boolean format, ChatColor color) {
		if(permission != null && !sender.hasPermission(permission)) return false;
		
		String r = "§f/" + entered + (additional == null ? "" : " " + additional) + color + " - " + usage;
		
		if(format) {
			Core.msgc(sender, getPlugin().getPluginPackage(), r);
		} else {
			Core.nmsg(sender, r);
		}
		
		return true;
	}
	
}