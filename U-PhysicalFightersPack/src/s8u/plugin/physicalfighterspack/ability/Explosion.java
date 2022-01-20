package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import s8u.plugin.physicalfighterspack.PhysicalFightersPackPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.GameState;

public class Explosion extends PFPAbility implements Listener {

  public Explosion() {
    super();

    initAbility("익스플로젼",
        AbilityType.PASSIVE,
        AbilityRank.B,
        "사망 시 강력한 폭발을 일으킵니다.");
  }

  @EventHandler (priority = EventPriority.LOWEST)
  public void onDeath(PlayerDeathEvent e) {
    if (!getPlayer().equals(e.getEntity())) return;

    Location location = e.getEntity().getLocation();

    Bukkit.getScheduler().runTaskLater(PhysicalFightersPackPlugin.getInstance(), () -> location.getWorld().createExplosion(location, 8.0F, false), 1L);
  }

}
