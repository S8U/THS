package su.plugin.ability.api.object.other;

import Xeon.VisualAbility.MainModule.AbilityBase;
import Xeon.VisualAbility.MainModule.EventManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.PluginType;
import su.plugin.ability.api.object.Ability;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

public class BAbility extends Ability {
	
	@Getter
	private AbilityBase abilityBase;
	
	@SuppressWarnings("incomplete-switch")
	public BAbility(AbilityBase ability) {
		abilityBase = ability;
		AbilityType type = AbilityType.PASSIVE;
		switch(ability.GetAbilityType()) {
		case Active_Continue:
			type = AbilityType.ACTIVE_CONTINUE; break;
		case Active_Immediately:
			type = AbilityType.ACTIVE; break;
		}
		AbilityRank rank = null;
		switch(ability.GetRank()) {
		case GOD:
			rank = AbilityRank.S; break;
		case SS:
			rank = AbilityRank.A; break;
		case S:
			rank = AbilityRank.B; break;
		case A:
			rank = AbilityRank.C; break;
		case B:
			rank = AbilityRank.D; break;
		case C:
			rank = AbilityRank.E; break;
		case D:
			rank = AbilityRank.F; break;
		}
		initAbility(ability.GetAbilityName(), PluginType.BITABILITY, type, rank, ability.GetGuide());
		setCoolTime(ability.GetCoolDown());
		setDurationTime(ability.GetDuration());
	}
	
	@Override
	public void setPlayer(Player p) {
		if(p == null) return;

		super.setPlayer(p);

		abilityBase.SetPlayer(p, false);
	}
	
	@Override
	public void onCoolDownStart() {
		abilityBase.A_CoolDownStart();
	}
	
	@Override
	public void onCoolDownEnd() {
		abilityBase.A_CoolDownEnd();
	}
	
	@Override
	public void onDurationStart() {
		abilityBase.A_DurationStart();
	}
	
	@Override
	public void onDurationEnd() {
		abilityBase.A_DurationEnd();
		abilityBase.A_FinalDurationEnd();
	}
	
	public void excute(Event event, int data) {
		if(EventManager.DamageGuard = api.isInvincibilityTime()) return;
		int cd = abilityBase.A_Condition(event, data);
		if(cd == -1 || cd == -2) return;
		if(getRemainingDurationTime() > 0) {
			Core.cmsg(getPlayer(), ChatColor.RED, (getGamePlayer().getAbilities().size() < 2 ? "" : getName() + " ") + "§c능력 지속 종료 시간까지 §f" + StringUtil.buildTimeString(getRemainingDurationTime() * 1000) + " §c남았습니다.");
			return;
		} else if(getRemainingCoolTime() > 0) {
			Core.cmsg(getPlayer(), ChatColor.GOLD, (getGamePlayer().getAbilities().size() < 2 ? "" : getName() + " ") + "§e능력 사용 가능 시간까지 §f" + StringUtil.buildTimeString(getRemainingCoolTime() * 1000) + " §e남았습니다.");
			return;
		} else if(type.equals(AbilityType.ACTIVE_CONTINUE)) {
			runDurationTask();
		} else {
			if(type.equals(AbilityType.ACTIVE)) {
				runCoolDownTask();
			} 
			abilityBase.A_Effect(event, cd);
		}
		
		if(getType() != AbilityType.PASSIVE) {
			Core.cmsg(getPlayer(), ChatColor.GOLD, (getGamePlayer().getAbilities().size() < 2 ? "" : getName() + " ") + "§e능력을 사용했습니다.");
		}
	}
	
}