package su.plugin.gessentials.bungee.command;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.bungee.api.GCore;
import su.plugin.core.bungee.api.player.GPlayer;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;

public class ModListCommand implements UCommandListener {

  @CommandHandler(
      name = "모드목록",
      aliases = {"modList"},
      usage = "모드 목록을 확인합니다.",
      additional = "<플레이어>",
      minArgs = 1,
      permission = "gessentials.modlist"
  )
  public void mute(UCommandSender sender, String[] args) {
    GPlayer tgp = (GPlayer) GCore.getUPlayerByDisplayName(args[0]);
    if(tgp == null) {
      sender.wmsg("접속 중이 아닌 플레이어입니다.");
      return;
    }

    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

    sender.nmsg("§7[ §f" + target.getName() + " §7님의 모드 정보 ]");
    sender.nmsg("§7포지 감지: §f" + (target.isForgeUser() ? "O" : "X"));
    sender.nmsg("§7모드 목록: §f" + target.getModList());
  }

}