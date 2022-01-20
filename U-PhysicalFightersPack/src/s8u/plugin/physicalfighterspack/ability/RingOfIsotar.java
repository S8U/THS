package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.GameState;

public class RingOfIsotar extends PFPAbility implements Listener {

  public RingOfIsotar() {
    super();

    initAbility("이슈타르의 링",
        AbilityType.ACTIVE,
        AbilityRank.A,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 바라보는 방향으로 4 데미지의 화살을 두발 발사합니다.");
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    if (AbilityAPI.getGameManager().getGameState() != GameState.PLAYING || AbilityAPI.isInvincibilityTime());
    if (!getPlayer().equals(e.getPlayer()) || e.getPlayer().getItemInHand() == null || e.getPlayer().getItemInHand().getType() != Material.IRON_INGOT) return;

    Arrow arrow1 = e.getPlayer().launchProjectile(Arrow.class);
    arrow1.setVelocity(arrow1.getVelocity().multiply(3));

    Arrow arrow2 = e.getPlayer().launchProjectile(Arrow.class);
    arrow2.setVelocity(arrow2.getVelocity().multiply(2));
  }

  @EventHandler
  public void onArrowDamage(EntityDamageByEntityEvent e) {
    if (!(e.getDamager() instanceof Arrow) || !((Arrow) e.getDamager()).getShooter().equals(getPlayer())) return;

    e.setDamage(4);
  }

  @EventHandler
  public void onArrowHit(ProjectileHitEvent e) {
    if (!e.getEntity().getShooter().equals(getPlayer())) return;

    e.getEntity().remove();
  }

}