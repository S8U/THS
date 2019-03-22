package su.plugin.core.bukkit.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import su.plugin.core.bukkit.api.event.entity.EntityDamageByPlayerEvent;

public class PlayerDamageByPlayerEvent extends EntityDamageByPlayerEvent {

  public PlayerDamageByPlayerEvent(Player damaged, Player damager, Projectile projectile, EntityDamageByEntityEvent entityDamageByEntityEvent) {
    super(damaged, damager, projectile, entityDamageByEntityEvent);
  }

  public Player getDamaged() {
    return (Player) super.getDamaged();
  }

}