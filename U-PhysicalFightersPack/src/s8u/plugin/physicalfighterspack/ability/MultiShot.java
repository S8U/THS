package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;

public class MultiShot extends PFPAbility implements Listener {

  public MultiShot() {
    super();

    initAbility("멀티샷",
        AbilityType.PASSIVE,
        AbilityRank.A,
        "활 발사 시 여러개의 화살이 날아갑니다.");
    setCoolTime(3);
  }

  @Override
  public void onAssign() {
    getPlayer().getInventory().addItem(new ItemStack(Material.BOW));
    getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 64));
  }

  @EventHandler
  public void onProjectileLaunch(ProjectileLaunchEvent e) {
    if (!getPlayer().equals(e.getEntity().getShooter()) || !(e.getEntity() instanceof Arrow) || getRemainingCoolTime() > 0) return;

    Arrow arrow = (Arrow) e.getEntity();

    for (int i = 0; i < 10; i++) {
      Arrow copy = ((Player) e.getEntity().getShooter()).getWorld().spawnArrow(e.getEntity().getLocation(), e.getEntity().getVelocity(), 1.5F, 10F);
      copy.spigot().setDamage(arrow.spigot().getDamage());
      copy.setCritical(arrow.isCritical());
      copy.setKnockbackStrength(arrow.getKnockbackStrength());
      copy.setFireTicks(arrow.getFireTicks());
      copy.setShooter(e.getEntity().getShooter());
    }

    runCoolDownTask();
  }

}