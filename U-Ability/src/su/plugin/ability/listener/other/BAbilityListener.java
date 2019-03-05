package su.plugin.ability.listener.other;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.object.Ability;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.ability.api.object.other.BAbility;
import su.plugin.core.common.api.Core;

public class BAbilityListener implements Listener {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		excuteAbility(e, e.getEntity(), 0);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Arrow && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
			excuteAbility(e, e.getEntity(), 93);
			excuteAbility(e, (Player) ((Arrow) e.getDamager()).getShooter(), 92);
		} else if(e.getDamager() instanceof Snowball && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
			excuteAbility(e, e.getEntity(), 95);
			excuteAbility(e, (Player) ((Snowball) e.getDamager()).getShooter(), 94);
		} else if(e.getDamager() instanceof Fireball&& ((Projectile) e.getDamager()).getShooter() instanceof Player) {
			excuteAbility(e, e.getEntity(), 97);
			excuteAbility(e, (Player) ((Fireball) e.getDamager()).getShooter(), 96);
		} else if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(p.getNoDamageTicks() > p.getMaximumNoDamageTicks() / 2F) return;
			excuteAbility(e, e.getDamager(), 9);
		}
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		excuteAbility(e, e.getEntity(), 0);
	}
	
	@EventHandler
	public void onTask(InventoryClickEvent e) {
		if(e.getSlotType() != InventoryType.SlotType.ARMOR) return;
		excuteAbility(e, e.getWhoClicked(), 0);
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		excuteAbility(e, e.getEntity(), 0);
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e) {
		excuteAbility(e, e.getEntity(), 0);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		excuteAbility(e, e.getPlayer(), 0);
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		excuteAbility(e, e.getPlayer(), 0);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		excuteAbility(e, e.getEntity(), 0);
		if(e.getEntity().getKiller() == null) return;
		excuteAbility(e, e.getEntity().getKiller(), 1);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		excuteAbility(e, e.getPlayer(), 0);
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		excuteAbility(e, e.getPlayer(), 0);
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if(e.getEntityType() == EntityType.SNOWBALL) {
			excuteAbility(e, (Player) e.getEntity().getShooter(), 0);
		} else if(e.getEntityType() == EntityType.ARROW) {
			excuteAbility(e, (Player) e.getEntity().getShooter(), 1);
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		excuteAbility(e, e.getPlayer(), 0);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		excuteAbility(e, e.getPlayer(), 0);
	}
	
	public void excuteAbility(Event event, Player player, int data) {
		if(api.getGameManager().getGameState().getProgress() < GameState.PLAYING.getProgress() || api.isInvincibilityTime()) return;
		GamePlayer gp = api.getPlayerManager().getGamePlayer(player);
		if(gp == null || !gp.hasAbility()) return;
		for(Ability ability : gp.getAbilities()) {
			if(!(ability instanceof BAbility)) continue;
			BAbility ba = (BAbility) ability;

			try {
				ba.excute(event, data);
			} catch (Exception e) {
				Core.log(ability.getName() + " / " + ability.getPluginName() + " 에서 오류가 발생했습니다. :" + e.getMessage());
			}
		}
	}
	
	public void excuteAbility(Event event, Entity entity, int data) {
		if(!(entity instanceof Player)) return;

		excuteAbility(event, (Player) entity, data);
	}
	
}