package su.plugin.ability.api.object.other;

import daybreak.abilitywar.ability.AbilityBase;
import lombok.Getter;
import su.plugin.ability.api.object.Ability;

public class AWAbility extends Ability {

  @Getter
  private AbilityBase abilityBase;

  public AWAbility(AbilityBase abilityBase) {
    // initAbility(abilityBase.getName(), PluginType.ABILITY_WAR, null, );
  }

}