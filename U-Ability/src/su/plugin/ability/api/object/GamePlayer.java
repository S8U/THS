package su.plugin.ability.api.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.category.KillType;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.gui.SideBar;
import su.plugin.core.bukkit.api.lib.VaultHandler;
import su.plugin.core.bukkit.api.player.KPlayer;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.permission.api.PermissionAPI;

@RequiredArgsConstructor
@Getter
public class GamePlayer {
	
	private final PlayerKey playerKey;
	
	@Setter
	private int redrawCount;
	
	@Setter
	private long lastKillTime;
	
	@Setter
	private boolean online, join, eliminate, reconnectEliminate, reconnectEliminateMessage, rangeSelectMode, watchMode;
	private boolean hide, fly;

	@Setter
	@Getter
	private SideBar sideBar;
	
	@Setter
	private KillType lastKillType;
	
	@Setter
	private List<Ability> abilities = new ArrayList<>();
	
	@Setter
	private HashMap<PlayerKey, Long> lastHitTimes = new HashMap<>();

	public GamePlayer(Player player) {
		playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(player);
	}

	public String getName() {
		return playerKey.getName();
	}

	public Player getPlayer() {
		return (Player) playerKey.getPlatformPlayer();
	}

	public KPlayer getKPlayer() {
		return (KPlayer) playerKey.getUPlayer();
	}

	public String getDisplayName() {
		return playerKey.getDisplayName();
	}

	public void toggleFly(boolean fly) {
		if(!isOnline()) return;
		
		Bukkit.getScheduler().runTask(AbilityPlugin.getInstance(), () -> {
			getPlayer().setAllowFlight(fly);
			getPlayer().setFlying(fly);
		});
		
		this.fly = fly;
	}
	
	public void addAbility(int abilityId) {
		addAbility(AbilityPlugin.getApi().getAbilityManager().getAbility(abilityId));
	}
	
	public void addAbility(Ability ability) {
		if(hasAbility(ability)) return;
		Ability ac = ability.clone();
		try {
			ac.setPlayer(getPlayer());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ac.onAssign();
			abilities.add(ac);
		}
	}
	
	public void removeAbility(Ability ability) {
		if(ability == null) return;
		ability = getAbility(ability.getAbilityId());
		ability.stopCoolDownTask();
		ability.stopDurationTask();
		ability.stopEffectTask();
		ability.onResign();
		ability.setPlayer(null);
		if(ability instanceof Listener) {
			HandlerList.unregisterAll((Listener) ability);
		}
		abilities.remove(ability);
	}
	
	public void removeAbility(int abilityId) {
		removeAbility(getAbility(abilityId));
	}
	
	public void clearAbility() {
		if(!hasAbility()) return;
		Iterator<Ability> it = abilities.iterator();
		while(it.hasNext()) {
			Ability ability = it.next();
			ability.stopCoolDownTask();
			ability.stopDurationTask();
			ability.stopEffectTask();
			ability.onResign();
			ability.setPlayer(null);

			if(ability instanceof Listener) {
				HandlerList.unregisterAll((Listener) ability);
			}
			ability = null;
			it.remove();
		}
	}

	public boolean hasAbility() {
		return abilities.size() > 0;
	}
	
	public boolean hasAbility(Ability ability) {
		return hasAbility(ability.getAbilityId());
	}
	
	public boolean hasAbility(int abilityId) {
		for(Ability ability : abilities) {
			if(ability.getAbilityId() == abilityId) return true;
		}
		return false;
	}
	
	public Ability getAbility(int abilityId) {
		for(Ability ability : abilities) {
			if(ability.getAbilityId() == abilityId) return ability;
		}
		return null;
	}
	
	public List<Ability> getPassiveAbilities() {
		List<Ability> ab = new ArrayList<>();
		
		for(Ability ability : abilities) {
			if(ability.getType() != AbilityType.PASSIVE) continue;
			ab.add(ability);
		}
		
		return ab;
	}
	
	public List<Ability> getActiveAbilities() {
		List<Ability> ab = new ArrayList<>();
		
		for(Ability ability : abilities) {
			if(ability.getType() == AbilityType.PASSIVE) continue;
			ab.add(ability);
		}
		
		return ab;
	}
	
	public String getAbilitiesNames() {
		StringBuilder sb = new StringBuilder();
		getAbilities().forEach((ability) -> sb.append(sb.length() < 1 ? ability.getName() : ", " + ability.getName()) );
		return sb.toString();
	}
	
	public void toggleWatchMode(boolean toggle, boolean msg) {
		AbilityAPI api = AbilityPlugin.getApi();
		
		Bukkit.getScheduler().runTask(AbilityPlugin.getInstance(), () -> {
			if(getPlayer() == null) return;
			else if(toggle) {
				if(join && !watchMode && !eliminate && getPlayer().getHealth() < api.getQuitDeathHealth() && api.getGameManager().getGameState() != GameState.END) {
					getPlayer().setHealth(0);

					Core.cbc(ChatColor.DARK_RED, "§f" + getDisplayName() + " §c님께서 낮은 체력으로 관전 모드로 전환하여 탈락 처리되었습니다.");
				}
				
				clearAbility();

				watchMode = true;
				join = false;

				redrawCount = 0;

				getKPlayer().hidePlayer();
				getPlayer().spigot().setCollidesWithEntities(false);
				toggleFly(true);
				
				getPlayer().setGameMode(GameMode.ADVENTURE);
				getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 1, true, true));

				getPlayer().setLevel(0);
				clearInventory();
				
				if(api.isUseWatchModeQuickBar()) {
					api.getGUIManager().updateTeleportGUI();
					api.getBarManager().getWatchModeQuickBar().update();
					Bukkit.getScheduler().runTaskLater(AbilityPlugin.getInstance(), () -> api.getBarManager().getWatchModeQuickBar().setTo(getPlayer()), 1);
				}

				if(api.isUseSideBar()) {
					api.getBarManager().updateSideBarAllPlayer();
				}
				
				if(msg) {
					Core.cbc(ChatColor.BLUE, getDisplayName() + " §b님께서 관전 모드로 전환했습니다.");
				}
				return;
			}
			
			watchMode = false;
			join = true;

			getKPlayer().showPlayer();
			getPlayer().spigot().setCollidesWithEntities(true);
			toggleFly(false);
			
			getPlayer().setGameMode(Bukkit.getDefaultGameMode());
			getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);

			getPlayer().setLevel(0);
			clearInventory();

			if(api.isUseWatchModeQuickBar()) {
				api.getGUIManager().updateTeleportGUI();
				KCore.getGUIManager().clearQuickBar(getPlayer());
			}
			
			if(api.getGameManager().getGameState() == GameState.WAITING) {
				if(api.isUseWaitingQuickBar()) {
					Bukkit.getScheduler().runTaskLater(AbilityPlugin.getInstance(), () -> api.getBarManager().getWaitingQuickBar().setTo(getPlayer()), 1);
				}
				if(api.isUseSideBar()) {
					api.getBarManager().updateSideBarAllPlayer();
				}
			}
			
			if(msg) {
				Core.cbc(ChatColor.BLUE, getDisplayName() + " §b님께서 관전 모드를 해제했습니다.");
			}
		});
	}
	
	public void clearInventory() {
		if(getPlayer() == null) return;
		
		Bukkit.getScheduler().runTask(AbilityPlugin.getInstance(), () -> {
			getPlayer().getInventory().setHelmet(null);
			getPlayer().getInventory().setChestplate(null);
			getPlayer().getInventory().setLeggings(null);
			getPlayer().getInventory().setBoots(null);
			
			getPlayer().getInventory().clear();
			
			getPlayer().updateInventory();
		});
	}

	public String getRank() {
		if(AbilityAPI.isUsePermission()) {
			return PermissionAPI.getPlayerManager().getPermissionPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(getPlayer())).getGroupName();
		}

		return VaultHandler.getChat().getPlayerGroups(getPlayer())[0];
	}
	
}