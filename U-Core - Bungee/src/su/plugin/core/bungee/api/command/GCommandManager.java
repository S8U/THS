package su.plugin.core.bungee.api.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
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
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.plugin.UPlugin;
import su.plugin.core.common.api.util.ReflectionUtil;

@Getter
public class GCommandManager extends UCommandManager implements Listener {
	
	@Override
	public void registerCommands(UPlugin plugin, UCommandListener commandListener) {
		for(Method method : ReflectionUtil.getMethodsInOrder(commandListener.getClass())) {
			Class<?>[] parameters = method.getParameterTypes();
			
			if(!(parameters.length == 2 || parameters.length == 3)
					|| !(CommandSender.class.isAssignableFrom(parameters[0]) || UCommandSender.class.isAssignableFrom(parameters[0])) || !String[].class.isAssignableFrom(parameters[1])
					|| (parameters.length == 3 && !Command.class.isAssignableFrom(parameters[2]))) continue;
			
			if(isCommandHandler(method)) {
				CommandHandler anno = method.getAnnotation(CommandHandler.class);
				
				String permission = anno.permission().equals("") ? null : anno.permission();
				
				String[] aliases = anno.aliases()[0].isEmpty() ? null : anno.aliases();
				List<String> aliasesArr = aliases == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(aliases));
				
				if(anno.usePlatformPrefix()) {
					if(aliases != null) {
						for(String ac : aliases) {
							aliasesArr.add("g" + ac);
						}
					}

					aliasesArr.add("g" + anno.name());
				}
				
				PluginCommand pc = aliasesArr.size() < 1 ? new PluginCommand(anno.name(), permission) : new PluginCommand(anno.name(), permission, aliasesArr.toArray(new String[aliasesArr.size()]));
				
				ProxyServer.getInstance().getPluginManager().registerCommand((Plugin) plugin.getPlatformPlugin(), pc);
				
				MainCommand mc = new MainCommand(plugin, anno.name());
				
				if(!anno.usage().equals("")) {
					mc.setUsage(anno.usage());
				}
				
				if(!anno.additional().isEmpty()) {
					mc.setAdditional(anno.additional());
				}
				
				mc.setMinArgs(anno.minArgs());
				mc.setMaxArgs(anno.maxArgs());

				mc.setPermission(permission);
				
				if(!anno.noPermissionMessage().equals("")) {
					mc.setNoPermissionMessage(anno.noPermissionMessage());
				}
				
				mc.setPlayerOnlyMessage(anno.playerOnlyMessage());
				mc.setConsoleOnlyMessage(anno.consoleOnlyMessage());
				
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

					if(!anno.usage().equals("")) {
						sc.setUsage(anno.usage());
					}

					sc.setMinArgs(anno.minArgs());
					sc.setMaxArgs(anno.maxArgs());

					if(!anno.permission().equals("")) {
						sc.setPermission(anno.permission());
					}

					if(!anno.noPermissionMessage().equals("")) {
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
	
	//
	
	@EventHandler
	public void onTabComplete(TabCompleteEvent e) {
		ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
		
		String[] args = e.getCursor().split(" ");
		String label = args[0].substring(1, args[0].length()).toLowerCase();
		args = e.getCursor().substring(label.length() + 1).split(" ");
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

		if((getCommand(e.getCursor().substring(1)) != null || e.getCursor().length() < 1) && (playerNameTabComplete || playerDisplayNameTabComplete)) {
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
		
		e.getSuggestions().addAll(completeCommands);
	}

}