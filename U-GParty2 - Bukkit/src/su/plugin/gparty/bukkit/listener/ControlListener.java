package su.plugin.gparty.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.gparty.bukkit.KGPartyPlugin;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class ControlListener implements Listener {

  private KGPartyAPI api = KGPartyPlugin.getApi();

  @EventHandler
  public void onPlayerDamageByPlayer(EntityDamageByEntityEvent e) {
    if(api.isAllowPartyPVP() || !(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) return;

    PartyPlayer pp = api.getPlayerManager().getPartyPlayers().get(PlayerKey.getPlayerKeyByPlatformPlayer((Player) e.getDamager()));
    PartyPlayer tp = api.getPlayerManager().getPartyPlayers().get(PlayerKey.getPlayerKeyByPlatformPlayer((Player) e.getEntity()));
    if(pp == null || tp == null || !pp.hasParty() || !tp.hasParty() || !pp.getParty().equals(tp.getParty())) return;

    pp.getPlayerKey().getUPlayer().wmsg("파티 중인 플레이어는 공격할 수 없습니다.");

    e.setCancelled(true);
  }

  @EventHandler
  public void onPlayerDamageByProjectile(EntityDamageByEntityEvent e) {
    if(api.isAllowPartyPVP() || !(e.getDamager() instanceof Projectile) || !(((Projectile) e.getDamager()).getShooter() instanceof Player) || !(e.getEntity() instanceof Player)) return;

    PartyPlayer pp = api.getPlayerManager().getPartyPlayers().get(PlayerKey.getPlayerKeyByPlatformPlayer((Player) ((Projectile) e.getDamager()).getShooter()));
    PartyPlayer tp = api.getPlayerManager().getPartyPlayers().get(PlayerKey.getPlayerKeyByPlatformPlayer((Player) e.getEntity()));
    if(pp == null || tp == null || !pp.hasParty() || !tp.hasParty() || !pp.getParty().equals(tp.getParty())) return;

    pp.getPlayerKey().getUPlayer().wmsg("파티 중인 플레이어는 공격할 수 없습니다.");

    e.setCancelled(true);
  }

}