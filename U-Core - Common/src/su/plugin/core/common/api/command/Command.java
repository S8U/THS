package su.plugin.core.common.api.command;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.plugin.UPlugin;

@Setter
@Getter
@RequiredArgsConstructor
public abstract class Command {
	
	protected final UPlugin plugin;
	
	protected final String name;
	protected List<String> aliases = new ArrayList<>();
	protected String additional, usage;
	
	protected int minArgs, maxArgs;
	
	protected boolean usePlatformPrefix;
	
	protected String permission;
	protected String noPermissionMessage;
	
	protected String playerOnlyMessage;
	protected String consoleOnlyMessage;

	//

	public abstract void sendUsage(UCommandSender sender, boolean format);

	public abstract void sendUsage(UCommandSender sender, String entered, boolean format);

	public abstract void sendUsage(UCommandSender sender, boolean format, ChatColor color);

	public abstract void sendUsage(UCommandSender sender, String entered, boolean format, ChatColor color);


	public abstract boolean sendUsageIfHasPermission(UCommandSender sender, boolean format);

	public abstract boolean sendUsageIfHasPermission(UCommandSender sender, String entered, boolean format);

	public abstract boolean sendUsageIfHasPermission(UCommandSender sender, boolean format, ChatColor color);

	public abstract boolean sendUsageIfHasPermission(UCommandSender sender, String entered, boolean format, ChatColor color);
	
}