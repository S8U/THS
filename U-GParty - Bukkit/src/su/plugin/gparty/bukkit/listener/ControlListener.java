package su.plugin.gparty.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import su.plugin.gparty.bukkit.KGPartyPlugin;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.bukkit.api.object.KPartyPlayer;
import su.plugin.core.common.api.Core;

public class ControlListener implements Listener {
	
	private KGPartyAPI api = KGPartyPlugin.getApi();
	
	@EventHandler
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent e) {
		if(api.isAllowPartyPVP() || !(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) return;

		KPartyPlayer pp = api.getPlayerManager().getPartyPlayer((Player) e.getDamager());
		KPartyPlayer tp = api.getPlayerManager().getPartyPlayer((Player) e.getEntity());
		if(pp == null || tp == null || !pp.hasParty() || !tp.hasParty() || !pp.getParty().equals(tp.getParty())) return;

		Core.wmsg(pp.getPlayer(), "파티 중인 플레이어는 공격할 수 없습니다.");

		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerDamageByProjectile(EntityDamageByEntityEvent e) {
		if(api.isAllowPartyPVP() || !(e.getDamager() instanceof Projectile) || !(((Projectile) e.getDamager()).getShooter() instanceof Player) || !(e.getEntity() instanceof Player)) return;


		KPartyPlayer pp = api.getPlayerManager().getPartyPlayer((Player) ((Projectile) e.getDamager()).getShooter());
		KPartyPlayer tp = api.getPlayerManager().getPartyPlayer((Player) e.getEntity());
		if(pp == null || tp == null || !pp.hasParty() || !tp.hasParty() || !pp.getParty().equals(tp.getParty())) return;

		Core.wmsg(pp.getPlayer(), "파티 중인 플레이어는 공격할 수 없습니다!");

		e.setCancelled(true);
	}
	
}