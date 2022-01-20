package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.ClickType;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class Ninja extends PFPAbility implements Listener {

  public Ninja() {
    super();

    initAbility("닌자",
        AbilityType.ACTIVE,
        AbilityRank.A,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 화살을 발사합니다.",
        "화살에 맞을 경우",
        "10% 확률로 폭발,",
        "30% 확률로 화염,",
        "60% 확률로 쿨타임이 초기화됩니다.");
    setCoolTime(10);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
  }

  @Override
  public void onUseCastingItem(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {
    Arrow arrow = e.getPlayer().launchProjectile(Arrow.class, e.getPlayer().getLocation().getDirection());
    arrow.setVelocity(arrow.getVelocity().multiply(8));
    if (Math.random() < 0.3) {
      arrow.setFireTicks(20);
    }
  }

  @EventHandler
  public void onArrowHit(EntityDamageByEntityEvent e) {
    if (!(e.getDamager() instanceof Arrow) || !(((Arrow) e.getDamager()).getShooter().equals(getPlayer()))) return;

    if (Math.random() < 0.6) {
      stopCoolDownTask();
      setRemainingCoolTime(0);

      Core.cmsg(getPlayer(), ChatColor.YELLOW, "쿨타임이 초기화되었습니다.");
    }
    if (Math.random() < 0.1) {
      e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), 4F);
    }
  }

}
