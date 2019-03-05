package su.plugin.ability.listener.other;

import Physical.Fighters.AbilityList.Time;
import Physical.Fighters.MainModule.AbilityBase;
import Physical.Fighters.MainModule.EventManager;
import Physical.Fighters.MinerModule.EventData;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.object.Ability;
import su.plugin.ability.api.object.other.PAbility;
import su.plugin.core.common.api.Core;

public class PAbilityListener implements Listener {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		excuteAbility(e, EventManager.onEntityDamage);
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		excuteAbility(e, EventManager.onEntityDamageByEntity);
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		excuteAbility(e, EventManager.onEntityTarget);
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		excuteAbility(e, EventManager.onFoodLevelChange);
	}
	
	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e) {
		excuteAbility(e, EventManager.onEntityRegainHealth);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		excuteAbility(e, EventManager.onBlockPlaceEvent);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		excuteAbility(e, EventManager.onBlockBreakEvent);
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		excuteAbility(e, EventManager.onSignChangeEvent);
	}

	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
		excuteAbility(e, EventManager.onPlayerToggleSneakEvent);
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		excuteAbility(e, EventManager.onProjectileLaunchEvent);
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		excuteAbility(e, EventManager.onPlayerPickupItem);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		excuteAbility(e, EventManager.onPlayerRespawn);
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		excuteAbility(e, EventManager.onEntityDeath);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		excuteAbility(e);
		excuteAbility(e, EventManager.onPlayerInteract);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		excuteAbility(e, EventManager.onPlayerMoveEvent);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		excuteAbility(e, EventManager.onProjectileHitEvent);
	}
	
	public void excuteAbility(Event event, List<EventData> ed) {
		if(api.getGameManager().getGameState().getProgress() < GameState.PLAYING.getProgress() || api.isInvincibilityTime()) return;

		boolean b = false;
		for(EventData eData : ed) {
			for(PAbility ability : getAbilities(eData.ab)) {
				if(ability.getType().equals(AbilityType.ACTIVE_CONTINUE)) {
					if(eData.ab.GetPlayer() != null && ability.getDurationTask().getTaskId() != -1) {
						if(ability.getAbilityBase() instanceof Time) {
							PlayerMoveEvent e = (PlayerMoveEvent) event;
							if(!ability.getPlayer().equals(e.getPlayer())) {
								e.setTo(e.getFrom());
							}
						} else {
							eData.ab.A_Effect(event, 0);
						}
						b = true;
						continue;
					}
				} else if(ability.excute(event, eData.parameter)) {
					b = true;
					continue;
				}
			}
			if(b) return;
		}
	}
	
	public void excuteAbility(PlayerInteractEvent e) {
		if(api.getGameManager().getGameState().getProgress() < GameState.PLAYING.getProgress() || api.isInvincibilityTime()) return;

		boolean excuted = false;

		if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			for(AbilityBase ab : EventManager.LeftHandEvent) {
				List<PAbility> abs = getAbilities(ab);

				for(PAbility ability : abs) {
					try {
						ability.excute(e, 0);
					} catch (Exception ex) {
						Core.log(ability.getName() + " / " + ability.getPluginName() + " 에서 오류가 발생했습니다. :" + ex.getMessage());
					}
				}
			}
		} else if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			for(AbilityBase ab : EventManager.RightHandEvent) {
				List<PAbility> abs = getAbilities(ab);

				for(PAbility ability : abs) {
					try {
						ability.excute(e, 1);
					} catch (Exception ex) {
						Core.log(ability.getName() + " / " + ability.getPluginName() + " 에서 오류가 발생했습니다. :" + ex.getMessage());
					}
				}
			}
		}
	}
	
	private List<PAbility> getAbilities(AbilityBase ab) {
		List<PAbility> abilities = new ArrayList<>();

		for(Ability ability : api.getAbilityManager().getAssignedAbilities()) {
			if(!(ability instanceof PAbility)) continue;
			if(ability.getName().equals(ab.GetAbilityName())) {
				abilities.add((PAbility) ability);
			}
		}

		return abilities;
	}
	
	
}