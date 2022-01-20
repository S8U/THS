package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;

public class Anorexia extends PFPAbility implements Listener {

  public Anorexia() {
    super();

    initAbility("거식증",
        AbilityType.PASSIVE,
        AbilityRank.A,
        "배고픔이 항상 최대로 고정됩니다.",
        "체력 회복량이 3배로 증가합니다.");
  }

  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent e) {
    if (!getPlayer().equals(e.getEntity())) return;

    e.setFoodLevel(20);
    ((Player) e.getEntity()).setSaturation(0);
  }

  @EventHandler
  public void onRegainHealth(EntityRegainHealthEvent e) {
    if (!getPlayer().equals(e.getEntity())) return;

    e.setAmount(e.getAmount() * 3);
  }

}
