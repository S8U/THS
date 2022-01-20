package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;

public class Fly extends PFPAbility implements Listener {

  public Fly() {
    super();

    initAbility("플라이",
        AbilityType.ACTIVE_CONTINUE,
        AbilityRank.S,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 10초간 하늘을 날 수 있습니다.",
        "낙하 데미지를 받지 않습니다.");
    setCoolTime(80);
    setDurationTime(10);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
  }

  @Override
  public void onDurationStart() {
    getPlayer().setAllowFlight(true);
    getPlayer().setFlying(true);
  }

  @Override
  public void onDurationEnd() {
    getPlayer().setAllowFlight(false);
    getPlayer().setFlying(false);
  }

  @EventHandler
  public void onFallDamage(EntityDamageEvent e) {
    if (!getPlayer().equals(e.getEntity()) || e.getCause() != DamageCause.FALL) return;

    e.setCancelled(true);
  }

}