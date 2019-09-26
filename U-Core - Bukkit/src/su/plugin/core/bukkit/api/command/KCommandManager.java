package su.plugin.core.bukkit.api.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import su.plugin.core.bukkit.api.util.KReflectionUtil;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.MainCommand;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandManager;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.command.UConsoleSender;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.plugin.UPlugin;
import su.plugin.core.common.api.util.ReflectionUtil;
import su.plugin.core.common.api.util.StringUtil;

@Getter
public class KCommandManager extends UCommandManager implements CommandExecutor, TabCompleter {
	
	@Override
	public void registerCommands(UPlugin plugin, UCommandListener commandListener) {
		for(Method method : ReflectionUtil.getMethodsInOrder(commandListener.getClass())) {
			Class<?>[] parameters = method.getParameterTypes();
			
			if(!(parameters.length == 2 || parameters.length == 3)
					|| !(CommandSender.class.isAssignableFrom(parameters[0]) || UCommandSender.class.isAssignableFrom(parameters[0])) || !String[].class.isAssignableFrom(parameters[1])
					|| (parameters.length == 3 && !Command.class.isAssignableFrom(parameters[2]))) continue;

			if(isCommandHandler(method)) {
				CommandHandler anno = method.getAnnotation(CommandHandler.class);

				PluginCommand pc = KReflectionUtil.getCommand(anno.name(), (Plugin) plugin.getPlatformPlugin());

				if(pc == null) continue;

				pc.setExecutor(this);

				MainCommand mc = new MainCommand(plugin, anno.name());

				String[] aliases = anno.aliases()[0].isEmpty() ? null : anno.aliases();
				List<String> aliasesArr = aliases == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(aliases));

				if(anno.usePlatformPrefix()) {
					if(aliases != null) {
						for(String ac : aliases) {
							aliasesArr.add("bungee" + ac);
						}
					}

					aliasesArr.add("bungee" + anno.name());
				}

				if(!aliasesArr.isEmpty()) {
					pc.setAliases(aliasesArr);
					mc.setAliases(aliasesArr);
				}

				if(!anno.additional().isEmpty()) {
					mc.setAdditional(anno.additional());
				}

				if(!anno.description().isEmpty()) {
					pc.setDescription(anno.description());
				}

				if(!anno.usage().isEmpty()) {
					pc.setUsage(anno.usage());
					mc.setUsage(anno.usage());
				}

				mc.setMinArgs(anno.minArgs());
				mc.setMaxArgs(anno.maxArgs());

				if(!anno.permission().isEmpty()) {
					pc.setPermission(anno.permission());
					mc.setPermission(anno.permission());
				}

				if(!anno.noPermissionMessage().isEmpty()) {
					pc.setPermissionMessage(anno.noPermissionMessage());
					mc.setNoPermissionMessage(anno.noPermissionMessage());
				}

				mc.setPlayerOnlyMessage(anno.playerOnlyMessage());
				mc.setConsoleOnlyMessage(anno.consoleOnlyMessage());

				KReflectionUtil.getCommandMap().register(plugin.getName(), pc);

				mainCommands.put(anno.name().toLowerCase(), mc);
				mainCommandListeners.put(anno.name().toLowerCase(), commandListener);
				mainCommandMethods.put(anno.name().toLowerCase(), method);
			}

			if(isSubCommandHandler(method)) {
				SubCommandHandler anno = method.getAnnotation(SubCommandHandler.class);

				for(String parent : anno.parent()) {
					SubCommand sc = new SubCommand(plugin, anno.name(), parent);
					if(!anno.aliases().equals(new String[] { "" })) {
						sc.setAliases(Arrays.asList(anno.aliases()));
					}

					if(!anno.additional().isEmpty()) {
						sc.setAdditional(anno.additional());
					}

					if(!anno.usage().isEmpty()) {
						sc.setUsage(anno.usage());
					}

					sc.setMinArgs(anno.minArgs());
					sc.setMaxArgs(anno.maxArgs());

					if(!anno.permission().isEmpty()) {
						sc.setPermission(anno.permission());
					}

					if(!anno.noPermissionMessage().isEmpty()) {
						sc.setNoPermissionMessage(anno.noPermissionMessage());
					}

					sc.setPlayerOnlyMessage(anno.playerOnlyMessage());
					sc.setConsoleOnlyMessage(anno.consoleOnlyMessage());

					subCommands.put(sc.getCommand().toLowerCase(), sc);
					subCommandListeners.put(sc.getCommand().toLowerCase(), commandListener);
					subCommandMethods.put(sc.getCommand().toLowerCase(), method);
				}
			}
		}
	}
	
	@SneakyThrows(Exception.class)
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		String commandLine = label + (args.length < 1 ? "" : " " + String.join(" ", args));
		
		Command command = getCommand(commandLine);
		if(command == null) return false;
		
		String commandString = command instanceof MainCommand ? command.getName().toLowerCase() : ((SubCommand) command).getCommand().toLowerCase();
		
		Object listener = command instanceof MainCommand ? mainCommandListeners.get(commandString) : subCommandListeners.get(commandString);
		Method method = command instanceof MainCommand ? mainCommandMethods.get(commandString) : subCommandMethods.get(commandString);
		
		//
		
		UCommandSender uSender = Core.getUCommandSender(sender);
		
		if((method.getParameterTypes()[0].equals(Player.class) && !(sender instanceof Player)) || (method.getParameterTypes()[0].equals(UPlayer.class) && !(uSender instanceof UPlayer))) {
			Core.wmsgc(sender, command.getPlugin().getPluginPackage(), command.getPlayerOnlyMessage());
			return true;
		} else if((method.getParameterTypes()[0].equals(ConsoleCommandSender.class) && !(sender instanceof ConsoleCommandSender)) || (method.getParameterTypes()[0].equals(UConsoleSender.class) && !(sender instanceof UConsoleSender))) {
			Core.wmsgc(sender, command.getPlugin().getPluginPackage(), command.getConsoleOnlyMessage());
			return true;
		} else if(command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
			Core.wmsgc(sender, command.getPlugin().getPluginPackage(), command.getNoPermissionMessage());
			return true;
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
				return true;
			}
			
			String entered = label;
			if(command instanceof SubCommand) {
				int length = StringUtil.countMatches(((SubCommand) command).getCommand(), " ");
				
				for(int i = 0; i < length; i++) {
					entered += " " + args[i];
				}
			}
			
			command.sendUsage(uSender, entered, true);
			return true;
		}
		
		if(method.getParameterCount() < 3) {
			method.invoke(listener, (CommandSender.class.isAssignableFrom(method.getParameterTypes()[0]) ? sender : uSender), newArgs);
		} else {
			method.invoke(listener, (CommandSender.class.isAssignableFrom(method.getParameterTypes()[0]) ? sender : uSender), newArgs, command);
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		label = label.toLowerCase();
		String lastArgs = args[args.length - 1].toLowerCase();
		
		List<String> completeCommands = new ArrayList<>();
		
		if(args.length < 1) { // Main Command
			for(MainCommand mainCommand : mainCommands.values()) {
				if(mainCommand.getPermission() != null && !sender.hasPermission(mainCommand.getPermission())) continue;
				
				else if(mainCommand.getName().toLowerCase().startsWith(label)) {
					completeCommands.add(mainCommand.getName());
				} else {
					for(String alias : mainCommand.getAliases()) {
						if(alias.toLowerCase().startsWith(label)) {
							completeCommands.add(alias);
						}
					}
				}
			}
		} else { // SubCommand
			String commandLine = label + (args.length < 1 ? "" : " " + String.join(" ", args));
			
			for(SubCommand subCommand : getSubCommands(commandLine, 1)) {
				if(subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) continue;
				
				else if(subCommand.getName().toLowerCase().startsWith(lastArgs)) {
					completeCommands.add(subCommand.getName());
				} else {
					for(String alias : subCommand.getAliases()) {
						if(alias.toLowerCase().startsWith(lastArgs)) {
							completeCommands.add(alias);
						}
					}
				}
			}
		}
		
		if(completeCommands.size() < 1 && (playerNameTabComplete || playerDisplayNameTabComplete)) {
			for(UPlayer up : Core.getOnlineUPlayers()) {
				if(playerNameTabComplete && (lastArgs.length() < 1 || up.getName().toLowerCase().startsWith(lastArgs))) {
					completeCommands.add(up.getName());
				}

				if(playerDisplayNameTabComplete && (lastArgs.length() < 1 || up.hasDisplayName() && ChatColor.stripColor(up.getDisplayName()).toLowerCase().startsWith(lastArgs.toLowerCase()))) {
					completeCommands.add(ChatColor.stripColor(up.getDisplayName()));
				}
			}
		}
		
		Collections.sort(completeCommands);
		
		return completeCommands;
	}
	
}