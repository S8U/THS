package su.plugin.channelgui.command;

import org.bukkit.entity.Player;
import su.plugin.channelgui.ChannelGUIPlugin;
import su.plugin.channelgui.PermissionList;
import su.plugin.channelgui.api.ChannelGUIAPI;
import su.plugin.channelgui.api.object.ChannelGUI;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;

public class ChannelGUICommand implements UCommandListener {

  private ChannelGUIAPI api = ChannelGUIPlugin.getApi();

  @CommandHandler(
      name = "channelGUI",
      aliases = {"cg"},
      usage = "채널 GUI 명령어를 확인합니다."
  )
  public void channelGUI(UCommandSender sender, String[] args) {
    Core.nmsg(sender, "§e§l[ U-ChannelGUI ]");
    for(SubCommand sc : Core.getCommandManager().getSubCommands("channelGUI", 1)) {
      sc.sendUsageIfHasPermission(sender, false);
    }
  }

  @SubCommandHandler(
      parent = "channelGUI",
      name = "open",
      aliases = {"열기"},
      additional = "<GUI>",
      minArgs = 1,
      permission = PermissionList.CHANNELGUI_OPEN,
      usage = "채널 GUI를 엽니다."
  )
  public void channelGUI_open(UPlayer up, String[] args, Command cmd) {
    if(!api.getGUIManager().existsGUI(args[0])) {
      up.wmsg("존재하지 않는 GUI입니다.");
      return;
    }

    ChannelGUI gui = api.getGUIManager().getGUI(args[0]);
    if(gui.getPermission() != null && !up.hasPermission(gui.getPermission())) {
      up.wmsg("GUI를 열 권한이 없습니다.");
      return;
    }

    gui.open((Player) up.getPlatformSender());
  }

  @SubCommandHandler(
      parent = "channelGUI",
      name = "list",
      aliases = {"목록"},
      permission = PermissionList.CHANNELGUI_LIST,
      usage = "채널 GUI 목록을 확인합니다."
  )
  public void channelGUI_list(UCommandSender sender, String[] args, Command cmd) {
    if(api.getGUIManager().getGUIs().size() < 1) {
      sender.wmsg("GUI가 없습니다.");
      return;
    }

    sender.msg("§e메뉴 목록: " + String.join(", ", api.getGUIManager().getGUIs().keySet()));
  }

}