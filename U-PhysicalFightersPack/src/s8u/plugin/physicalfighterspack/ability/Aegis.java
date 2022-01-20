package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;

public class Aegis extends PFPAbility implements Listener {

  public Aegis() {
    super();

    initAbility("이지스",
        AbilityType.ACTIVE_CONTINUE,
        AbilityRank.S,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 5초간 무적 상태가 됩니다.");
    setCoolTime(55);
    setDurationTime(5);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
  }

  @EventHandler
  public void onDamage(EntityDamageEvent e) {
    if (!getPlayer().equals(e.getEntity()) || getRemainingDurationTime() < 1) return;

    e.getEntity().setFireTicks(0);
    e.setCancelled(true);
  }

}
