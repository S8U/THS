package su.plugin.core.common.api.command;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.Getter;
import lombok.Setter;
import su.plugin.core.common.api.plugin.UPlugin;
import su.plugin.core.common.api.util.StringUtil;

@Getter
public abstract class UCommandManager {
	
	@Setter
	protected boolean playerNameTabComplete, playerDisplayNameTabComplete;
	
	//

	public abstract void registerCommands(UPlugin plugin, UCommandListener commandListener);

	/*public boolean unregisterCommand(String command) {
		Command cobj = getCommand(command);
		if(cobj == null) return false;

		else if(cobj instanceof MainCommand) {
			mainCommandListeners.remove(cobj.getName());
			mainCommandMethods.remove(cobj.getName());
			mainCommands.remove(cobj.getName());
		} else {
			SubCommand sc = (SubCommand) cobj;
			subCommandListeners.remove(sc.getCommand());
			subCommandMethods.remove(sc.getCommand());
			subCommands.remove(sc.getCommand());
		}

		return true;
	}*/

	//
	
	protected LinkedHashMap<String, UCommandListener> mainCommandListeners = new LinkedHashMap<>();
	protected LinkedHashMap<String, Method> mainCommandMethods = new LinkedHashMap<>();
	protected LinkedHashMap<String, MainCommand> mainCommands = new LinkedHashMap<>();
	
	protected LinkedHashMap<String, UCommandListener> subCommandListeners = new LinkedHashMap<>();
	protected LinkedHashMap<String, Method> subCommandMethods = new LinkedHashMap<>();
	protected LinkedHashMap<String, SubCommand> subCommands = new LinkedHashMap<>();
	
	//
	
	public boolean isCommandHandler(Method method) {
		return method.getAnnotation(CommandHandler.class) != null;
	}
	
	public boolean isSubCommandHandler(Method method) {
		return method.getAnnotation(SubCommandHandler.class) != null;
	}
	
	//
	
	public Command getCommand(String command) {
		if(command.isEmpty()) return null;
		else if(StringUtil.countMatches(command, " ") < 1) return getMainCommand(command);
		
		Command commandObj = getSubCommand(command);
		
		return commandObj == null ? getMainCommand(command) : commandObj;
	}
	
	public List<Command> getCommands(UPlugin plugin) {
		List<Command> commands = new ArrayList<>();
		
		commands.addAll(getMainCommands(plugin));
		commands.addAll(getSubCommands(plugin));
		
		return commands;
	}
	
	//
	
	public MainCommand getMainCommand(String command) {
		if(command.isEmpty()) return null;
		
		String commandSplit = command.split(" ")[0];
		
		for(MainCommand mainCommand : mainCommands.values()) {
			if(mainCommand.getName().equalsIgnoreCase(commandSplit)) return mainCommand;
			
			for(String as : mainCommand.getAliases()) {
				if(commandSplit.equalsIgnoreCase(as)) return mainCommand;
			}
		}
		
		return null;
	}
	
	public List<MainCommand> getMainCommands(UPlugin plugin) {
		List<MainCommand> mainCommands = new ArrayList<>();
		
		for(MainCommand mainCommand : this.mainCommands.values()) {
			if(!mainCommand.getPlugin().equals(plugin)) continue;
			mainCommands.add(mainCommand);
		}
		
		return mainCommands;
	}
	
	//
	
	public SubCommand getSubCommand(String command) {
		String[] commandSplit = command.toLowerCase().split(" ");
		if(commandSplit.length < 2) return null;
		
		String parentCommandName = getMainCommand(commandSplit[0]).getName().toLowerCase();
		String commandLine = parentCommandName;
		
		for(int i = 1; i < commandSplit.length; i++) {
			String subCommandName = commandSplit[i].toLowerCase();
			for(SubCommand subCommand : subCommands.values()) {
				int subCommandLength = StringUtil.countMatches(subCommand.getCommand(), " ");
				if(subCommandLength != i || !subCommand.getCommand().toLowerCase().startsWith(commandLine)) continue;
				
				else if(subCommand.getName().equalsIgnoreCase(subCommandName)) {
					commandLine += " " + subCommand.getName().toLowerCase();
				} else {
					for(String alias : subCommand.getAliases()) {
						if(alias.equalsIgnoreCase(subCommandName)) {
							commandLine += " " + subCommand.getName().toLowerCase();
							break;
						}
					}
				}
			}
		}
		
		return subCommands.get(commandLine);
	}
	
	public List<SubCommand> getSubCommands(String command, int args) {
		List<SubCommand> subCommands = new ArrayList<>();
		
		Command parentCommand = getCommand(command);
		String parentCommandName = parentCommand instanceof MainCommand ? parentCommand.getName().toLowerCase() : ((SubCommand) parentCommand).getCommand().toLowerCase();
		
		int parentCommandLength = StringUtil.countMatches(parentCommandName, " ");
		for(SubCommand subCommand : this.subCommands.values()) {
			int subCommandLength = StringUtil.countMatches(subCommand.getCommand(), " ");
			if(subCommandLength <= parentCommandLength || subCommandLength > parentCommandLength + args || !subCommand.getCommand().toLowerCase().startsWith(parentCommandName + " ")) continue;
			
			subCommands.add(subCommand);
		}
		
		return subCommands;
	}
	
	public List<SubCommand> getSubCommands(String command) {
		List<SubCommand> subCommands = new ArrayList<>();
		
		Command parentCommand = getCommand(command);
		String parentCommandName = parentCommand instanceof MainCommand ? parentCommand.getName().toLowerCase() : ((SubCommand) parentCommand).getCommand().toLowerCase();
		
		int parentCommandLength = StringUtil.countMatches(parentCommandName, " ");
		for(SubCommand subCommand : this.subCommands.values()) {
			int subCommandLength = StringUtil.countMatches(subCommand.getCommand(), " ");
			if(subCommandLength <= parentCommandLength || !subCommand.getCommand().toLowerCase().startsWith(parentCommandName + " ")) continue;
			
			subCommands.add(subCommand);
		}
		
		return subCommands;
	}
	
	public List<SubCommand> getSubCommands(UPlugin plugin) {
		List<SubCommand> subCommands = new ArrayList<>();
		
		for(SubCommand sc : this.subCommands.values()) {
			if(!sc.getPlugin().equals(plugin)) continue;
			subCommands.add(sc);
		}
		
		return subCommands;
	}
	
	//
	
	public int registerCommands(UPlugin plugin) {
		return registerCommands(plugin, "");
	}
	
	public int registerCommands(UPlugin plugin, String pack) {
		int i = 0;
		
		try {
			ZipInputStream jarStream = new ZipInputStream(new FileInputStream(plugin.getFile()));
			ZipEntry item = null;
			
			while((item = jarStream.getNextEntry()) != null) {
				if(item.isDirectory() || !item.getName().endsWith(".class")) continue;
				
				String className = item.getName().replaceAll("/", ".").substring(0, item.getName().length() - 6);
				if(pack.length() > 0 && !className.substring(0, className.lastIndexOf(".")).equals(pack)) continue;
				
				try {
					Class<?> c = Class.forName(className);
					
					try {
						UCommandListener listener = (UCommandListener) c.newInstance();
						registerCommands(plugin, listener);
						i++;
					} catch (Exception ex) { }
				} catch(ClassNotFoundException cnfe) { }
			}
			
			jarStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return i;
	}
	
}