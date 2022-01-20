package s8u.plugin.minigamecore.api.spectator;

import java.util.Comparator;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.minigamecore.api.MiniGameCore;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.gui.PageableGUI;

public class SpectatorTeleportGUI extends PageableGUI {

  public SpectatorTeleportGUI() {
    super("U-MiniGameCore/Spectator/Teleport", "게임 시작 투표", 6, 1, 9, 1, 4);

    // 4줄까지 플레이어 머리
    // 5줄은 장식
    // 6줄은 페이지

    Icon deco = new Icon(new ItemStack(Material.STAINED_GLASS_PANE)) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };

    for (int i = 1; i <= 9; i++) {
      setCommonIcon(i,5,deco);
    }

    Icon previousIcon = new Icon(new ItemBuilder(Material.PAPER).displayName("§b§l이전 페이지").lore("§f클릭 시 이전 페이지로 이동합니다.").build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };

    Icon nextIcon = new Icon(new ItemBuilder(Material.PAPER).displayName("§b§l다음 페이지").lore("§f클릭 시 다음 페이지로 이동합니다.").build()) {
      @Override
      protected ItemStack updateItem() {
        return getItem();
      }
    };

    setCommonIcon(1, 6,previousIcon);
    setCommonIcon(9, 6, nextIcon);

    setPreviousIcon(1,6);
    setNextIcon(9,6);
  }

  @Override
  protected void onUpdate() {
    // 삭제
    Iterator<Icon> it = getIcons().iterator();
    for (int i = 0; it.hasNext(); i++) {
      GamePlayerIcon icon = (GamePlayerIcon) it.next();

      if (!icon.getGamePlayer().isPlaying()) {
        it.remove();
      }
    }

    // 추가
    MiniGameCore.getPlayerManager().getOnlinePlayingPlayers().forEach(gp -> {
      for (int i = 0; i < getIcons().size(); i++) {
        if (((GamePlayerIcon) getIcon(i)).getGamePlayer().equals(gp)) return;
      }

      addIcon(new GamePlayerIcon(gp));
    });

    // 정렬
    getIcons().sort(Comparator.comparing(icon -> ((GamePlayerIcon) icon).getGamePlayer().getPlayerKey().getDisplayName()));
  }
}