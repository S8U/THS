package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class Time extends PFPAbility implements Listener {

  public Time() {
    super();

    initAbility("타임",
        AbilityType.ACTIVE_CONTINUE,
        AbilityRank.B,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 자신을 제외한 모든 플레이어의 이동을 5초동안 차단합니다.",
        "단, 직접적인 이동만 불가능합니다.");
    setCoolTime(40);
    setDurationTime(5);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
  }

  @Override
  public void onDurationStart() {
    Core.cbc(ChatColor.GREEN, getGamePlayer().getDisplayName() + "님께서 타임 능력을 사용했습니다.");
  }

  @Override
  public void onDurationEnd() {
    Core.cbc(ChatColor.GREEN, "타임 능력이 해제되었습니다.");
  }

  @EventHandler
  public void onMove(PlayerMoveEvent e) {
    if (getPlayer() != null && e.getPlayer().equals(getPlayer()) || getRemainingDurationTime() < 1) return;

    GamePlayer gp = api.getPlayerManager().getGamePlayer(e.getPlayer());
    if (gp.isWatchMode() || gp.isEliminate()) return;

    e.setTo(e.getFrom());
  }

}