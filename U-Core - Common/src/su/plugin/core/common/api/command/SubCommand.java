package su.plugin.core.common.api.command;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.plugin.UPlugin;

public class SubCommand extends Command {
	
	@Getter
	private String parentCommand;
	
	public SubCommand(UPlugin plugin, String name, String parent) {
		super(plugin, name);
		parentCommand = parent;
	}
	
	public String getCommand() {
		return getParentCommand() + " " + getName();
	}
	
	public List<String> getAliasesCommands() {
		List<String> a = new ArrayList<>();
		
		for(String s : getAliases()) {
			a.add(getParentCommand() + " " + s);
		}
		
		return a;
	}
	
	@Override
	public void sendUsage(UCommandSender sender, boolean format) {
		sendUsage(sender, getCommand(), format, plugin.getColor());
	}

	@Override
	public void sendUsage(UCommandSender sender, String entered, boolean format) {
		sendUsage(sender, entered, format, plugin.getColor());
	}

	@Override
	public void sendUsage(UCommandSender sender, boolean format, ChatColor color) {
		sendUsage(sender, getCommand(), format, color);
	}

	@Override
	public void sendUsage(UCommandSender sender, String entered, boolean format, ChatColor color) {
		String r = "§f/" + entered + (additional == null ? "" : " " + additional) + plugin.getColor() + " - " + usage;
		
		if(format) {
			Core.msgc(sender, getPlugin().getPluginPackage(), r);
			return;
		}
		
		Core.nmsg(sender, r);
	}
	
	@Override
	public boolean sendUsageIfHasPermission(UCommandSender sender, boolean format) {
		return sendUsageIfHasPermission(sender, getCommand(), format, plugin.getColor());
	}

	@Override
	public boolean sendUsageIfHasPermission(UCommandSender sender, String entered, boolean format) {
		return sendUsageIfHasPermission(sender, entered, format, plugin.getColor());
	}

	@Override
	public boolean sendUsageIfHasPermission(UCommandSender sender, boolean format, ChatColor color) {
		return sendUsageIfHasPermission(sender, getCommand(), format, color);
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