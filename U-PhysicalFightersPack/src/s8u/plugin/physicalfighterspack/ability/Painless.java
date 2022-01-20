package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.GameState;

public class Painless extends PFPAbility implements Listener {

  public Painless() {
    super();

    initAbility("무통증",
        AbilityType.PASSIVE,
        AbilityRank.A,
        "공격 받을 시 80% 확률로 넉백을 무시합니다.");
  }

  private boolean damaged1, damaged2;

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
    if (AbilityAPI.getGameManager().getGameState() != GameState.PLAYING || AbilityAPI.isInvincibilityTime());
    if (!getPlayer().equals(e.getEntity()) || Math.random() >= 0.8) return;

    damaged1 = true;
    damaged2 = true;
  }

  @EventHandler
  public void onVelocity(PlayerVelocityEvent e) {
    if (!damaged1 && !damaged2) return;

    damaged2 = damaged1;
    damaged1 = false;

    e.setCancelled(true);
  }

}