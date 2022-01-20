package s8u.plugin.fmllogger;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UPlayerJoinEvent;

public class FmlLoggerPlugin extends UGPlugin implements UEventListener {

  private SqlManager sqlManager = new SqlManager();

  @Override
  public void onUEnable() {
    if (!sqlManager.connect(this)) {
      wlog("Sql에 접속할 수 없어 비활성화됩니다.");
      return;
    }
  }

  @Override
  public void onUDisable() {
    sqlManager.close();
  }

  @UEventHandler
  public void onJoin(UPlayerJoinEvent e) {
    sqlManager.log(e.getPlayer().getPlayerKey().getId(), ((ProxiedPlayer) e.getPlayer().getPlatformSender()).getModList());
  }

}