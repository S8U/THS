package s8u.plugin.cash.command;

import org.bukkit.entity.Player;
import s8u.plugin.cash.api.CashAPI;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;

public class ColorDisplayNameCommand implements UCommandListener {

  @CommandHandler(
      name = "색깔닉네임",
      aliases = {"colorDisplayName", "torRkfslrspdla", "닉네임색깔", "slrspdlatorRKf" },
      usage = "닉네임 색깔을 선택합니다."
  )
  public void colorDisplayName(Player p, String[] args) {
    CashAPI.getColorSelectGUI().open(p);
  }

}