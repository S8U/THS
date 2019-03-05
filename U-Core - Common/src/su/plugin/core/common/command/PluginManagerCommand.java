package su.plugin.core.common.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.MainCommand;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.plugin.UPlugin;

public class PluginManagerCommand implements UCommandListener {

  @SubCommandHandler(
      parent = "core",
      name = "pluginManager",
      aliases = {"pm", "플러그인관리"},
      permission = "core.admin",
      usage = "U-Core 기반 플러그인 관리 명령어를 확인합니다."
  )
  public void core_pluginManager(UCommandSender sender, String[] args) {
    sender.nmsg("§e§l[ U-Core | PluginManager ]");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("core pluginManager", 1)) {
      sc.sendUsageIfHasPermission(sender, false);
    }
  }

  @SubCommandHandler(
      parent = "core pluginManager",
      name = "list",
      aliases = {"l", "목록"},
      permission = "core.admin",
      usage = "U-Core 기반 플러그인 목록을 확인합니다."
  )
  public void core_pluginManager_list(UCommandSender sender, String[] args) {
    List<String> enabledPluginNames = new ArrayList<>();
    List<String> disabledPluginNames = new ArrayList<>();
    for(UPlugin plugin : Core.getUPluginManager().getPlugins().values()) {
      if(plugin.isEnabled()) {
        enabledPluginNames.add(plugin.getName() + " / " + plugin.getVersion());
      } else {
        disabledPluginNames.add(plugin.getName() + " / " + plugin.getVersion());
      }
    }

    Collections.sort(enabledPluginNames);
    Collections.sort(disabledPluginNames);

    sender.nmsg("§e[ 플러그인 목록 (" + Core.getUPluginManager().getPlugins().size() + ") ]");
    for(String pluginName : enabledPluginNames) {
      sender.nmsg("§a" + pluginName);
    }
    for(String pluginName : disabledPluginNames) {
      sender.nmsg("§c" + pluginName);
    }
  }

  @SubCommandHandler(
      parent = "core pluginManager",
      name = "messageFormat",
      aliases = {"mf", "메시지형식"},
      additional = "<플러그인>",
      minArgs = 1,
      permission = "core.admin",
      usage = "U-Core 기반 플러그인의 Message Format을 확인합니다."
  )
  public void core_pluginManager_messageFormat(UCommandSender sender, String[] args) {
    UPlugin plugin = Core.getUPluginManager().getUPluginByName(args[0]);
    if(plugin == null) {
      sender.wmsg("존재하지 않는 플러그인입니다.");
      return;
    } else if(!plugin.isEnabled()) {
      sender.wmsg("활성화되어있지 않은 플러그인입니다.");
      return;
    }

    sender.nmsg("§e[ " + plugin.getName() + " 플러그인 Message Format ]");
    sender.nmsg("logFormat: " + plugin.getLogFormat().replace("§", "&") + " / color: " + (plugin.isUseLogColor() ? "O" : "X"));
    sender.nmsg("warningLogFormat: " + plugin.getWarningLogFormat().replace("§", "&") + " / color: " + (plugin.isUseWarningLogColor() ? "O" : "X"));
    sender.nmsg("messageFormat: " + plugin.getMessageFormat().replace("§", "&") + " / color: " + (plugin.isUseMessageColor() ? "O" : "X"));
    sender.nmsg("colorMessageFormat: " + plugin.getColorMessageFormat().replace("§", "&") + " / color: " + (plugin.isUseColorMessageColor() ? "O" : "X"));
    sender.nmsg("warningMessageFormat: " + plugin.getWarningMessageFormat().replace("§", "&") + " / color: " + (plugin.isUseWarningMessageColor() ? "O" : "X"));
    sender.nmsg("broadcastFormat: " + plugin.getBroadcastFormat().replace("§", "&") + " / color: " + (plugin.isUseBroadcastColor() ? "O" : "X"));
    sender.nmsg("colorBroadcastFormat: " + plugin.getColorBroadcastFormat().replace("§", "&") + " / color: " + (plugin.isUseColorBroadcastColor() ? "O" : "X"));
  }

  @SubCommandHandler(
      parent = "core pluginManager",
      name = "reloadMessageFormat",
      aliases = {"rmf", "메시지형식리로드"},
      additional = "<플러그인>",
      minArgs = 1,
      permission = "core.admin",
      usage = "U-Core 기반 플러그인의 Message Format을 다시 불러옵니다."
  )
  public void core_pluginManager_reloadMessageFormat(UCommandSender sender, String[] args) {
    UPlugin plugin = Core.getUPluginManager().getUPluginByName(args[0]);
    if(plugin == null) {
      sender.wmsg("존재하지 않는 플러그인입니다.");
      return;
    } else if(!plugin.isEnabled()) {
      sender.wmsg("활성화되어있지 않은 플러그인입니다.");
      return;
    }

    plugin.loadMessageFormatConfig(sender);
  }

  @SubCommandHandler(
      parent = "core pluginManager",
      name = "reloadConfig",
      aliases = {"rc", "설정리로드"},
      additional = "<플러그인>",
      minArgs = 1,
      permission = "core.admin",
      usage = "U-Core 기반 플러그인의 설정을 다시 불러옵니다."
  )
  public void core_pluginManager_reloadConfig(UCommandSender sender, String[] args) {
    UPlugin plugin = Core.getUPluginManager().getUPluginByName(args[0]);
    if(plugin == null) {
      sender.wmsg("존재하지 않는 플러그인입니다.");
      return;
    } else if(!plugin.isEnabled()) {
      sender.wmsg("활성화되어있지 않은 플러그인입니다.");
      return;
    }

    plugin.loadConfig(sender);
  }

  @SubCommandHandler(
      parent = "core pluginManager",
      name = "commandList",
      aliases = {"cl", "명령어목록"},
      additional = "<플러그인>",
      minArgs = 1,
      permission = "core.admin",
      usage = "U-Core 기반 플러그인의 Command 목록을 확인합니다."
  )
  public void core_pluginManager_commandList(UCommandSender sender, String[] args) {
    UPlugin plugin = Core.getUPluginManager().getUPluginByName(args[0]);
    if(plugin == null) {
      sender.wmsg("존재하지 않는 플러그인입니다.");
      return;
    } else if(!plugin.isEnabled()) {
      sender.wmsg("활성화되어있지 않은 플러그인입니다.");
      return;
    }

    List<MainCommand> mainCommands = Core.getCommandManager().getMainCommands(plugin);
    sender.nmsg("§e[ " + plugin.getName() + " 플러그인 MainCommand (" + mainCommands.size() + ") ]");
    for(MainCommand mainCommand : mainCommands) {
      sender.nmsg(mainCommand.getName() + (mainCommand.getAliases().isEmpty() ? "" : "§e: §f" + String.join(", ", mainCommand.getAliases())) + (mainCommand.getPermission() == null ? "" : "§e: §f[" + mainCommand.getPermission() + "]"));
    }

    List<SubCommand> subCommands = Core.getCommandManager().getSubCommands(plugin);
    sender.nmsg("§e[ " + plugin.getName() + " 플러그인 SubCommand (" + subCommands.size() + ") ]");
    for(SubCommand subCommand : subCommands) {
      sender.nmsg("(" + subCommand.getParentCommand() + ") " + subCommand.getName() + (subCommand.getAliases().isEmpty() ? "" : "§e: §f" + String.join(", ", subCommand.getAliases())) + (subCommand.getPermission() == null ? "" : " §e: §f[" + subCommand.getPermission() + "]"));
    }
  }

}