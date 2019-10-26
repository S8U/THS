package su.plugin.core.bukkit.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.gui.FakeIcon;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.UPlayer;

public class TestCommand implements UCommandListener {

  // Locale Test
  @SubCommandHandler(
      parent = "ctest",
      name = "getLang",
      permission = "core.admin",
      usage = "getLang"
  )
  public void getLang(Player p, String[] args) {
    Core.msg(p, p.getLocale());
  }

  // Fake Icon Test

  private GUI fakeIconGUI;

  @SubCommandHandler(
      parent = "ctest",
      name = "fakeIcon1",
      permission = "core.admin",
      usage = "fakeIcon GUI Create"
  )
  public void fakeIcon1(Player p, String[] args) {
    fakeIconGUI = new GUI("fakeIcon","아이콘 테스트",1);

    Icon icon = new Icon(new ItemBuilder(3).displayName("일반 아이콘").build()) {
      @Override
      protected ItemStack updateItem() {
        return this.getItem();
      }
    };
    fakeIconGUI.setIcon(1,1, icon);

    FakeIcon fi = new FakeIcon(new ItemBuilder(1).displayName("").build()) {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        return new ItemBuilder(1).displayName("Hello " + up.getDisplayName() + "!").lore(up.getIp()).build();
      }
    };
    fakeIconGUI.setIcon(5,1, fi);

    FakeIcon fi2 = new FakeIcon() {
      @Override
      protected ItemStack updateItem(UPlayer up) {
        return new ItemBuilder(2)
            .displayName(up.getDisplayName())
            .lore(up.getPlayerKey().getId() + "")
            .lore(up.getPlayerKey().getName())
            .lore(up.getPlayerKey().getUuid().toString())
            .build();
      }
    };
    fakeIconGUI.setIcon(9,1, fi2);
    fakeIconGUI.update();

    Core.msg(p,"FakeIcon GUI를 생성했습니다.");
  }

  @SubCommandHandler(
      parent = "ctest",
      name = "fakeIcon2",
      permission = "core.admin",
      usage = "fakeIcon GUI Open"
  )
  public void fakeIcon2(Player p, String[] args) {
    fakeIconGUI.open(p);

    Core.msg(p,"FakeIcon GUI를 열었습니다.");
  }

}
