package su.plugin.gcmdlocker.command;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.gcmdlocker.GCMDLockerPlugin;
import su.plugin.gcmdlocker.api.GCMDLockerAPI;

public class CLCommand implements UCommandListener  {

  private GCMDLockerAPI api = GCMDLockerPlugin.getApi();

  @CommandHandler(
      name = "cmdLogin",
      aliases = { "clogin", "cl" },
      additional = "<비밀번호>",
      minArgs = 1,
      permission = "gcmdlocker.admin",
      usage = "CMDLocker에 로그인합니다."
  )
  public void cmdLogin(UPlayer up, String[] args) {
    String password = String.join(" ", args);

    if(api.isCorrectPassword(password)) {
      api.login((ProxiedPlayer) up.getPlatformSender());
      return;
    }

    up.wmsg("잘못된 비밀번호입니다.");
  }

  @CommandHandler(
      name = "cmdLogout",
      aliases = { "clogout", "clo" },
      permission = "gcmdlocker.admin",
      usage = "CMDLocker에서 로그아웃합니다."
  )
  public void cmdLogout(UPlayer up, String[] args) {
    if(api.isLogged(up.getPlayerKey())) {
      api.logout((ProxiedPlayer) up.getPlatformSender());
      api.logoutIp(up.getIp());
      return;
    }

    up.wmsg("아직 로그인하지 않았습니다.");
  }

}