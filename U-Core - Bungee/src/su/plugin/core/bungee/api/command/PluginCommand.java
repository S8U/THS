package su.plugin.core.bungee.api.command;

import java.lang.reflect.Method;

import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.MainCommand;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.command.UConsoleSender;
import su.plugin.core.common.api.command.UnregisterableCommandListener;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.StringUtil;

public class PluginCommand extends net.md_5.bungee.api.plugin.Command implements UnregisterableCommandListener {
	
	public PluginCommand(String name) {
		super(name);
	}
	
	public PluginCommand(String name, String permission, String...aliases) {
		super(name, permission, aliases);
	}
	
	@SneakyThrows(Exception.class)
	@Override
	public void execute(CommandSender sender, String[] args) {
		String commandLine = getName() + (args.length < 1 ? "" : " " + String.join(" ", args));
		
		Command command = Core.getCommandManager().getCommand(commandLine);
		if(command == null) return;
		
		String commandString = command instanceof MainCommand ? command.getName().toLowerCase() : ((SubCommand) command).getCommand().toLowerCase();
		
		Object listener = command instanceof MainCommand ? Core.getCommandManager().getMainCommandListeners().get(commandString) : Core.getCommandManager().getSubCommandListeners().get(commandString);
		Method method = command instanceof MainCommand ? Core.getCommandManager().getMainCommandMethods().get(commandString) : Core.getCommandManager().getSubCommandMethods().get(commandString);
		
		//
		
		UCommandSender uSender = Core.getUCommandSender(sender);
		
		if((method.getParameterTypes()[0].equals(ProxiedPlayer.class) && !(sender instanceof ProxiedPlayer)) || (method.getParameterTypes()[0].equals(UPlayer.class) && !(uSender instanceof UPlayer))) {
			Core.wmsgc(sender, command.getPlugin().getPluginPackage(), command.getPlayerOnlyMessage());
			return;
		} else if((method.getParameterTypes()[0].equals(ConsoleCommandSender.class) && !(sender instanceof ConsoleCommandSender)) || (method.getParameterTypes()[0].equals(UConsoleSender.class) && !(sender instanceof UConsoleSender))) {
			Core.wmsgc(sender, command.getPlugin().getPluginPackage(), command.getConsoleOnlyMessage());
			return;
		} else if(command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
			Core.wmsgc(sender, command.getPlugin().getPluginPackage(), command.getNoPermissionMessage());
			return;
		}
		
		String[] newArgs = args;
		if(command instanceof SubCommand) {
			int length = StringUtil.countMatches(((SubCommand) command).getCommand(), " ");
			newArgs = new String[args.length - length];
			
			for(int i = length; i < args.length; i++) {
				newArgs[i - length] = args[i];
			}
		}
		
		if(newArgs.length < command.getMinArgs() || (command.getMaxArgs() != -1 && newArgs.length > command.getMaxArgs())) {
			if(command.getUsage() == null) {
				Core.wmsgc(sender, command.getPlugin().getPluginPackage(), "명령어 사용 조건에 충족되지 못했습니다.");
				return;
			}
			
			command.sendUsage(uSender, true);
			return;
		}
		
		if(method.getParameterCount() < 3) {
			method.invoke(listener, (CommandSender.class.isAssignableFrom(method.getParameterTypes()[0]) ? sender : uSender), newArgs);
		} else {
			method.invoke(listener, (CommandSender.class.isAssignableFrom(method.getParameterTypes()[0]) ? sender : uSender), newArgs, command);
		}
	}
	
}