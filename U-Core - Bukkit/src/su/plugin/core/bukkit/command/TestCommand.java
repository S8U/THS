package su.plugin.core.bukkit.command;

import org.bukkit.entity.Player;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;

public class TestCommand implements UCommandListener {

  @SubCommandHandler(
      parent = "ctest",
      name = "getLang",
      permission = "core.admin",
      usage = "getLang"
  )
  public void getLang(Player p, String[] args) {
    Core.msg(p, p.getLocale());
  }

}
