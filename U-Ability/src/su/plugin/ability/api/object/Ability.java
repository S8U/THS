package su.plugin.ability.api.object;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.ClickType;
import su.plugin.ability.api.category.PluginType;
import su.plugin.ability.api.task.ability.CoolDownTask;
import su.plugin.ability.api.task.ability.DurationTask;
import su.plugin.ability.api.task.ability.EffectTask;
import su.plugin.ability.listener.AbilityListener;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.UnregisterableListener;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.StringUtil;

public class Ability implements Cloneable, UnregisterableListener {
	
	protected AbilityAPI api = AbilityPlugin.getApi();

	@Setter
	@Getter
	protected PlayerKey playerKey;

	@Getter
	protected String name, pluginName = PluginType.DEFAULT.getText();
	
	@Getter
	protected String[] manual = null;
	
	@Getter
	protected final int abilityId;
	
	@Setter
	@Getter
	protected int coolTime, durationTime, remainingCoolTime, remainingDurationTime;
	
	@Setter
	@Getter
	protected CoolDownTask coolDownTask;
	
	@Setter
	@Getter
	protected DurationTask durationTask;
	
	@Setter
	@Getter
	protected EffectTask effectTask;
	
	@Setter
	@Getter
	protected boolean enable = false;
	
	@Setter
	@Getter
	protected AbilityType type = null;
	
	@Setter
	@Getter
	protected AbilityRank rank = null;
	
	public Ability() {
		this.abilityId = api.getAbilityManager().getAbilities().size();
	}

	public String getPluginName() {
		return ChatColor.stripColor(pluginName);
	}
	
	public void onUseCastingItem(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {}
	public void onCoolDownStart() {}
	public void onCoolDownEnd() {}
	public void onDurationStart() {}
	public void onDurationEnd() {}
	public void onEffect() {}
	public void onAssign() {}
	public void onResign() {}
	
	public void initAbility(String abilityName, PluginType pluginType, AbilityType type, AbilityRank rank, String... manual) {
		initAbility(abilityName, pluginType.getText(), type, rank, manual);
	}
	
	public void initAbility(String abilityName, String pluginName, AbilityType type, AbilityRank rank, String... manual) {
		this.pluginName = pluginName;
		initAbility(abilityName, type, rank, manual);
	}
	
	public void initAbility(String abilityName, AbilityType type, AbilityRank rank, String... manual) {
		this.name = abilityName;
		this.type = type;
		this.rank = rank;
		this.manual = manual;
	}
	
	public void registerLeftClick(ItemStack[] items) {
		AbilityListener.getLeftEvents().put(getAbilityId(), items);
	}
	
	public void registerRightClick(ItemStack[] items) {
		AbilityListener.getRightEvents().put(getAbilityId(), items);
	}
	
	public boolean canPlay() {
		return name != null && abilityId != -1 && type != null && rank != null && manual != null && !isBlackListed();
	}
	
	public void setPlayer(Player p) {
		setPlayerKey(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public Player getPlayer() {
		if(playerKey == null) return null;
		return (Player) playerKey.getPlatformPlayer();
	}
	
	public GamePlayer getGamePlayer() {
		return api.getPlayerManager().getGamePlayer(playerKey);
	}
	
	public boolean isBlackListed() {
		return api.getAbilityManager().isBlackListed(this);
	}
	
	public void runCoolDownTask() {
		setRemainingCoolTime(getCoolTime());
		if(coolDownTask != null && coolDownTask.getTaskId() != -1) return;
		coolDownTask = new CoolDownTask(this);
		onCoolDownStart();
		coolDownTask.runTaskTimerAsynchronously(0, 20);
	}
	
	public void stopCoolDownTask() {
		if(coolDownTask == null || coolDownTask.getTaskId() == -1) return;
		coolDownTask.cancel();
		coolDownTask = null;
	}
	
	public void runDurationTask() {
		setRemainingDurationTime(getDurationTime());
		if(durationTask != null && durationTask.getTaskId() != -1) return;
		durationTask = new DurationTask(this);
		onDurationStart();
		durationTask.runTaskTimerAsynchronously(0, 20);
	}
	
	public void stopDurationTask() {
		if(durationTask == null || durationTask.getTaskId() == -1) return;
		durationTask.cancel();
		durationTask = null;
	}
	
	public void startEffectTask(int tick) {
		if(effectTask != null && effectTask.getTaskId() != -1) return;
		effectTask = new EffectTask(this);
		effectTask.runTaskTimer(0, tick);
	}
	
	public void stopEffectTask() {
		if(effectTask == null || effectTask.getTaskId() == -1) return;
		effectTask.cancel();
		effectTask = null;
	}
	
	public void excute(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {
		if(api.isInvincibilityTime()) return;
		if(getRemainingDurationTime() > 0) {
			Core.cmsg(getPlayer(), ChatColor.RED, (getGamePlayer().getAbilities().size() < 2 ? "" : name + " ") + "§c능력 지속 종료 시간까지 §f" + StringUtil.buildTimeString(getRemainingDurationTime()) + " §c남았습니다.");
			return;
		} else if(getRemainingCoolTime() > 0) {
			Core.cmsg(getPlayer(), ChatColor.GOLD, (getGamePlayer().getAbilities().size() < 2 ? "" : name + " ") + "§e능력 사용 가능 시간까지 §f" + StringUtil.buildTimeString(getRemainingCoolTime()) + " §e남았습니다.");
			return;
		}
		onUseCastingItem(e, castingItem, clickType);
		if(type.equals(AbilityType.ACTIVE_CONTINUE)) {
			runDurationTask();
			onDurationStart();
		} else {
			runCoolDownTask();
			onCoolDownStart();
		}
		
		if(getType() != AbilityType.PASSIVE) {
			Core.msg(getPlayer(), ChatColor.GOLD, (getGamePlayer().getAbilities().size() < 2 ? "" : name + " ") + "§e능력을 사용했습니다.");
		}
	}
	
	public Ability clone() {
		try {
			Ability ab = (Ability) super.clone();
			if(ab instanceof Listener) {
				Bukkit.getPluginManager().registerEvents((Listener) ab, AbilityPlugin.getInstance());
			}
			return ab;
		} catch(CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}