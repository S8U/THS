package s8u.plugin.minigamecore.api.spectator;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.minigamecore.api.player.GamePlayer;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.util.ItemUtil;
import su.plugin.core.common.api.Core;

public class GamePlayerIcon extends Icon {

  @Getter
  private GamePlayer gamePlayer;

  public GamePlayerIcon(GamePlayer gamePlayer) {
    super(ItemUtil.getSkull(
        new ItemBuilder(397)
            .displayName("§f" + gamePlayer.getPlayerKey().getDisplayName())
            .lore("§f클릭 시 이동합니다.").build(), gamePlayer.getPlayerKey().getName()));

    this.gamePlayer = gamePlayer;
  }

  @Override
  protected ItemStack updateItem() {
    return getItem();
  }

  @Override
  public void onIconClick(IconClickEvent event) {
    if(gamePlayer == null || gamePlayer.getPlayer() == null) {
      Core.wmsg(event.getPlayer(), "선택한 플레이어가 접속 중이 아닙니다.");
      return;
    }

    event.getPlayer().closeInventory();

    KCore.teleport(event.getPlayer(), gamePlayer.getPlayer().getLocation());
  }

}
