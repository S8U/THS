package su.plugin.core.bukkit.api.event.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

@RequiredArgsConstructor
@Getter
public class EntityDamageByPlayerEvent extends UKCancellableEvent {

  private final Entity damaged;

  private final Player damager;

  private final Projectile projectile;

  private final EntityDamageByEntityEvent entityDamageByEntityEvent;

  public boolean isProjectile() {
    return projectile != null;
  }

}