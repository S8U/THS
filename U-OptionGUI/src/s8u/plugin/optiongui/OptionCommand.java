package s8u.plugin.optiongui;

import org.bukkit.entity.Player;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;

public class OptionCommand implements UCommandListener {

  private OptionGUI gui = new OptionGUI();

  @CommandHandler(
      name = "옵션",
      aliases = { "option", "dhqtus" }
  )
  public void option(Player p, String[] args) {
    gui.open(p);
  }

}